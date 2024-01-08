package com.example.helloworld.services;

import com.example.helloworld.models.Session;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.apache.flink.kubernetes.operator.api.FlinkDeployment;
import org.apache.flink.kubernetes.operator.api.FlinkDeploymentList;
import org.apache.flink.kubernetes.operator.api.lifecycle.ResourceLifecycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class FlinkService {
    private static final Logger log = LoggerFactory.getLogger(FlinkService.class);

    public Session createFlinkSessionCluster(String id) {
        String flinkDeploymentYaml = templateFlinkDeployment(id);
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

    String templateFlinkDeployment(String appName) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("appName", appName);

        return processTemplate("flink-deployment-template.yaml.ftl", templateData);
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
}
