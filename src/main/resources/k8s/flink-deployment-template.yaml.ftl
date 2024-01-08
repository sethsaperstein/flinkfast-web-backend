apiVersion: flink.apache.org/v1beta1
kind: FlinkDeployment
metadata:
  name: ${appName}
spec:
  image: flink_test_5
  flinkVersion: v1_18
  flinkConfiguration:
    taskmanager.numberOfTaskSlots: "2"
  serviceAccount: flink
  jobManager:
    resource:
      memory: "2048m"
      cpu: 1
  taskManager:
    resource:
      memory: "2048m"
      cpu: 1
