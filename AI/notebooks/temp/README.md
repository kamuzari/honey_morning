## 환경 정리

- GPU 환경
    - CUDA: 11.8
    - CuDNN: 8.6

- Python 환경
    - python 3.10
    - conda(latest version: 24.7.1)
    - conda 가상환경(temp-honey)
    - `conda env create -f temp-honey.yml` bash 명령어로 동일한 가상환경 생성 가능

- Jupyter Kernel 환경
    - 로컬에서 `python -m ipykernel install --user --name temp-honey --display-name "Python temp-honey"` bash 명령어로 kernel 생성 후 브라우저 환경에서 가상 환경 kernel(Python temp-honey 선택)로 작업 가능


- Pytorch 관련
    - `conda install pytorch==2.4.0 torchvision==0.19.0 torchaudio==2.4.0  pytorch-cuda=11.8 -c pytorch -c nvidia`


