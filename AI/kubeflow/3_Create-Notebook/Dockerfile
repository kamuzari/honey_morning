FROM kubeflownotebookswg/jupyter-pytorch-cuda-full:v1.8.0
USER root 

# Nvidia GPG Public Key 교체
RUN rm -f /etc/apt/sources.list.d/cuda.list \
  && rm -f /etc/apt/sources.list.d/nvidia-ml.list

ENV JAVA_HOME /usr/lib/jvm/java-1.7-openjdk/jre

RUN apt-get update && apt-get install -y --no-install-recommends \
	  sudo \
	  apt-utils \
    python3-pip \
    python3-dev \
    python3-setuptools\
    g++\
    curl\
    git\
    default-jdk\
    make\
  && apt-get clean && \
  rm -rf /var/lib/apt/lists/* \
  && usermod -aG sudo ${NB_USER} \
  && echo ${NB_USER}:${NB_USER} | chpasswd \
  && echo "${NB_USER} ALL=(root) NOPASSWD:SETENV: /init" >> /etc/sudoers
# jovyan 의 비밀번호는 jovyan으로 설정됩니다.

RUN python3 -m pip install --upgrade pip

RUN pip3 --no-cache-dir install \
  tensorflow\
  bertopic\
  bertopic[visualization]\
  konlpy\
  tf-keras\
  mecab-python3\
  JPype1-py3

RUN curl -L https://raw.githubusercontent.com/konlpy/konlpy/master/scripts/mecab.sh | bash

EXPOSE 8888
EXPOSE 8889
USER jovyan

ENV NB_USER $NB_USER
ENV NB_UID=1000
ENV HOME /home/$NB_USER
ENV NB_PREFIX /

CMD ["sh","-c", "jupyter notebook --notebook-dir=/home/jovyan --ip=0.0.0.0 --no-browser --allow-root --port=8888 --NotebookApp.token='' --NotebookApp.password='' --NotebookApp.allow_origin='*' --NotebookApp.base_url=${NB_PREFIX} --NotebookApp.max_buffer_size=100000000000000000000000000000000000000000000000000000000000000000000000000000"]