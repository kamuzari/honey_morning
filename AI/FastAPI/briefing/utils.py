from datetime import datetime, timedelta
from tqdm import tqdm
import pickle
import glob
import os
import pandas as pd

# 이전 몇 개 까지의 파일을 읽을 것인지 작성
hours_back = 8

# 마운트된 디렉토리 (Docker run -v 과 일치해야 함)
pkl_dir = "/var/data/"

mapping_tag = {
    '정치': '100',
    '경제': '101',
    '사회': '102',
    '생활/문화': '103',
    '세계': '104',
    'IT/과학': '105'
}

# 파일 이름 배열을 반환
def get_file_patterns(tag: str) :
    section_number = mapping_tag.get(tag)
    current_time = datetime.now()
    start_time = current_time - timedelta(hours=hours_back)

    valid_hours = []
    for hour in range(hours_back + 1):  # 0부터 hours_back까지의 시간을 계산
        valid_hour = (start_time + timedelta(hours=hour)).strftime('%H')
        valid_hours.append(valid_hour)
    print("valid_hours: ")
    print(valid_hours)

    file_patterns = [f"/var/data/{section_number}/_{hour}.pkl" for hour in valid_hours]
    print("file_patterns : ")
    print(file_patterns)
    return file_patterns 
    

# 파일을 읽어서 concat된 텍스트를 반환
def get_merged_documents(tag, file_patterns) :
    merged_documents = []
    for file_pattern in tqdm(file_patterns, desc=f"섹션 번호: {tag} 파일"):
        file_list = glob.glob(file_pattern)

        for file_path in file_list:
            if(os.path.isfile(file_pattern)):
                with open(file_path, 'rb') as file:
                    merged_documents += pd.read_pickle(file)
                    print(len(merged_documents))
                    print(file_pattern)

    print(f"문서의 개수: {len(merged_documents)} 개 입니다.")

    print(file_patterns)

    return merged_documents
    
def get_overlapped_chunks(text, chunk, overlap):
    return [text[a:a+chunk].strip() for a in range(0, len(text), chunk-overlap)]