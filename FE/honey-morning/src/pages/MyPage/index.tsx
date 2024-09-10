import styled from 'styled-components';
import HashTag from '@/component/MyPage/HashTag';
import Pagination from '@/component/MyPage/Pagination';
import {useState} from 'react';
import GlobalBtn from '@/component/GlobalBtn';
import {
  HashTagSelect,
  CustomHashTagSelect,
} from '@/component/InterestSetting/InterestSetting';
import {useInterestStore} from '@/store/InterestStore';
export const categoryList = [
  '정치',
  '경제',
  '사회',
  '생활/문화',
  '세계',
  '기술/IT',
  '연예',
  '스포츠',
];

interface Data {
  date: string;
  content: string;
}
const dataSample: Data = {
  date: '8/30',
  content: '이것은 아무 내용이 들어있는 아무 샘플이지요.',
};

const MyPage: React.FC = () => {
  //Pagination
  var data: Data[] = [
    dataSample,
    dataSample,
    dataSample,
    dataSample,
    dataSample,
    dataSample,
  ]; // 임시 데이터 100개

  const itemsPerPage = 5;

  const [currentPage, setCurrentPage] = useState(1);

  // 페이지에 따라 데이터 슬라이싱
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = data.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(data.length / itemsPerPage);
  const [isSelectOpen, setIsSelectOpen] = useState(false);
  const {selectedCategory, selectedCustomCategory, addCustomCategory} =
    useInterestStore();
  const selectedList = selectedCategory;
  return (
    <Container>
      <WhiteContainer>
        <Content>
          <div className="titleContainer">
            <Title>내 관심사</Title>
            <BtnContainer>
              {isSelectOpen ? (
                <GlobalBtn
                  text="저장"
                  $bgColor="var(--green-color)"
                  $textColor="var(--black-color)"
                  $padding={3}
                  onClick={() => {
                    setIsSelectOpen(false);
                    addCustomCategory();
                  }}
                />
              ) : (
                <GlobalBtn
                  text="수정"
                  $bgColor="var(--darkblue-color)"
                  $textColor="var(--white-color)"
                  $padding={3}
                  onClick={() => {
                    console.log('작동');
                    setIsSelectOpen(true);
                  }}
                />
              )}
            </BtnContainer>
          </div>
          {isSelectOpen ? (
            <HashTagSelect />
          ) : (
            <div className="hashTagContainer">
              {categoryList.map(item => (
                <HashTag
                  text={item}
                  type="NEWS"
                  selected={selectedList.includes(item) ? true : false}
                />
              ))}
            </div>
          )}
          <div className="smallTitleContainer">
            <SmallTitle>나만의 관심사</SmallTitle>
          </div>

          {isSelectOpen ? (
            <CustomHashTagSelect />
          ) : (
            <div className="hashTagContainer">
              {selectedCustomCategory.length >= 1 ? (
                selectedCustomCategory.map(item => {
                  return (
                    <HashTag
                      text={item}
                      type="CUSTOM"
                    />
                  );
                })
              ) : (
                <HashTag
                  text="나만의 관심사가 없습니다."
                  type="CUSTOM"
                />
              )}
            </div>
          )}
        </Content>

        <Content>
          <div className="titleContainer">
            <Title>스트릭</Title>
          </div>
          <div className="streakContainer">스트릭은 아직 디자인 중입니다.</div>
        </Content>

        <Content>
          <div className="titleContainer">
            <Title>히스토리</Title>
          </div>
          <PaginationContainer>
            <PaginationHead>
              <li>날짜</li>
              <li>핵심 브리핑</li>
            </PaginationHead>
            {currentItems.map((item, index) => (
              <PaginationItem key={index}>
                <span className="date">{item.date}</span>
                <span className="content">{item.content}</span>
              </PaginationItem>
            ))}
          </PaginationContainer>
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={setCurrentPage}
          />
        </Content>
      </WhiteContainer>
    </Container>
  );
};

export const Container = styled.div`
  display: flex;
  width: 100%;
  height: 100vh;
  background-color: var(--darkblue-color);
  justify-content: center;
  padding: 3rem;
  box-sizing: border-box;
  * {
    // border: 1px solid lime;
  }
`;

export const WhiteContainer = styled.div`
  display: flex;
  width: 100%;
  flex-direction: column;
  background-color: white;
  border-radius: 3rem;
  padding: 5rem 0 0 0;
`;

export const Content = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  .titleContainer {
    display: flex;
    width: 100%;
  }

  .hashTagContainer {
    display: flex;
    width: 90%;
    height: 12rem;
    box-sizing: border-box;
    padding: 3rem 0 3rem 0;

    overflow-x: auto;
    &::-webkit-scrollbar {
      display: none;
    }
  }

  .smallTitleContainer {
    display: flex;
    width: 100%;
    margin: 2rem;
    padding: 0 0 0 3rem;
    align-items: center;
  }

  .streakContainer {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 40rem;
    border: 1px solid lime;
  }
`;
export const Title = styled.div`
  display: flex;
  height: 6rem;
  border-radius: 0 10rem 10rem 0;
  background-color: var(--yellow-color);
  font-weight: bold;
  font-size: 4rem;
  padding: 0 5rem 0 5rem;
  align-items: center;
`;

export const SmallTitle = styled.div`
  color: var(--black-color);
  font-size: 3.5rem;
  font-weight: bold;
`;

const PaginationContainer = styled.ul`
  display: flex;
  box-sizing: border-box;
  margin: 3rem 0 0 0;
  flex-direction: column;
  align-items: center;
`;

const PaginationHead = styled.div`
  display: flex;
  width: 100%;
  justify-content: space-around;
  border-bottom: 4px solid var(--black-color);
  padding: 0 0 1rem 0;
  li {
    display: flex;
    font-size: 3rem;
    font-weight: bold;
    justify-content: center;
  }

  li:nth-child(1) {
    width: 15rem;
  }
  li:nth-child(2) {
    width: 40rem;
  }
`;

const PaginationItem = styled.li`
  display: flex;
  font-size: 2.8rem;
  height: 5.2rem;
  width: 100%;
  align-items: center;
  justify-content: space-around;
  span {
    display: flex;
    justify-content: center;
  }
  .date {
    display: flex;
    width: 15rem;
  }
  .content {
    display: block;
    width: 40rem;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
  }
  &&:hover {
    font-weight: bold;
    background-color: var(--lightblue-color);
  }
`;

const BtnContainer = styled.div`
  display: flex;
  width: 20rem;
  justify-content: center;
`;

export default MyPage;
