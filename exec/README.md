# 포팅 매뉴얼

### 사용한 JVM, 웹서버, WAS 제품 등의 종류와 설정 값, 버전(IDE 버전 포함) 기재

### 빌드 시 사용되는 환경 변수 등의 내용 상세 기재

### 배포 시 특이사항 기재

### DB 접속 정보 등 프로젝트(ERD)에 활용되는 주요 계정 및 프로퍼티가 정의된 파일 목록

## 빌드 방법

### docker compose

1. root 폴더에서 `docker compose up -d` 실행

### 크롤링

1. "./AI/news_crawling" 폴더에서 `docker build -t hm-ai-crawling` 실행
2. `crontab -e` 명령어 실행으로 linux 스케줄러 설정
3. 다음의 명령 추가 및 저장

```
0 * * * * docker run --rm --name hm-ai-crawling --network hm-network -v ~/project_data/crawling_data:/var/data -v /etc/localtime:/etc/localtime hm-ai-crawling
```

## 외부 서비스

### ElevenLabs

가입 계정의 API KEY 필요

### SUNO

가입 계정의 SessionID 필요

### OpenAI - ChatGPT(4o-mini)

가입 계정의 API KEY 필요

## DB 덤프 파일 최신본

## 시연 시나리오
