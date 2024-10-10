import {useState, useEffect} from 'react';
import {useNavigate} from 'react-router-dom';
import {
  useQuery,
  useQueryClient,
  useMutation,
  QueryClient,
} from '@tanstack/react-query';
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
import Streak from '@/component/Streak/Streak';
import {StreakProps} from '@/component/Streak/Streak';

export const categoryList = [
  '정치',
  '경제',
  '사회',
  '생활/문화',
  '세계',
  '기술/IT',
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

//유저 정보
const fetchUserInfo = async () => {
  const {data} = await instance.get(`/api/auth/userInfo`);
  return data;
};

//태그 정보
const fetchTagInfo = async () => {
  const {data} = await instance.get(`/api/alarms/category`);
  return data;
};

const MyPage: React.FC = () => {
  // useQuery를 사용하여 userInfo 가져오기
  const {data: userInfo} = useQuery({
    queryKey: ['userInfo'],
    queryFn: fetchUserInfo,
  });

  const {data: tagInfo} = useQuery({
    queryKey: ['tagInfo'],
    queryFn: fetchTagInfo,
  });

  const [totalPages, setTotalPages] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);
  const [briefingData, setBriefingData] = useState([]);
  const [streakData, setStreakData] = useState(null);
  const [finalStreak, setFinalStreak] = useState(null);
  const [rows, setRows] = useState(0);
  if (tagInfo) console.log(tagInfo);
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

  // New function to fetch data from /api/alarms/result
  const fetchStreakData = async () => {
    const {data} = await instance.get(`/api/alarms/result`);

    return data;
  };

  useEffect(() => {
    if (userInfo) {
      const getStreakData = async () => {
        try {
          const data = await fetchStreakData(); // 새로운 API 요청
          console.log(data);
          setStreakData(data);

          const finalData = getFinalStreakData(data, userInfo.createdAt);
          setFinalStreak(finalData);
        } catch (error) {
          console.error(`[Error] streak data: ${error}`);
        }
      };

      const days = calculateDaysBetween(
        userInfo.createdAt,
        '2024-10-11T09:18:35.010323',
      );
      setRows(days % 6 == 0 ? days / 6 : Math.floor(days / 6) + 1);
      getStreakData();
    }
  }, [userInfo]); // 이 useEffect는 컴포넌트가 처음 렌더될 때만 실행됨

  const getFinalStreakData = (streakData: StreakProps[], startDate: string) => {
    const broken = {
      count: 0,
      isAttending: 0,
      createdAt: 'string',
    };
    var answer = [];
    for (
      var i = 0;
      i <
      calculateDaysBetween(userInfo.createdAt, '2024-10-09T09:18:35.010323');
      i++
    ) {
      answer.push(broken);
    }
    streakData.forEach(item => {
      const daysBetween = calculateDaysBetween(startDate, item.createdAt);
      answer[daysBetween] = {
        count: item.count,
        isAttending: item.isAttending,
        createdAt: item.createdAt,
      };
    });

    return answer;
  };
  // 가입일과 오늘 날짜로 며칠 차이인지 계산하는 함수
  const calculateDaysBetween = (from: string, to: string) => {
    const signupDate = new Date(from);
    var today = null;
    if (to == 'today') {
      today = new Date();
    } else {
      today = new Date(to);
    }

    // 두 날짜 간의 차이 계산 (밀리초 차이를 일 단위로 변환)
    const differenceInTime = today.getTime() - signupDate.getTime();
    const differenceInDays = Math.floor(differenceInTime / (1000 * 3600 * 24));
    return differenceInDays;
  };

  const currentItems = briefingData;

  const [isSelectOpen, setIsSelectOpen] = useState(false);
  const {selectedCategory, selectedCustomCategory, addCustomCategory} =
    useInterestStore();
  const [selectedCategoryList, setSelectedCategoryList] = useState([]);
  const selectedList = selectedCategory;
  const NavIcons = SoleMainNavBarProps;
  const navigate = useNavigate();

  useEffect(() => {
    if (tagInfo) {
      // console.log('실행!');
      var selectedCategoryList = [];
      tagInfo.map(item => {
        selectedCategoryList.push(item.word);
      });
      setSelectedCategoryList(selectedCategoryList);
    }
  }, [tagInfo]);

  const queryClient = useQueryClient();
  const patchFn = () => {
    const data = {categoryWords: selectedCategory};
    setSelectedCategoryList(selectedCategory);
    return instance.patch('/api/alarms/category', data);
  };
  const mutation = useMutation({
    mutationFn: patchFn,
    onSuccess: () => {
      queryClient.invalidateQueries({queryKey: ['tagInfo']});
    },
  });

  console.log(selectedCategoryList);
  // 로딩 상태 처리
  if (!briefingData.length) {
    return <div>Loading...</div>;
  }

  return (
    <Container>
      <WhiteContainer>
        <Content>
          <div className="titleContainer">
            <Title>{userInfo && userInfo.username}님 관심사</Title>
            <BtnContainer>
              {isSelectOpen ? (
                <GlobalBtn
                  text="저장"
                  $bgColor="var(--green-color)"
                  $textColor="var(--black-color)"
                  $padding={3}
                  onClick={() => {
                    setIsSelectOpen(false);
                    mutation.mutate();
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
              {categoryList.map((item, index) => (
                <HashTag
                  key={index}
                  text={item}
                  type="NEWS"
                  selected={selectedCategoryList.includes(item) ? true : false}
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
            {streakData &&
              Array.from(Array(rows).keys()).map(rowNum => {
                return (
                  <Row
                    $left={rowNum % 2 == 0}
                    key={rowNum}
                  >
                    {finalStreak
                      .slice(rowNum * 6, (rowNum + 1) * 6)
                      .map((item, index) => (
                        <Streak
                          key={index}
                          count={item.count}
                          isAttending={item.isAttending}
                          createdAt={item.createdAt}
                          size={7}
                        />
                      ))}
                  </Row>
                );
              })}
          </div>
        </Content>

        <Content>
          <div className="titleContainer">
            <Title>히스토리</Title>
          </div>
          <PaginationContainer>
            <PaginationHead>
              <li>핵심 브리핑</li>
            </PaginationHead>
            {currentItems.map((item, index) => (
              <PaginationItem
                key={index}
                onClick={() => {
                  navigate(`/briefingdetail/${item.briefId}`);
                }}
              >
                {/* <span className="date">{item.createdAt.split('T')[0]}</span> */}
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
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 36rem;
    width: 85%;
    overflow-x: hidden;
    overflow-y: scroll;
    box-sizing: border-box;
    padding: 20rem 0 5rem 4rem;
    margin-bottom: 3rem;
  }
  .topic {
    // flex-direction: column;
    // display: flex;
  }
`;
export const Title = styled.div`
  display: flex;
  height: 6rem;
  border-radius: 0 10rem 10rem 0;
  background-color: var(--yellow-color);
  font-weight: bold;
  font-size: 3rem;
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
  height: 30rem;
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
  height: 6rem;
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
    width: 55rem;
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

const Row =
  styled.div <
  {$left: boolean} >
  `
  display: flex;
  width: 100%;
  box-sizign: border-box;
  padding-left: ${props => (props.$left ? '0' : '7rem')};
  margin-top: -1.6rem;

`;

export default MyPage;
