package com.example.helloworld.services;

import com.example.helloworld.Settings;
import com.example.helloworld.models.Jobs;
import com.example.helloworld.models.Session;
import com.example.helloworld.utils.TimeUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import jakarta.validation.constraints.NotNull;
import org.apache.flink.kubernetes.operator.api.FlinkDeployment;
import org.apache.flink.kubernetes.operator.api.FlinkDeploymentList;
import org.apache.flink.kubernetes.operator.api.lifecycle.ResourceLifecycleState;
import org.apache.flink.kubernetes.operator.api.spec.FlinkVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;

@Service
public class FlinkService {
    private static final Logger log = LoggerFactory.getLogger(FlinkService.class);

    public Session createFlinkSessionCluster(String id) {
        boolean isLocal = Settings.getEnvironment().equals(Settings.LOCAL_ENV);
        String flinkDeploymentYaml = templateFlinkDeployment(id, isLocal);
        InputStream flinkDeploymentBytes = new ByteArrayInputStream(flinkDeploymentYaml.getBytes());

        String sqlGatewayServiceYaml = templateSqlGatewayService(id);
        InputStream sqlGatewayServiceBytes = new ByteArrayInputStream(sqlGatewayServiceYaml.getBytes());


        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            MixedOperation<FlinkDeployment, FlinkDeploymentList, Resource<FlinkDeployment>> flinkClient =
                client.resources(FlinkDeployment.class, FlinkDeploymentList.class);

            // Flink Deployment
            if (flinkClient.withName(id).get() != null) {
                log.info("Flink deployment already exists: {}", id);
                return Session.from(id, "PENDING");
            }
            log.info("Attempting to create flink deployment: {}", id);
            flinkClient.load(flinkDeploymentBytes).create();

            // SQL Gateway Service
            log.info("Attempting to create SQL gateway service: {}", id);
            client.services().load(sqlGatewayServiceBytes).create();
        }
        return Session.from(id, "PENDING");
    }

    String templateSqlGatewayService(String appName) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("appName", appName);

        return processTemplate("flink-sql-gateway-svc-template.yaml.ftl", templateData);
    }

    String templateFlinkDeployment(String appName, boolean isLocal) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("appName", appName);
        templateData.put("isLocalEnvironment", isLocal);

        return processTemplate("flink-deployment-template.yaml.ftl", templateData);
    }

    String templateFlinkJobDeployment(String appName, String sql, boolean isLocal) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("appName", appName);
        templateData.put("isLocalEnvironment", isLocal);
        templateData.put("sql", sql);

        return processTemplate("flink-job-deployment-template.yaml.ftl", templateData);
    }

    String templateFlinkJobConfigMap(String appName, String sql, boolean isLocal) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("appName", appName);
        templateData.put("isLocalEnvironment", isLocal);
        templateData.put("sql", sql);

        return processTemplate("flink-job-sql-cm-template.yaml.ftl", templateData);
    }

    private static String processTemplate(String templateName, Map<String, Object> data) {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
            cfg.setClassForTemplateLoading(FlinkService.class, "/k8s/");
            Template template = cfg.getTemplate(templateName);

            StringWriter writer = new StringWriter();
            template.process(data, writer);
            return writer.toString();
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Optional<Session> getSession(String id) {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            MixedOperation<FlinkDeployment, FlinkDeploymentList, Resource<FlinkDeployment>> flinkClient =
                client.resources(FlinkDeployment.class, FlinkDeploymentList.class);

            Resource<FlinkDeployment> flinkDeploymentResource = flinkClient.withName(id);
            if (flinkDeploymentResource.get() == null) {
                log.info("Flink deployment {} does not exist", id);
                return Optional.empty();
            }

            FlinkDeployment flinkDeployment = flinkDeploymentResource.get();
            log.info("Flink deployment {} lifecycle status: {}", id, flinkDeployment.getStatus().getLifecycleState());
            if (flinkDeployment.getStatus().getLifecycleState() == ResourceLifecycleState.STABLE) {
                return Optional.of(Session.from(id, "RUNNING"));
            }
            return Optional.of(Session.from(id, "PENDING"));
        }
    }

    public Jobs getJobs() {
        List<Jobs.Job> jobs = new ArrayList<>();
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            MixedOperation<FlinkDeployment, FlinkDeploymentList, Resource<FlinkDeployment>> flinkClient =
                client.resources(FlinkDeployment.class, FlinkDeploymentList.class);

            LabelSelector labelSelector = new LabelSelectorBuilder()
                .addToMatchLabels("type", "job")
                .build();
            FlinkDeploymentList flinkDeployments = flinkClient.inAnyNamespace().withLabelSelector(labelSelector).list();

            for (FlinkDeployment flinkDeployment : flinkDeployments.getItems()) {
                String name = flinkDeployment.getMetadata().getName();
                String createdAt = flinkDeployment.getMetadata().getCreationTimestamp();
                String state = flinkDeployment.getStatus().getJobStatus().getState();
                String targetState = flinkDeployment.getSpec().getJob().getState().name();
                jobs.add(Jobs.Job.from(name, 1, createdAt, state, targetState));
            }
        }
        return Jobs.from(jobs);
    }

    public void deleteJob(String jobName, Integer version) {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            MixedOperation<FlinkDeployment, FlinkDeploymentList, Resource<FlinkDeployment>> flinkClient =
                client.resources(FlinkDeployment.class, FlinkDeploymentList.class);

            flinkClient.withName(jobName).delete();
        }
    }

    public Jobs.Job createFlinkJobCluster(String user, @NotNull String sql) {
        String jobName = String.format("sql-%s", UUID.randomUUID().toString());
        boolean isLocal = Settings.getEnvironment().equals(Settings.LOCAL_ENV);

        String flinkDeploymentYaml = templateFlinkJobDeployment(jobName, sql, isLocal);
        InputStream flinkDeploymentBytes = new ByteArrayInputStream(flinkDeploymentYaml.getBytes());

        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            MixedOperation<FlinkDeployment, FlinkDeploymentList, Resource<FlinkDeployment>> flinkClient =
                client.resources(FlinkDeployment.class, FlinkDeploymentList.class);

            log.info("Attempting to create flink configmap for job {}", jobName);
            // TODO: move to template. issue with indentation in template for multi-line SQL

            String cmName = String.format("sql-scripts-%s", jobName);
            ConfigMap cm = new ConfigMapBuilder()
                .withNewMetadata()
                    .withName(cmName)
                .endMetadata()
                .addToData("script.sql", sql)
                .build();
            client.resource(cm).create();


            log.info("Attempting to create flink deployment {} for user {}", jobName, user);
            FlinkDeployment flinkDeployment = flinkClient.load(flinkDeploymentBytes).create();

            // cascade delete the cm on flinkdeployment delete
            ConfigMap updatedCm = client.configMaps().withName(cmName).get();
            OwnerReference ownerReference = new OwnerReferenceBuilder()
                .withApiVersion("flink.apache.org/v1beta1")
                .withKind("FlinkDeployment")
                .withName(flinkDeployment.getMetadata().getName())
                .withUid(flinkDeployment.getMetadata().getUid())
                .build();
            updatedCm.getMetadata().setOwnerReferences(List.of(ownerReference));
            client.resource(updatedCm).update();
        }
        return Jobs.Job.from(jobName, 1, TimeUtils.getCurrentTimeUTCISO(), "Pending", "Running");
    }
}
