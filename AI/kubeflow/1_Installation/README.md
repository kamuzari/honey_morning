# Installation

[Kubeflow Essentials](https://www.youtube.com/watch?v=qoqLtrdAXQg&t=177s) 강의를 보고 작성됨

ubuntu 20.04
gpu : rtx 3070

## 1. install cudnn

https://docs.nvidia.com/deeplearning/cudnn/latest/reference/support-matrix.html

```r
sudo apt-get -y update
sudo apt-get -y remove --purge '^nvidia-.\*'
sudo apt-get -y remove --purge 'cuda-.\*'
```

[nvidia cuda toolkit, V.11.8 설치](https://developer.nvidia.com/cuda-11-8-0-download-archive?target_os=Linux&target_arch=x86_64&Distribution=WSL-Ubuntu&target_version=2.0&target_type=deb_local)

```r
nvcc -V
```

---

안 될 시

`~/.bashrc` 에 다음 내용 추가

```r
export PATH=/usr/local/cuda-11.8/bin:$PATH
export LD_LIBRARY_PATH=/usr/local/cuda-11.8/lib64:$LD_LIBRARY_PATH
```

```r
source ~/.bashrc
```

---

```r
whereis cuda
mkdir ~/nvidia
cd ~/nvidia
```

cuDNN v9.3.0

```r
wget https://developer.download.nvidia.com/compute/cudnn/9.3.0/local_installers/cudnn-local-repo-ubuntu2004-9.3.0_1.0-1_amd64.deb
sudo dpkg -i cudnn-local-repo-ubuntu2004-9.3.0_1.0-1_amd64.deb
sudo cp /var/cudnn-local-repo-ubuntu2004-9.3.0/cudnn-*-keyring.gpg /usr/share/keyrings/
sudo apt-get update
sudo apt-get -y install cudnn
```

## 2. install docker

```r
sudo apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release

## key error 나면 PUB_KEY 자리에 KEY 넣어서
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys <PUB_KEY>

sudo install -m 0755 -d /etc/apt/keyrings

sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
# sudo chmod a+r /etc/apt/keyrings/docker.asc

echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt-get update -y
sudo apt-get install -y docker-ce docker-ce-cli containerd.io
sudo docker run hello-world
sudo usermod -aG docker $USER && newgrp docker
sudo service docker restart
```

## 3. install nvidia-docker

```r
distribution=$(. /etc/os-release;echo $ID$VERSION_ID)
curl -s -L https://nvidia.github.io/nvidia-docker/gpgkey | sudo apt-key add -
curl -s -L https://nvidia.github.io/nvidia-docker/$distribution/nvidia-docker.list | sudo tee /etc/apt/sources.list.d/nvidia-docker.list
sudo apt-get -y update
sudo apt-get -y install nvidia-docker2
sudo systemctl restart docker

sudo docker run --rm --runtime=nvidia --gpus all nvidia/cuda:11.8.0-base-ubuntu20.04 nvidia-smi


sudo bash -c 'cat <<EOF > /etc/docker/daemon.json
{
    "exec-opts": ["native.cgroupdriver=systemd"],
    "log-driver": "json-file",
    "log-opts": {
        "max-size": "100m"
    },
    "data-root": "/mnt/storage/docker_data",
    "storage-driver": "overlay2",
    "default-runtime" : "nvidia",
    "runtimes" : {
        "nvidia" : {
            "path": "/usr/bin/nvidia-container-runtime",
            "runtimeArgs" : []
        }
    }
}
EOF'
sudo systemctl restart docker
```

## 4. install k8s

```r
sudo swapoff -a
sudo sed -i '/ swap / s/^\(.*\)$/#\1/g' /etc/fstab

sudo apt-get install -y iptables arptables ebtables
sudo apt-get update && sudo apt-get install -y apt-transport-https curl
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -

## 수작업 nano
cat <<EOF | sudo tee /etc/apt/sources.list.d/kubernetes.list deb https://apt.kubernetes.io/ kubernetes-xenial main EOF

## 여기에
sudo nano /etc/apt/sources.list.d/kubernetes.list

## 이 내용을 쓰라고
deb https://apt.kubernetes.io/ kubernetes-xenial main




sudo apt-get update
# sudo apt-get install -y kubelet=1.21.10-00 kubeadm=1.21.10-00 kubectl=1.21.10-00 --allow-downgrades --allow-change-held-packages

# 더 이상 install안 됨. 도커 지원 안함. 1.29 kubernetes와 containered 써야함  apt-cache policy kubeadm 로 설치할 수 있는 것 확인

wget https://github.com/containerd/containerd/releases/download/v1.7.14/containerd-1.7.14-linux-amd64.tar.gz

sudo apt-get install -y kubelet=1.29.7-1.1 kubeadm=1.29.7-1.1 kubectl=1.29.7-1.1 --allow-downgrades --allow-chang
e-held-packages

curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.29/deb/Release.key | sudo gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg
echo "deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.29/deb/ /" | sudo tee /etc/apt/sources.list.d/kubernetes.list

sudo apt-mark hold kubelet kubeadm kubectl
kubeadm version
kubelet --version
kubectl version --client
```

## 5. init_k8s

```r
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
sudo mv /etc/kubernetes/manifests/kube-apiserver.yaml \
/etc/kubernetes/manifests/kube-controller-manager.yaml \
/etc/kubernetes/manifests/kube-scheduler.yaml \
/etc/kubernetes/manifests/etcd.yaml ./

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

# autocomplete k8s
shellname=`echo $SHELL | rev | cut -d '/' -f1 | rev`
shellconf=`echo ~/.\${shellname}rc`
grep -n "kubectl completion" $shellconf

if [ $? = 1 ]
  then
    echo 'install autocomplete k8s'
    sudo apt-get install bash-completion -y
    echo 'source <(kubectl completion '$shellname')' >>$shellconf
    echo 'alias k=kubectl' >>$shellconf
    echo 'complete -F __start_kubectl k' >>$shellconf
fi
$SHELL
```

## 6. install kubeflow

```r
cd ~
git clone https://github.com/kubeflow/manifests.git
cd manifests/

git checkout v1.8-branch

kustomize build ~/manifests/common/cert-manager/cert-manager/base | kubectl apply -f -
echo "Waiting for cert-manager to be ready ..."
kubectl wait --for=condition=ready pod -l 'app in (cert-manager,webhook)' --timeout=180s -n cert-manager
kubectl wait --for=jsonpath='{.subsets[0].addresses[0].targetRef.kind}'=Pod endpoints -l 'app in (cert-manager,webhook)' --timeout=180s -n cert-manager

while ! kustomize build example | kubectl apply -f -; do echo "Retrying to apply resources"; sleep 20; done

# watch kubectl get pods -A 다 running 될 때 까지 기다려


# 포트 포워딩

# gateway.yaml
# certificate.yaml 적용 해서 443 포트에 포워딩 하면 끝

```

## 7. port forwarding

```r
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.15.3/cert-manager.yaml

kubectl apply -f ./certificate.yaml
kubectl apply -f ./gateway.yaml
```

kubeflow는 기본적으로 https 접근을 지원하기 때문에, http : 80 번 포트로의 접근을 https: 443 포트로 바꿔주고, https 인증을 해주어야 한다.

```yaml
### gateway.yaml

apiVersion: networking.istio.io/v1alpha3
kind: Gateway
metadata:
  name: kubeflow-gateway
  namespace: kubeflow
spec:
  selector:
    istio: ingressgateway
  servers:
    - hosts:
        - "*"
      port:
        name: http
        number: 80
        protocol: HTTP
      # Upgrade HTTP to HTTPS
      tls:
        httpsRedirect: true
    - hosts:
        - "*"
      port:
        name: https
        number: 443
        protocol: HTTPS
      tls:
        mode: SIMPLE
        credentialName: kubeflow-ingressgateway-certs
```

```yaml
### certificate.yaml

apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: kubeflow-ingressgateway-certs
  namespace: istio-system
spec:
  commonName: example.com #Domain name
  issuerRef:
    kind: ClusterIssuer
    name: kubeflow-self-signing-issuer
  secretName: kubeflow-ingressgateway-certs
```

```r
kubectl port-forward --address=0.0.0.0 svc/istio-ingressgateway -n istio-system 8080:443
```

- certificate.yaml 파일의 secretName 이 gateway.yaml 파일의 credentialName 과 일치하는 것을 볼 수 있다. (kubeflow-ingressgateway-certs)

- [istio gateway 흐름](https://do-hansung.tistory.com/85) 에 따르면 트래픽의 흐름은 대략적으로 다음과 같다. 이 블로그에서 TLS 인증 방법에 대해서 잘 설명해주고 있다.

  - `Client` -> `istio-ingressgateway` -> `gateway` -> `virtual service` -> `svc` -> `pod`

- 직접 Key를 생성하고 인증방식을 제대로 구현하면 해당 부분에서 여러 문제가 생긴다.
  - [istio https redirect 설정 상태에서 certmanager 인증서 발급법](https://lcc3108.github.io/articles/2021-04/istio-https-redirect-certmanager-renew)
    - 이 문제는, 모든 http 요청을 https 로 바꿔버리면서 생겼다.
  - [istio gateway tls 404 에러해결](https://minhan2.tistory.com/entry/istio-gateway-tls-404-%EC%97%90%EB%9F%AC%ED%95%B4%EA%B2%B0)
    - 이 문제는 같은 인증서를 다른 곳에서 중복해 사용하면서 생겼다.

## 8. Trouble Shooting

### GPU 인식 안됨

```r
Name:               desktop-mmcqpv1
Roles:              control-plane
Labels:             beta.kubernetes.io/arch=amd64
                    beta.kubernetes.io/os=linux
                    kubernetes.io/arch=amd64
                    kubernetes.io/hostname=desktop-mmcqpv1
                    kubernetes.io/os=linux
                    node-role.kubernetes.io/control-plane=
                    node.kubernetes.io/exclude-from-external-load-balancers=
Annotations:        kubeadm.alpha.kubernetes.io/cri-socket: unix:///var/run/containerd/containerd.sock
                    node.alpha.kubernetes.io/ttl: 0
                    volumes.kubernetes.io/controller-managed-attach-detach: true
CreationTimestamp:  Wed, 28 Aug 2024 16:55:28 +0900
Taints:             <none>
Unschedulable:      false
Lease:
  HolderIdentity:  desktop-mmcqpv1
  AcquireTime:     <unset>
  RenewTime:       Fri, 30 Aug 2024 09:09:16 +0900
Conditions:
  Type                 Status  LastHeartbeatTime                 LastTransitionTime                Reason                       Message
  ----                 ------  -----------------                 ------------------                ------                       -------
  NetworkUnavailable   False   Wed, 28 Aug 2024 16:56:56 +0900   Wed, 28 Aug 2024 16:56:56 +0900   CiliumIsUp                   Cilium is running on this node
  MemoryPressure       False   Fri, 30 Aug 2024 09:09:07 +0900   Fri, 30 Aug 2024 08:58:45 +0900   KubeletHasSufficientMemory   kubelet has sufficient memory available
  DiskPressure         False   Fri, 30 Aug 2024 09:09:07 +0900   Fri, 30 Aug 2024 08:58:45 +0900   KubeletHasNoDiskPressure     kubelet has no disk pressure
  PIDPressure          False   Fri, 30 Aug 2024 09:09:07 +0900   Fri, 30 Aug 2024 08:58:45 +0900   KubeletHasSufficientPID      kubelet has sufficient PID available
  Ready                True    Fri, 30 Aug 2024 09:09:07 +0900   Fri, 30 Aug 2024 09:09:07 +0900   KubeletReady                 kubelet is posting ready status
Addresses:
  InternalIP:  172.26.42.180
  Hostname:    desktop-mmcqpv1
Capacity:
  cpu:                20
  ephemeral-storage:  263112772Ki
  hugepages-1Gi:      0
  hugepages-2Mi:      0
  memory:             16146808Ki
  pods:               110
Allocatable:
  cpu:                20
  ephemeral-storage:  242484730274
  hugepages-1Gi:      0
  hugepages-2Mi:      0
  memory:             16044408Ki
  pods:               110
System Info:
  Machine ID:                 69e3d8f35e3145c983d85a53af2ee225
  System UUID:                69e3d8f35e3145c983d85a53af2ee225
  Boot ID:                    69c1ff54-6252-4c34-a02f-3b91494da184
  Kernel Version:             5.15.153.1-microsoft-standard-WSL2
  OS Image:                   Ubuntu 20.04.6 LTS
  Operating System:           linux
  Architecture:               amd64
  Container Runtime Version:  containerd://1.7.20
  Kubelet Version:            v1.29.7
  Kube-Proxy Version:         v1.29.7
PodCIDR:                      10.217.0.0/24
PodCIDRs:                     10.217.0.0/24
Non-terminated Pods:          (63 in total)
```

`nvidia.com/gpu : 1` 과 같은 항목이 표시되질 않는다. 따라서, Kubeflow 에서 notebook을 만들 때 gpu를 요청했을 때 제대로 생성되질 않는다.

docker desktop 에서 Integration을 끄고, 자체 Docker를 이용하도록 바꾸면서 기존에 실행했던 컨테이너가 실행되지 않고 있었다.

다시 run 시키고, 확인했는데 여전히 nvidia.com/gpu 항목이 없어서 계속해서 찾아보고 있는 중이다.

### 해결!

`etc/containerd/config.toml` 파일에서 다음과 같이 수정했다.

수정 전

```r
[plugins]
  [plugins."io.containerd.grpc.v1.cri"]
    [plugins."io.containerd.grpc.v1.cri".containerd]
      default_runtime_name = "runc"
```

수정 후

```r
[plugins]
  [plugins."io.containerd.grpc.v1.cri"]
    [plugins."io.containerd.grpc.v1.cri".containerd]
      default_runtime_name = "nvidia"
```
