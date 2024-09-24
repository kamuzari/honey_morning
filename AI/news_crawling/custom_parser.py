import requests
from bs4 import BeautifulSoup

def get_article_content(article_url):
    """기사의 URL에서 본문 내용을 가져오는 함수"""
    try:
        response = requests.get(article_url)

        if response.status_code == 200:
            # HTML 파싱
            soup = BeautifulSoup(response.text, 'html.parser')
            
            # article 태그 내의 본문 내용 추출
            article_tag = soup.find('article')
            
            if article_tag:
                # 본문 텍스트만 추출 (HTML 태그 제외)
                content = article_tag.get_text(separator="\n").strip()
                return content
            else:
                print(f"본문 내용을 찾을 수 없습니다: {article_url}")
                return None
        else:
            print(f"요청 실패: {response.status_code}")
            return None
    except Exception as e:
        print(f"오류 발생: {e}")
        return None
