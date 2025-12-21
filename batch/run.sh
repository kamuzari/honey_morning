#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

echo 'container 정리 🧹'
docker compose down -v

echo 'container 실행 ✈️'
docker-compose up -d --build
