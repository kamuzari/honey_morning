#!/bin/bash

echo "🗑️기존 데이터 볼륨을 제거하고 새로운 인프라 환경을 업로드 합니다."

rm -rf ./database/master-data
rm -rf ./database/slave-data
rm -rf ./database/mysql

docker-compose down -v
docker-compose up -d

echo "인프라 모두 정상 가동되었습니다.
✅ 1. read only db에서 'show slave status\G;' replication 완료 여부를 확인하세요.
✅ 2. 웹 애플리케이션을 실행 주세요.
✅ 3. [선택] 테스트 데이터 생성을 원하시면 database/replication/warming/seed_register 를 실행해주세요."
