import re
from datetime import datetime, timedelta

def get_article_time_from_text(time_text):
    """시간 텍스트를 기반으로 실제 시간을 계산하여 반환"""
    if '분전' in time_text:
        minutes_ago = int(re.search(r'\d+', time_text).group())
        return datetime.now() - timedelta(minutes=minutes_ago)
    elif '시간전' in time_text:
        hours_ago = int(re.search(r'\d+', time_text).group())
        return datetime.now() - timedelta(hours=hours_ago)
    elif '일전' in time_text:
        days_ago = int(re.search(r'\d+', time_text).group())
        return datetime.now() - timedelta(days=days_ago)
    else:
        # 다른 형식의 시간 텍스트 처리 필요시 추가
        return None
