#!/bin/bash

echo "커넥터 확인"
sleep 2
# 커넥터 삭제
curl -X DELETE http://localhost:8083/connectors/mysql-outbox-connector

echo "커넥터 등록"
sleep 2
# 커넥터 등록
curl -X POST http://localhost:8083/connectors -H "Content-Type: application/json" -d '{
  "name": "mysql-outbox-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "database.hostname": "database","database.allowPublicKeyRetrieval": "true",
    "database.port": "3306",
    "database.user": "root",
    "database.password": "root",
    "database.server.id": "184054",
    "database.server.name": "main-database",
    "database.serverTimezone": "Asia/Seoul",
    "database.include.list": "honeymorning",
    "table.include.list": "honeymorning.outbox_alarm_event",
    "include.schema.changes": "false",

    "topic.prefix": "alarm_contents",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "transforms": "ExtractField",
    "transforms.ExtractField.type": "org.apache.kafka.connect.transforms.ExtractField$Value",
    "transforms.ExtractField.field": "after",
    "value.converter.schemas.enable": "true",
    "snapshot.mode": "initial",

    "schema.history.internal.kafka.bootstrap.servers": "kafka:29092",
    "schema.history.internal.kafka.topic": "schema-changes.honeymorning"
  }
}'