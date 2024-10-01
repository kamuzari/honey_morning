import {useState, useEffect} from 'react';
import {useNavigate} from 'react-router-dom';
import {useQuery, useQueryClient} from '@tanstack/react-query';
import styled from 'styled-components';
import {instance} from '@/api/axios';
import {findAlarmCategory} from '@/api/alarmApi';
import HashTag from '@/component/MyPage/HashTag';
import Pagination from '@/component/MyPage/Pagination';
import GlobalBtn from '@/component/GlobalBtn';
import {
  HashTagSelect,
  CustomHashTagSelect,
} from '@/component/InterestSetting/InterestSetting';
import {useInterestStore} from '@/store/InterestStore';
import NavBar from '@/component/NavBar/NavBar';
import {NavBarProps} from '@/component/NavBar/NavBar';
import {NavIconProps} from '@/component/NavBar/NavIcon';
import {SoleMainNavBarProps} from '@/component/NavBar/NavBar';
import {getBriefs} from '@/api/briefingApi';

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

// interface Data {
//   id: number;
//   categories: string[];
//   date: string;
//   content: string;
//   correctAnswer: number
// }

interface Response {
  dates: Data[];
  totalPage: number;
}

const dataSample: Data = {
  date: '8/30',
  content: '이것은 아무 내용이 들어있는 아무 샘플이지요.',
};

// 각 도형의 색을 계산하는 함수
const getShapeColor = (isAttending: number, count: number): string => {
  if (isAttending === 0) {
    return 'gray'; // 참석하지 않은 경우 회색
  }
  if (isAttending === 1 && count === 0) {
    return 'linear-gradient(to top, yellow 33%, white 33%)'; // 1/3만큼 노랑
  }
  if (isAttending === 1 && count === 1) {
    return 'linear-gradient(to top, yellow 66%, white 66%)'; // 2/3만큼 노랑
  }
  if (isAttending === 1 && count === 2) {
    return 'yellow'; // 전부 노랑
  }
  return 'white'; // 기본 색상
};

//유저 정보
const fetchUserInfo = async () => {
  const {data} = await instance.get(`/api/auth/userInfo`);
  return data;
};

const MyPage: React.FC = () => {
  // useQuery를 사용하여 userInfo 가져오기
  const {
    data: userInfo,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['userInfo'],
    queryFn: fetchUserInfo,
  });

  const [daysInMonths, setDaysInMonths] = useState<number[][]>([]); // 각 달의 일수를 저장하는 배열
  const [currentMonthIndex, setCurrentMonthIndex] = useState(0); // 현재 보여줄 달 인덱스
  const itemsPerPage = 5;
  const [totalPages, setTotalPages] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);
  const [briefingData, setBriefingData] = useState([]);
  const [daysSinceSignup, setDaysSinceSignup] = useState(0); // 가입일로부터 며칠인지 상태로 관리
  const [streakData, setStreakData] = useState<any>(null);
  const signupDate = new Date('2024-09-01'); // 예시 가입 날짜
  const today = new Date();

  useEffect(() => {
    const fetchBriefs = async () => {
      try {
        const data = await getBriefs(currentPage);
        setBriefingData(data.histories);
        setTotalPages(data.totalPage);
      } catch (error) {
        console.error(`[Error] data: ${error}`);
      }
    };

    fetchBriefs();
  }, [currentPage]);

  useEffect(() => {
    const userSignupDate = new Date('2024-09-20'); // 예시 가입 날짜, 실제로는 userInfo.createdAt 사용
    const today = new Date();

    // 가입한 달부터 현재 달까지 각 달의 일수를 계산
    const monthDaysArray = calculateDaysInMonths(userSignupDate, today);
    setDaysInMonths(monthDaysArray);
  }, []);

    // 이전 달로 이동
    const handlePreviousMonth = () => {
      setCurrentMonthIndex((prevIndex) =>
        prevIndex === 0 ? daysInMonths.length - 1 : prevIndex - 1
      );
    };
  
    // 다음 달로 이동
    const handleNextMonth = () => {
      setCurrentMonthIndex((prevIndex) =>
        prevIndex === daysInMonths.length - 1 ? 0 : prevIndex + 1
      );
    };
  
// New function to fetch data from /api/alarms/result
const fetchStreakData = async () => {
  const {data} = await instance.get(`/api/alarms/result`);
  return data;
};

