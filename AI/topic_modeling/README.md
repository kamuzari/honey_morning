# LDA Topic Modeling

- 개요: 크롤링된 수많은 기사의 집합 속에서 의미 있는 주제를 찾기 위한 LDA(Latent Dirichlet Allocation)를 통한 토픽 모델링(Topic Modeling)을 수행한 프로젝트입니다.
- 작성날짜: 2024.09.27
- 작성자: 배성진

## 주요 폴더 구조

```
📦topic_modeling
 ┣ 📂data
 ┃ ┣ 📂100
 ┃ ┃ ┗ 📜*_08.pkl
 ┃ ┃ ┗ 📜*_09.pkl
 ┃ ┃ ┗ 📜...
 ┃ ┣ 📂101
 ┃ ┃ ┗ 📜*_08.pkl
 ┃ ┃ ┗ 📜*_09.pkl
 ┃ ┃ ┗ 📜...
 ┃ ┣ 📂102
 ┃ ┃ ┗ 📜*_08.pkl
 ┃ ┃ ┗ 📜*_09.pkl
 ┃ ┃ ┗ 📜...
 ┃ ┣ 📂103
 ┃ ┃ ┗ 📜*_08.pkl
 ┃ ┃ ┗ 📜*_09.pkl
 ┃ ┃ ┗ 📜...
 ┃ ┣ 📂104
 ┃ ┃ ┗ 📜*_08.pkl
 ┃ ┃ ┗ 📜*_09.pkl
 ┃ ┃ ┗ 📜...
 ┃ ┗ 📂105
 ┃ ┃ ┗ 📜*_08.pkl
 ┃ ┃ ┗ 📜*_09.pkl
 ┃ ┃ ┗ 📜...
 ┣ 📜lda_function.ipynb
 ┣ 📜240924_lda.ipynb
 ┣ 📜240924_lda.ipynb
 ┗ 📜requirements.txt
```

- 향후 데이터 수집과 활용에 따라 폴더 구조 변경 및 파일명 변경이 이뤄질 수 있습니다.

## 프로젝트 설명

- `news_crawling` 프로젝트를 통해 생성된 섹션별 데이터를 병합하고, corpus 데이터 처리를 수행해 LDA 모델링을 수행했습니다.

- LDA 모델로 추출된 토픽의 개수에 따른 응집도 점수를 계산합니다. 계산된 값에 따라 최적의 토픽의 개수를 결정합니다.

- `pyLDAvis` 패키지를 통해 HTML 파일을 생성하여 LDA 모델링의 결과를 시각화했습니다.

## 주요 기능 설명

- `preprocess_text` 함수

```
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
```

- `optimalize_lda_model` 함수

```
def optimalize_lda_model(corpus, dictionary, processed_documents, start=2, end=6, step=1):
    coherence_values = []
    lda_model_list = []
    for num_topics in range(start, end+1, step):
        lda_model = LdaModel(corpus=corpus, id2word=dictionary, num_topics=num_topics)
        lda_model_list.append(lda_model)
        coherence_model = CoherenceModel(model=lda_model, texts=processed_documents, dictionary=dictionary, coherence='c_v')
        coherence_values.append(coherence_model.get_coherence())
    optimal_model = lda_model_list[coherence_values.index(max(coherence_values))]
    return optimal_model
```
