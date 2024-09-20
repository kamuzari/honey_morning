import requests

# API 엔드포인트 설정
url = "http://localhost:8000/generate"

lyrics = "[Verse]\n하늘 아래 우리 둘\n눈빛 속에 빠져서\n손을 잡고 걸어가\n끝없는 길 같이 가\n\n[Verse 2]\n바람 속에 머리 날려\n함께 웃는 소리들\n내게 속삭이는 말\n사랑해 너만을\n\n[Chorus]\n내 맘 속에 넌 늘 있어\n잊지 못해 너의 미소\n하나 된 우리의 시간이\n끝없이 이어지길\n\n[Verse 3]\n밤하늘에 별이 빛나\n너와 나의 약속은\n달빛 아래 영원히\n우린 함께 머물러\n\n[Verse 4]\n미래가 눈앞에 보여\n우리 꿈 안에 살고\n서로 의지하며 가\n끝없이 이어지길\n\n[Chorus]\n내 맘 속에 넌 늘 있어\n잊지 못해 너의 미소\n하나 된 우리의 시간이\n끝없이 이어지길"
title = "사랑의 멜로디"

data = {
    "prompt": lyrics,
    "title" : title,
    "tags" : "Dance & Electronic",
    "mv" :"chirp-v3-0",
}

data = {
    "prompt" : "사랑에 대한 노래"
}

response = requests.post(url, json=data)

# 응답 확인
if response.status_code == 200:
    print("Generated Lyrics:", response.json())
else:
    print(f"Error: {response.status_code}, {response.text}")