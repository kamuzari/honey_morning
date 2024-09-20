#!/bin/bash
# init k8s

#/etc/sysctl.conf를 열어 net.ipv4.ip_forward=1행의 주석을 제거
sudo sysctl -p

#/etc/containerd/config.toml 에서
# [plugins]
#   [plugins."io.containerd.grpc.v1.cri"]
#     [plugins."io.containerd.grpc.v1.cri".containerd]
#       default_runtime_name = "nvidia" ## 이거 설정
# sudo systemctl restart docker


## 밑에 kubeadm init 에러나면
# sudo mv /etc/kubernetes/manifests/kube-apiserver.yaml
# /etc/kubernetes/manifests/kube-controller-manager.yaml
# /etc/kubernetes/manifests/kube-scheduler.yaml
# /etc/kubernetes/manifests/etcd.yaml

sudo swapoff -a

sudo kubeadm init phase kubelet-start

sudo kubeadm init --pod-network-cidr=10.217.0.0/16

mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config

kubectl cluster-info

# CNI

CILIUM_CLI_VERSION=$(curl -s https://raw.githubusercontent.com/cilium/cilium-cli/main/stable.txt)
CLI_ARCH=amd64
if [ "$(uname -m)" = "aarch64" ]; then CLI_ARCH=arm64; fi
sudo curl -L --fail --remote-name-all https://github.com/cilium/cilium-cli/releases/download/${CILIUM_CLI_VERSION}/cilium-linux-${CLI_ARCH}.tar.gz{,.sha256sum}
sha256sum --check cilium-linux-${CLI_ARCH}.tar.gz.sha256sum
sudo tar xzvfC cilium-linux-${CLI_ARCH}.tar.gz /usr/local/bin
rm cilium-linux-${CLI_ARCH}.tar.gz{,.sha256sum}

cilium install --version 1.16.1

kubectl get pods -n kube-system --selector=k8s-app=cilium

kubectl taint nodes --all node-role.kubernetes.io/control-plane:NoSchedule-

kubectl create -f https://raw.githubusercontent.com/NVIDIA/k8s-device-plugin/v0.16.1/deployments/static/nvidia-device-plugin.yml


#test GPU
kubectl -n kube-system get pod -l name=nvidia-device-plugin-ds
kubectl -n kube-system logs  -l name=nvidia-device-plugin-ds

# default storageclass
kubectl apply -f https://raw.githubusercontent.com/rancher/local-path-provisioner/master/deploy/local-path-storage.yaml
kubectl get storageclass
kubectl patch storageclass local-path -p '{"metadata": {"annotations":{"storageclass.kubernetes.io/is-default-class":"true"}}}'
kubectl get sc

# install kusomize
#

sudo curl -s "https://raw.githubusercontent.com/kubernetes-sigs/kustomize/master/hack/install_kustomize.sh"  | bash

export PATH=$PATH:$HOME
export PATH=$PATH:$HOME/kustomize

sudo chmod +x $HOME/kustomize