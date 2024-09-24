FROM python:3.8.6-slim-buster

WORKDIR /app

RUN apt-get update && apt-get install -y git && rm -rf /var/lib/apt/lists/* && mkdir /var/data

COPY requirements.txt ./
RUN --mount=type=cache,target=/root/.cache/pip \
    pip install -r requirements.txt --no-cache-dir

COPY . .

EXPOSE 8000
CMD [ "uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8000" ]
