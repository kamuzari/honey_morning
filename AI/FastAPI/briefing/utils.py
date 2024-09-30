from datetime import datetime, timedelta
from tqdm import tqdm
import pickle
import glob

# 이전 몇 개 까지의 파일을 읽을 것인지 작성
valid_hours = 2

# 마운트된 디렉토리 (Docker run -v 과 일치해야 함)
pkl_dir = "./var/data/"

# 파일 이름 배열을 반환
def get_file_patterns(tag: str) :
    file_patterns = []
    now = datetime.now()
    for i in range(1, valid_hours+1) :
        file_patterns.append(pkl_dir + tag + "/" + (now - timedelta(hours=i)).strftime("_%H.pkl"))
    print("file_patterns : ")
    print(file_patterns)
    return file_patterns 
    

# 파일을 읽어서 concat된 텍스트를 반환
def get_merged_document(tag, file_patterns) :
    merged_content = []
    for file_pattern in tqdm(file_patterns, desc=f"섹션 번호: {tag} 파일"):
        file_list = glob.glob(file_pattern)

        for file_path in file_list:
            with open(file_path, 'rb') as file:
                if file:
                    data = pickle.load(file)
                    for article in data:
                        merged_content.append(article['content'])

    merged_document = ' '.join(merged_content)
    merged_document.replace("\n", " ")
    merged_document.replace("  ", " ")

    return merged_document
    


