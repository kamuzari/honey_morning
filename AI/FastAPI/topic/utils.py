from konlpy.tag import Okt
import re
from gensim import corpora
from gensim.models.ldamodel import LdaModel
import pickle
import glob
from datetime import datetime, timedelta
from gensim.models.coherencemodel import CoherenceModel
from tqdm import tqdm

# 이전 몇 개 까지의 파일을 읽을 것인지 작성
hours_back = 8

# 한국어 불용어 텍스트 파일 경로
stopwords_file_path = './korean_stopwords.txt'


def load_stopwords(file_path):
    """
    file_path 경로에서 불용어를 불러오는 함수
    """
    with open(file_path, 'r', encoding='utf-8') as f:
        stopwords = f.read().splitlines()
    return stopwords


def preprocess_text(text, stopwords=None):
    """
    Okt를 사용한 텍스트 전처리 함수
    - 불용어 제거
    - 명사 추출
    """
    okt = Okt()
    # 정규 표현식을 사용한 숫자/특수문자 제거
    text = re.sub(r'\d+', '', text)
    text = re.sub(r'[^\w\s]', '', text)

    nouns = okt.nouns(text)

    if stopwords:
        nouns = [word for word in nouns if word not in stopwords]
    
    # 길이가 1인 단어들 제거
    nouns = [word for word in nouns if len(word) > 1]

    processed_text = ' '.join(nouns)

    return processed_text


def load_and_merge_section_data(section_number, hours_back=hours_back):
    """
    섹션 번호에 해당하는 모든 pkl 파일을 불러와서 기사 본문을 개별 문서로 병합하는 함수
    현재 시간에서 -hours_back 시간 전까지의 파일만 병합
    """
    current_time = datetime.now()
    start_time = current_time - timedelta(hours=hours_back)

    valid_hours = []
    for hour in range(hours_back + 1):  # 0부터 hours_back까지의 시간을 계산
        valid_hour = (start_time + timedelta(hours=hour)).strftime('%H')
        valid_hours.append(valid_hour)

    file_patterns = [f"/var/data/{section_number}/*_{hour}.pkl" for hour in valid_hours]

    merged_content = []
    for file_pattern in file_patterns:
        file_list = glob.glob(file_pattern)

        for file_path in file_list:
            with open(file_path, 'rb') as file:
                data = pickle.load(file)
                for article in data:
                    merged_content.append(article['content'])

    return merged_content  # 각각의 기사를 리스트 형태로 반환


def process_documents(section_numbers, stopwords_file_path=stopwords_file_path, hours_back=hours_back):

    stopwords = load_stopwords(stopwords_file_path)

    processed_documents = []
    
    for section_number in tqdm(section_numbers, desc='태그 병합 중'):
        merged_content_list = load_and_merge_section_data(section_number, hours_back)
        for content in merged_content_list:
            processed_text = preprocess_text(content, stopwords)
            processed_documents.append(processed_text.split())

    return processed_documents


def optimalize_lda_model(corpus, dictionary, processed_documents, start=2, end=6, step=1):
    coherence_values = []
    lda_model_list = []

    try:
        if not corpus:
            raise ValueError("코퍼스가 비어 있습니다. LDA 모델을 생성할 수 없습니다.")

        for num_topics in tqdm(range(start, end+1, step), desc='토픽 개수 최적화 중...'):
            lda_model = LdaModel(corpus=corpus, id2word=dictionary, num_topics=num_topics)
            lda_model_list.append(lda_model)
            coherence_model = CoherenceModel(model=lda_model, texts=processed_documents, dictionary=dictionary, coherence='c_v')
            coherence_values.append(coherence_model.get_coherence())

        print('LDA 모델 최적화-토픽 개수 추출 완료')
        optimal_model = lda_model_list[coherence_values.index(max(coherence_values))]
        return optimal_model

    except ValueError as ve:
        print(ve)
        return None  

    except Exception as e:
        print(e)
        return None