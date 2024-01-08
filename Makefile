operator:
	kubectl create -f https://github.com/jetstack/cert-manager/releases/download/v1.8.2/cert-manager.yaml
	helm repo add flink-operator-repo https://downloads.apache.org/flink/flink-kubernetes-operator-1.7.0/
	helm install flink-kubernetes-operator flink-operator-repo/flink-kubernetes-operator

expose:
	kubectl proxy &