// Helper 함수: 가입한 달부터 현재 달까지 각 달의 일수를 계산
const calculateDaysInMonths = (startDate: Date, endDate: Date): number[][] => {
  const months = [];
  let currentDate = new Date(startDate);

  while (currentDate <= endDate) {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();
    const daysInMonth = new Date(year, month + 1, 0).getDate(); // 해당 달의 일수 계산
    const daysArray = Array(daysInMonth).fill(0); // 해당 달의 일수만큼 0으로 채운 배열 생성
    months.push(daysArray);

    // 다음 달로 이동
    currentDate.setMonth(currentDate.getMonth() + 1);
  }

  return months;
};


  console.log(streakData);
  console.log(userInfo);
  if(userInfo){
    console.log(userInfo.createdAt);
  }


  useEffect(() => {
    const getStreakData = async () => {
      try {
        const data = await fetchStreakData(); // 새로운 API 요청
        setStreakData(data);
      } catch (error) {
        console.error(`[Error] streak data: ${error}`);
      }
    };
    if (userInfo) {
      const days = calculateDaysSinceSignup(userInfo.createdAt);
      setDaysSinceSignup(days);
    }
    getStreakData();
  }, [userInfo]); // 이 useEffect는 컴포넌트가 처음 렌더될 때만 실행됨

    // 가입일과 오늘 날짜로 며칠 차이인지 계산하는 함수
    const calculateDaysSinceSignup = (createdAt: string) => {
      const signupDate = new Date(createdAt);
      const today = new Date();
      
      // 두 날짜 간의 차이 계산 (밀리초 차이를 일 단위로 변환)
      const differenceInTime = today.getTime() - signupDate.getTime();
      const differenceInDays = Math.floor(differenceInTime / (1000 * 3600 * 24));
  
      return differenceInDays;
    };
    

    // 각 일자에 대응하는 도형 배열을 생성
    const shapesArray = Array.from({ length: daysSinceSignup }, (_, index) => (
      <Shape key={index} />
    ));

    

  const currentItems = briefingData;

  const [isSelectOpen, setIsSelectOpen] = useState(false);
  const {selectedCategory, selectedCustomCategory, addCustomCategory} =
    useInterestStore();
  const selectedList = selectedCategory;
  const NavIcons = SoleMainNavBarProps;
  const navigate = useNavigate();

  // 로딩 상태 처리
  if (!briefingData.length) {
    return <div>Loading...</div>;
  }

  return (
    <Container>
      <WhiteContainer>
        <Content>
          <div className="titleContainer">
            <Title>{userInfo.username}님 관심사</Title>
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
          <div className="streakContainer">
            <CarouselContainer>
              <Button onClick={handlePreviousMonth}>{"<"}</Button>
              <MonthContainer>
                {/* 육각형이 교차 배치되도록 각 달의 일수를 표시 */}
                {daysInMonths[currentMonthIndex].map((dayIndex, rowIndex) => (
                  <Row key={rowIndex} isOffset={rowIndex % 2 !== 0}>
                    {/* 행에 맞게 남은 일수를 그리기 */}
                    {daysInMonths[currentMonthIndex]
                      .slice(rowIndex * 6, (rowIndex + 1) * 6) // 한 행에 6개의 육각형씩 그리기
                      .map((_, hexIndex) => (
                        <Shape key={hexIndex} />
                      ))}
                  </Row>
                ))}
              </MonthContainer>
              <Button onClick={handleNextMonth}>{">"}</Button>
            </CarouselContainer>
          </div>
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
              <PaginationItem
                key={index}
                onClick={() => {
                  navigate(`/briefingdetail/${item.briefId}`);
                }}
              >
                <span className="date">{item.createdAt.split('T')[0]}</span>
                <span className="content">{item.summary}</span>
              </PaginationItem>
            ))}
          </PaginationContainer>
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={setCurrentPage}
          />
        </Content>
        <NavBar props={NavIcons}></NavBar>
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
  align-items: center;
  flex-direction: column;
  padding: 2rem 3rem;
  box-sizing: border-box;
  * {
    // border: 1px solid lime;
  }
`;

export const WhiteContainer = styled.div`
  display: flex;
  width: 100%;
  height: 100%;
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

// 도형을 렌더링할 스타일드 컴포넌트 예시
const Shape = styled.div`
  width: 52px;
  height: 60px;
  background-color: var(--yellow-color);
  clip-path: polygon(50% 0%, 100% 25%, 100% 75%, 50% 100%, 0% 75%, 0% 25%);
  margin-right: 1px;
`;

// 행마다 오프셋을 적용하고 겹치게 배치
const Row = styled.div<{ isOffset: boolean }>`
  display: flex;
  justify-content: center;
  margin-left: ${({ isOffset }) => (isOffset ? '17.5%' : '0')}; /* 짝수 행에 오프셋을 줌 */
  margin-top: -4.5%; /* 위아래 행이 겹치도록 음수 margin 적용 */
`;

// Carousel 버튼
const Button = styled.button`
  background-color: transparent;
  border: none;
  font-size: 2rem;
  cursor: pointer;
`;

const MonthContainer = styled.div`
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  width: 300px; /* 너비 설정 */
`;

const CarouselContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
`;


export default MyPage;
