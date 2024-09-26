FROM python:3.8.6

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends \
    openjdk-11-jdk-headless \
    openjdk-11-jre-headless \
    && apt-get install -y --no-install-recommends \
    ca-certificates-java \
    && apt-get install -y --no-install-recommends \
    openjdk-11-jdk \
    openjdk-11-jre \
    && dpkg --configure -a \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
# ENV PATH="$JAVA_HOME/bin:$PATH"

COPY requirements.txt ./
RUN --mount=type=cache,target=/root/.cache/pip \
    pip install -r requirements.txt --no-cache-dir

COPY . .

EXPOSE 8003
CMD [ "uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8003"]

