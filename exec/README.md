# 포팅 매뉴얼

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
