#!/bin/bash

echo "애플리케이션이 먼저 실행된 후 호출해야 합니다."

CONTAINER_NAME="honeymorning_read_only_db"
DB_NAME="honeymorning"
DB_USER="root"
DB_PASS="root"

echo "📦 프로시저 & 이벤트 등록 중..."

docker exec $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASS $DB_NAME -e "SOURCE /tmp/seed.sql;"

echo "✅ 등록 완료! 1분마다 자동 실행됨"
echo ""

docker exec $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASS $DB_NAME -e "SHOW EVENTS\G"