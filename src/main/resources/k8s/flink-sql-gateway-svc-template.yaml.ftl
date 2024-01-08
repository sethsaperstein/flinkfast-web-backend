apiVersion: v1
kind: Service
metadata:
  name: ${appName}-sql-gateway
  namespace: default
  labels:
    app: ${appName}-sql-gateway
spec:
  selector:
    app: ${appName}
    component: jobmanager
    type: flink-native-kubernetes
  type: ClusterIP
  ports:
    - name: rest
      protocol: TCP
      port: 8083
      targetPort: 8083
