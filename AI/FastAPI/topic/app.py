from typing import Union, Literal, List
from pydantic import BaseModel
from fastapi import FastAPI
from gensim import corpora
from gensim.models import LdaModel
from utils import process_documents, optimalize_lda_model

app = FastAPI(root_path="/ai/topic")


class JSON_Topic(BaseModel):
    tags: List[Literal['정치', '경제', '사회', '생활/문화', '세계', 'IT/과학']]


class Term(BaseModel):
    word: str
    weight: float


class Topic(BaseModel):
    topic_id: int
    topic_words: List[Term]


class JSON_Topic_Out(BaseModel):
    data: List[Topic]


@app.post("/", response_model=JSON_Topic_Out)
def generate_topic(json: JSON_Topic):

    mapping_tag = {
        '정치': '100',
        '경제': '101',
        '사회': '102',
        '생활/문화': '103',
        '세계': '104',
        'IT/과학': '105'
    }

    tag_numbers = [mapping_tag.get(tag) for tag in json.tags]

    processed_documents = process_documents(tag_numbers)

    print(f'전체 문서의 개수: {len(processed_documents)}개')
    
    dictionary = corpora.Dictionary(processed_documents)

    corpus = [dictionary.doc2bow(text) for text in processed_documents]

    optimal_lda_model = optimalize_lda_model(corpus, dictionary, processed_documents)

    # 토픽의 수
    num_topics = optimal_lda_model.num_topics
    
    print(f'최적화된 토픽의 개수: {num_topics}개')

    topics_list = []

    # 각 토픽의 단어와 비중 추출
    for topic_num in range(num_topics):
        term_weight_pairs = optimal_lda_model.get_topic_terms(topicid=topic_num, topn=30)
        terms = [Term(word=dictionary[term_id], weight=weight) for term_id, weight in term_weight_pairs]
        topic = Topic(topic_id=topic_num, topic_words=terms)
        topics_list.append(topic)

    print("토픽의 단어와 비중 추출 완료")
    
    return JSON_Topic_Out(data=topics_list)



