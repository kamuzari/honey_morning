import requests
from bs4 import BeautifulSoup
from datetime import datetime
import json
from utils import get_article_time_from_text

def crawl_news_articles(section_num, target_time, max_pages=10):
    base_url = 'https://news.naver.com/section/template/SECTION_ARTICLE_LIST'
    
    articles = []
    next_cursor = None  # 초기 next 값은 None으로 시작
    page = 1

    while True:
        # AJAX 요청에 필요한 파라미터 설정
        params = {
            'sid': section_num,
            'sid2': '',
            'cluid': '',
            'pageNo': page,
            'date': '',
            'next': next_cursor,
            '_': int(datetime.now().timestamp() * 1000)  # 현재 타임스탬프
        }

        # AJAX 요청 보내기
        response = requests.get(base_url, params=params)
        
        if response.status_code == 200:
            # JSON 응답을 디코딩
            data = json.loads(response.text)
            # HTML 콘텐츠가 포함된 부분을 추출
            html_content = data.get('renderedComponent', {}).get('SECTION_ARTICLE_LIST', '')

            # BeautifulSoup을 사용하여 HTML 파싱
            soup = BeautifulSoup(html_content, 'html.parser')
            
            # 각 기사에 대한 정보를 추출
            article_blocks = soup.find_all('li')  # 필요한 li 태그로 크롤링 (html 구조에 따라 조정 필요)
            for block in article_blocks:
                title = block.find('strong').get_text().strip()
                link = block.find('a')['href']

                # "n분전", "n시간전" 텍스트를 추출하여 발행 시간 계산
                time_text = block.find('b').get_text()
                
                article_time = get_article_time_from_text(time_text)

                if article_time and article_time < target_time:
                    print("목표 시간에 도달했습니다. 크롤링을 종료합니다.")
                    return articles

                articles.append((title, link, article_time))
                print(f"기사 제목: {title}, 링크: {link}, 발행 시간: {article_time}")

            # "더 보기" 버튼이 있는지 확인
            more_button = soup.find('a', class_='section_more_inner _CONTENT_LIST_LOAD_MORE_BUTTON')

            if more_button:
                # 다음 페이지로 넘어가기 위한 next_cursor 값 갱신
                next_cursor_element = soup.find('div', class_='section_latest_article _CONTENT_LIST _PERSIST_META')
                if next_cursor_element:
                    next_cursor = next_cursor_element.get('data-cursor')  # 다음 AJAX 요청에 사용할 next 값 갱신
                    print(f"Next Cursor: {next_cursor}")
                else:
                    print("더 이상 불러올 데이터가 없습니다.")
                    break
            else:
                print("더 보기 버튼이 존재하지 않음. 크롤링을 종료합니다.")
                break

            page += 1

            if page > max_pages:
                print(f"최대 페이지({max_pages})에 도달했습니다. 크롤링을 종료합니다.")
                break
        else:
            print(f"요청 실패: {response.status_code}")
            break

    return articles
