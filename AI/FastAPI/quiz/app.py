from typing import Union, List
from fastapi import FastAPI
from pydantic import BaseModel, ConfigDict, ValidationError
from pydantic_core import from_json
from openai import AsyncOpenAI
from dotenv import load_dotenv
import os

load_dotenv(verbose=True)

model = "gpt-4o-mini";

prompt = """-- create two simple multiple choice quizes based on the info below
-- translate the JSON content to Korean without the keys
-- return the values in the JSON structure below
-- don't return any other lines, just return JSON structure
-- provide two quizes always
-- each quiz has four choices always, and one of them has to be the right answer for the problem
-- each choices needs to be distinguishable by the meaning to avoid ambiguity
-- each choices are preferred to be one term with no spaces, to pronounce it in 1 second
-- ensure "answer" is accurate as it will be used for grading and should not include any special characters like quotation marks
-- answer must be number between 1 to 4
-- thoroughly test and cross-verify "answer" values to ensure correctness
-- make sure there is no trailing comma
[{
    "problem": "", 
    "choices": [
        {"id": 1, "item": ""},
        {"id": 2, "item": ""},
        {"id": 3, "item": ""},
        {"id": 4, "item": ""}
    ],
    "answer": ""
},{
    "problem": "", 
    "choices": [
        {"id": 1, "item": ""},
        {"id": 2, "item": ""},
        {"id": 3, "item": ""},
        {"id": 4, "item": ""}
    ],
    "answer": ""
}]

Fill in the format above for the description provided by the user.
<description>
"""

app = FastAPI(root_path="/ai/quiz")
client = AsyncOpenAI(api_key = os.getenv("API_KEY"))

class JSON_Quiz(BaseModel):
    text: str

class Choice(BaseModel):
    id: int
    item: str

class Quiz(BaseModel):
    problem: str
    choices: List[Choice]
    answer: int

class JSON_Quiz_Out(BaseModel):
    model_config = ConfigDict(strict=True)

    data: List[Quiz]


@app.post("/", response_model=JSON_Quiz_Out)
async def generate_quiz(json: JSON_Quiz):

    chat_completion = await client.chat.completions.create(
        model= model,
        messages=[{"role": "user", "content": prompt + json.text}]
    )

    result = None
    print(chat_completion.choices[0].message.content)
    
    try:
        JSON_Quiz_Out.model_validate({'data':from_json(chat_completion.choices[0].message.content, allow_partial = True)})
        result = from_json(chat_completion.choices[0].message.content, allow_partial = True)
    except ValidationError as e:
        print(e)

    return {"data" : result}