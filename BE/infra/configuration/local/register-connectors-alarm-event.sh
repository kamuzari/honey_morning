#!/bin/bash
echo "Waiting for Kafka Connect alarm event to start..."
sleep 3

CONNECTOR_NAME="mysql-outbox-connector"
CONNECTORS_URL="http://debezium-connect:8083/connectors"

echo "Checking if connector $CONNECTOR_NAME is already registered..."
EXISTING_CONNECTOR=$(curl -s "$CONNECTORS_URL/$CONNECTOR_NAME")

if [[ $EXISTING_CONNECTOR == *"error_code"* ]]; then
  echo "Registering new connector: $CONNECTOR_NAME"
  curl -X POST "$CONNECTORS_URL" -H "Content-Type: application/json" --data "~/debezium-mysql-connector-alarm-event.json"
else
  echo "Connector $CONNECTOR_NAME already exists. Skipping registration."
fi
