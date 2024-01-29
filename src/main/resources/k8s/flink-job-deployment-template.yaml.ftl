apiVersion: flink.apache.org/v1beta1
kind: FlinkDeployment
metadata:
  name: ${appName}
  labels:
    type: job
spec:
  image: 189266647936.dkr.ecr.us-east-1.amazonaws.com/flinkfast-sql-app:latest
  flinkVersion: v1_18
  flinkConfiguration:
    taskmanager.numberOfTaskSlots: "1"
  serviceAccount: flink
  jobManager:
    resource:
      memory: "2048m"
      cpu: 1
  taskManager:
    resource:
      memory: "2048m"
      cpu: 1
  job:
    jarURI: local:///opt/flink/usrlib/sql-runner.jar
    args: ["/opt/flink/usrlib/sql-scripts/script.sql"]
    parallelism: 1
    upgradeMode: stateless
  podTemplate:
    spec:
      volumes:
        - name: sql-scripts
          configMap:
            name: sql-scripts-${appName}
      containers:
        - name: flink-main-container
          volumeMounts:
            - name: sql-scripts
              mountPath: /opt/flink/usrlib/sql-scripts
