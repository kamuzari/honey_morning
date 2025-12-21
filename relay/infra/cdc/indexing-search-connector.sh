#!/bin/bash

KAFKA_CONNECT_HOST="${KAFKA_CONNECT_HOST:-localhost}"
KAFKA_CONNECT_PORT="${KAFKA_CONNECT_PORT:-8083}"
KAFKA_CONNECT_URL="http://${KAFKA_CONNECT_HOST}:${KAFKA_CONNECT_PORT}"

echo "커넥터 확인"
sleep 2

curl -X DELETE ${KAFKA_CONNECT_URL}/connectors/mysql-briefing-connector

echo "\n\n\n"

echo "커넥터 등록"
sleep 2

curl -X POST ${KAFKA_CONNECT_URL}/connectors -H "Content-Type: application/json" -d '{
  "name": "to-indexing-briefing-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "database.hostname": "honeymorning-db",
    "database.allowPublicKeyRetrieval": "true",
    "database.port": "3306",
    "database.user": "root",
    "database.password": "root",
    "database.server.id": "184055",
    "database.server.name": "main-database",
    "database.serverTimezone": "Asia/Seoul",
    "database.include.list": "honeymorning",
    "table.include.list": "honeymorning.briefings",
    "include.schema.changes": "false",

    "topic.prefix": "briefing_search",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "transforms": "ExtractField",
    "transforms.ExtractField.type": "org.apache.kafka.connect.transforms.ExtractField$Value",
    "transforms.ExtractField.field": "after",
    "value.converter.schemas.enable": "false",
    "snapshot.mode": "initial",
    "schema.history.internal.kafka.bootstrap.servers": "kafka-1:9093,kafka-2:9093,kafka-3:9093",
    "schema.history.internal.kafka.topic": "schema-changes.briefing"
  }
}'
