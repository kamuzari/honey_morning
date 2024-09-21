import os
import pickle

def load_articles_pickle(file_path):
    """Pickle 파일에서 기사 데이터를 로드"""
    if not os.path.exists(file_path):
        print(f"파일이 존재하지 않습니다: {file_path}")
        return None
    
    with open(file_path, 'rb') as f:
        articles = pickle.load(f)
    
    return articles

def load_all_articles_in_directory(section_num):
    """지정된 섹션 번호 디렉토리 내의 모든 Pickle 파일을 불러오는 함수"""
    base_dir = os.path.join('data', section_num)
    
    if not os.path.exists(base_dir):
        print(f"섹션 번호에 해당하는 디렉토리가 존재하지 않습니다: {base_dir}")
        return

    # 디렉토리 내의 모든 pkl 파일 찾기
    pkl_files = [f for f in os.listdir(base_dir) if f.endswith('.pkl')]
    
    if not pkl_files:
        print(f"디렉토리 내에 pkl 파일이 없습니다: {base_dir}")
        return

    # 각 pkl 파일 로드 및 출력
    for pkl_file in pkl_files:
        file_path = os.path.join(base_dir, pkl_file)
        print(f"파일 로드 중: {file_path}")
        articles = load_articles_pickle(file_path)
        
        if articles is not None:
            print(f"{len(articles)}개의 기사가 {pkl_file}에서 로드되었습니다.")
            for i, article in enumerate(articles, start=1):
                print(f"기사 {i}:")
                print(f"제목: {article['title']}")
                print(f"링크: {article['link']}")
                print(f"본문:\n{article['content']}")
                print("="*80)
        else:
            print(f"{pkl_file}에서 기사를 불러오지 못했습니다.")

def test_load_all_articles():
    """모든 섹션 디렉토리에서 pkl 파일을 불러와 테스트하는 함수"""
    # 섹션 번호 리스트 (예: 100부터 105까지)
    section_nums = ['100', '101', '102', '103', '104', '105']
    
    # 각 섹션 번호 디렉토리의 pkl 파일들을 모두 로드
    for section_num in section_nums:
        print(f"\n섹션 번호 {section_num}의 모든 pkl 파일을 로드합니다...")
        load_all_articles_in_directory(section_num)

if __name__ == "__main__":
    test_load_all_articles()
