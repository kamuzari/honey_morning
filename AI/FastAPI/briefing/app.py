from typing import Union, Literal, List
from fastapi import FastAPI
from pydantic import BaseModel, ConfigDict, ValidationError

import torch
from transformers import PreTrainedTokenizerFast
from transformers import BartForConditionalGeneration

import pickle
import pandas as pd
import re
import os
from datetime import datetime, timedelta

from sklearn_extra.cluster import KMedoids
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.feature_extraction.text import CountVectorizer

from transformers import pipeline

from pydantic_core import from_json
from openai import AsyncOpenAI
from dotenv import load_dotenv

from utils import get_file_patterns, get_merged_documents, get_overlapped_chunks

load_dotenv(verbose=True)

app = FastAPI(root_path="/ai/briefing")
client = AsyncOpenAI(api_key = os.getenv("API_KEY"))

model = "gpt-3.5-turbo";

prompt = """
-- 너에게 전날 밤에 있었던 뉴스들을 요약해서 들려주는 리포터 역할을 부여할게.
-- <description> 다음으로 기사들을 요약한 요약문들이 각각 주어질거야.
-- 그 요약문들을 모두 종합해서 자연스럽게 한국어 뉴스 브리핑을 만들어줘.
-- 친절한 말투로 부탁해.
-- 브리핑은 짧은 브리핑과 긴 브리핑 두가지로 만들어줘.
-- 짧은 브리핑과 긴 브리핑 모두 문장의 끝을 구어체로 제대로 맺어줘. "~습니다." 혹은 "~요" 형식을 말하는거야.
-- 짧은 브리핑은 500 token, 긴 브리핑은 1000 token 정도로 만들어줘.
-- 단어나 내용이 반복되지 않도록 해줘.
-- 짧은 브리핑과 긴 브리핑 모두 "<오늘 날짜> 꿀모닝 브리핑을 시작합니다." 로 시작해줘
-- make sure there is no trailing comma

{
    "shortBriefing": "", 
    "longBriefing": ""
}

-- 위의 포맷을 채워서 반환해줘. 위의 포맷외에 아무런 내용도 담아서는 안돼.
<description>
"""

class JSON_Briefing(BaseModel):
    tags: List[Literal['정치', '경제', '사회', '생활/문화', '세계', 'IT/과학']]

class Briefing(BaseModel):
    shortBriefing : str
    longBriefing : str

class JSON_Briefing_Out(BaseModel):
    model_config = ConfigDict(strict=True)

    data: Briefing

mapping_tag = {
    '정치': '100',
    '경제': '101',
    '사회': '102',
    '생활/문화': '103',
    '세계': '104',
    'IT/과학': '105'
}

@app.post("/", response_model=JSON_Briefing_Out)
async def read_briefing(json: JSON_Briefing):

    api_input = prompt
    cnt = 1

    countvectorizer = CountVectorizer(stop_words=["\n", "기자", "뉴스"], ngram_range=(1,2), max_df = 0.4, min_df = 0.05, lowercase=True, max_features = 550)
    summarizer = pipeline('summarization', model='gogamza/kobart-summarization')
    
    shortBriefing = ""
    longBriefing = ""

    for tag in json.tags:
        merged_documents = get_merged_documents(tag, get_file_patterns(tag))
        data = []
        for doc in merged_documents:
            data.append(doc.get("content"))

        data = TfidfTransformer().fit_transform(countvectorizer.fit_transform(data))

        clst = KMedoids(n_clusters=3)
        clst.fit(data)

        medoid_indices = clst.medoid_indices_
        print(f"중심으로 선정된 기사들의 index: {medoid_indices}")


        for med in medoid_indices:
            text = merged_documents[med].get("content").replace("\n","").strip()
            re.sub('[^A-Za-z0-9가-힣]', '', text)

            while len(text) > 1024:
                outs = []   
                for chunk in get_overlapped_chunks(text, 256, 32):
                    out = summarizer(chunk.strip(), max_length=64, min_length=8)
                    outs.append(out[0]['summary_text'].strip())
                text = ' '.join(outs)
            api_input += str(cnt) + ". "
            api_input += text.strip()
            api_input += "\n"
            cnt += 1
    print("open api에 다음과 같은 prompt를 보냅니다.")
    print(api_input)
    chat_completion = await client.chat.completions.create(
        model= model,
        messages=[{"role": "user", "content": api_input}]
    )
        
    print(chat_completion.choices[0].message.content)
    try:
        JSON_Briefing_Out.model_validate({'data':from_json(chat_completion.choices[0].message.content, allow_partial = True)})
        response = from_json(chat_completion.choices[0].message.content, allow_partial = True)
    except ValidationError as e:
        print(e)
    
    return {"data" : {"shortBriefing" : response.get("shortBriefing"), "longBriefing" : response.get("longBriefing")}}