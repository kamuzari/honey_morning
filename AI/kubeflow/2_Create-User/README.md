# Create User

```r
# configmap을 수정해 유저를 등록한다.
code ~/manifests/common/dex/base/config-map.yaml
```

```r
# dex 를 지우고
kubectl delete deployments.apps dex -n auth

# 다시 빌드한다.
kustomize build ~/manifests/common/dex/overlays/istio/ | kubectl apply -f -
```
