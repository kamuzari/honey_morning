from crawler import crawl_news_articles
from custom_parser import get_article_content
from datetime import datetime, timedelta
import os
import pickle
import time

def save_articles_pickle(section_num, articles):
    """기사들을 섹션 번호에 따라 Pickle 파일로 저장"""
    base_dir = f"/var/data/{section_num}"
    os.makedirs(base_dir, exist_ok=True)

    # 현재 시간을 기반으로 파일명 생성
    now = datetime.now()
    file_name = now.strftime("_%H.pkl")
    file_path = os.path.join(base_dir, file_name)

    # Pickle 형식으로 저장
    with open(file_path, 'wb') as f:
        pickle.dump(articles, f)
    print(f"파일 저장 완료: {file_path}")

def main():
    # 목표 시간 설정 (예: 1시간 전)
    target_time = datetime.now() - timedelta(hours=1)
    
    # 섹션 번호 리스트 (예: 100부터 105까지)
    section_nums = ['100', '101', '102', '103', '104', '105']
    
    # 각 섹션 번호별로 크롤링 수행 및 Pickle로 저장
    for section_num in section_nums:
        print(f"섹션 번호 {section_num}에 대한 크롤링 시작...")
        articles = crawl_news_articles(section_num, target_time, max_pages=20)
        
        # articles 리스트를 구성 (각 article은 딕셔너리로 구성)
        structured_articles = []
        for title, link, _ in articles:
            time.sleep(1)
            content = get_article_content(link)
            if content:
                article_dict = {
                    'title': title,
                    'link': link,
                    'content': content
                }
                structured_articles.append(article_dict)
        
        # Pickle 파일로 저장
        save_articles_pickle(section_num, structured_articles)
        print(f"섹션 번호 {section_num}에 대한 크롤링 및 저장 완료.\n")

if __name__ == "__main__":
    main()
