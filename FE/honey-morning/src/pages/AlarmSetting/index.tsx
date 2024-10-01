import React,{useState, useEffect} from 'react';
import {useQuery, useMutation} from '@tanstack/react-query'
import {useNavigate} from 'react-router-dom';
import styled from 'styled-components';
import {instance} from '@/api/axios';
import GlobalBtn from '@/component/GlobalBtn';
import NavBar from '@/component/NavBar/NavBar';
import {SoleMainNavBarProps} from '@/component/NavBar/NavBar';

// Typescript
interface AlarmData {
  id: number,
  alarmTime: string,
  daysOfWeek: string,
  repeatFrequency: number,
  repeatInterval: number,
  musicFilePath: string,
  isActive: number,
}


const week = ['월', '화', '수', '목', '금', '토', '일'];
const timeIntervalList = [1, 5, 10, 15, 20, 25, 30];
const repeatCntList = [1, 2, 3, 5, 10];


const hours = Array.from({length: 24}, (_, i) => String(i).padStart(2, '0'));
const minutes = Array.from({length: 60}, (_, i) => String(i).padStart(2, '0'));
const seconds = Array.from({length: 60}, (_, i) => String(i).padStart(2, '0'));

// 알람 데이터 가져오기(조회)
const fetchAlarmData = async (): Promise<AlarmData|null> => {
  try {
    const token = sessionStorage.getItem('access');
    const response = await instance.get(`/api/alarms`, {
      headers: {
        access: token,
      },
    });
    return response.data;
  }   catch (error: any) {
    if (error.response?.status === 500) {
      console.error("서버 오류 발생, 기본값을 반환합니다.");
      return {
        id: 0,
        alarmTime: '07:00',
        daysOfWeek: '0000000',  
        repeatFrequency: 0,
        repeatInterval: 0,
        musicFilePath: '',
        isActive: 0,
      };
    }
    throw error;
  }
};

const updateAlarmData = async (updatedAlarm: AlarmData) => {
  try {
    const token = sessionStorage.getItem('access');
    const response = await instance.patch(`/api/alarms`, updatedAlarm, {
      headers: {
        access: token,
      },
    });
    return response.data;
  } catch (error) {
    console.error("알람 업데이트 중 오류 발생:", error);
    throw error;
  }
};


function getAdjustedTime(inputHour: number, inputMinute: number) {
  // 현재 시간 가져오기
  const currentTime = new Date();

  // 입력된 시와 분을 기반으로 오늘 날짜에 해당하는 Date 객체 생성
  let inputTime = new Date(
    currentTime.getFullYear(),
    currentTime.getMonth(),
    currentTime.getDate(),
    inputHour,
    inputMinute,
  );

  // 입력된 시간이 현재 시간보다 과거인 경우 (즉, 입력된 시간이 이미 지난 시간인 경우)
  if (inputTime < currentTime) {
    // 입력된 시간에 하루를 더해서 다음 날로 이동
    inputTime.setDate(inputTime.getDate() + 1);
  }
  
  // 입력된 시간과 현재 시간의 차이를 계산 (밀리초 단위)
  const timeDifference = inputTime.getTime() - currentTime.getTime();
  
  // 5시간을 밀리초로 계산 (5 * 60 * 60 * 1000 밀리초)
  const fiveHoursInMs = 5 * 60 * 60 * 1000;
  
  // 입력된 시간과 현재 시간의 차이가 5시간 이하인 경우
  if (timeDifference <= fiveHoursInMs) {
    // 입력된 시간에 다시 하루를 더함
    inputTime.setDate(inputTime.getDate() + 1);
  }

  return inputTime;
}

const AlarmSetting: React.FunctionComponent = () => {

  const navigate = useNavigate();

   // 알람 데이터 - react query
   const {
    data: alarmData,
    isLoading,
    isError,
    } = useQuery<AlarmData|null>({
      queryKey: ['alarmData'],
      queryFn: fetchAlarmData,
    })


  // const [isAlarmOn, setIsAlarmOn] = useState(true);
  // const [timeInterval, setTimeInterval] = useState(init.time);
  // const [repeatCnt, setRepeatCnt] = useState(init.repeat);
  // const [selectedWeak, setSelectedWeak] = useState(init.selectedWeek);
  const [reservedTime, setReservedTime] = useState(new Date());
  const [isTimeDropDownOpen, setIsTimeDropDownOpen] = useState(false);
  const [isRepeatDropDownOpen, setIsRepeatDropDownOpen] = useState(false);
  const [isHourDropDownOpen, setIsHourDropDownOpen] = useState(false);
  const [isMinuteDropDownOpen, setIsMinuteDropDownOpen] = useState(false);
  const [isResultModalOpen, setIsResultModalOpen] = useState(false);
  const [hour, setHour] = useState('00');
  const [minute, setMinute] = useState('00');
  const [second, setSecond] = useState('00');
  const [alarmState, setAlarmState] = useState<AlarmData>({
    id: 0,
    alarmTime: '',
    daysOfWeek: '0000000',
    repeatFrequency: 0,
    repeatInterval: 0,
    musicFilePath: '',
    isActive: 0,
  });

  const { mutate: updateAlarm } = useMutation({
    mutationFn: updateAlarmData,
    onSuccess: () => {
      console.log("알람이 성공적으로 수정되었습니다.");
      setIsResultModalOpen(true);
    },
    onError: () => {
      console.error("알람 수정에 실패했습니다.");
    },
  });

  useEffect(() => {
    if (alarmData) {
      setAlarmState(alarmData);
      const [alarmHour, alarmMinute, alarmSecond] = alarmData.alarmTime.split(':');
      setHour(alarmHour);
      setMinute(alarmMinute);
      setSecond(alarmSecond);
    }
  }, [alarmData]);

  const handleInputChange = (field: keyof AlarmData, value: any) => {
    setAlarmState((prevState) => ({
      ...prevState,
      [field]: value,
    }));
  };
  
  const handleSelectWeek = (dayIndex: number) => {
    let daysArray = alarmState.daysOfWeek.split('');  
    if (daysArray.length !== 7) {
      daysArray = ['0', '0', '0', '0', '0', '0', '0']; 
    }
    daysArray[dayIndex] = daysArray[dayIndex] === '1' ? '0' : '1';  
    handleInputChange('daysOfWeek', daysArray.join('')); 
  };

  if (isLoading) {
    return <div>로딩 중...</div>;
  }

  if (isError) {
    return <div>알람 데이터를 불러오는 중 오류가 발생했습니다.</div>;
  }

  
  

  return (
    <Container
      onClick={() => {
        if (isRepeatDropDownOpen) setIsRepeatDropDownOpen(false);
        if (isTimeDropDownOpen) setIsTimeDropDownOpen(false);
      }}
    >
      <ContentsContainer>
        <ToggleContainer $isAlarmOn={alarmState.isActive === 1}>
          <div
            className="toggleBackground"
            onClick={() => {
              handleInputChange('isActive', alarmState.isActive === 1 ? 0 : 1);
            }}
          >
            <div className="circle"></div>
          </div>
        </ToggleContainer>
        <TimeContainer>
          {!isHourDropDownOpen ? (
            <Time>{hour}</Time>
          ) : (
            <TimePicker>
              {hours.map(item => {
                return (
                  <li
                    key={item}
                    onClick={() => {
                    setHour(item);
                    setIsHourDropDownOpen(false);
                    }}
                  >
                    {item}
                  </li>
                );
              })}
            </TimePicker>
          )}
          <hr/>
          {!isMinuteDropDownOpen ? (
            <Time>{minute}</Time>
          ) : (
            <TimePicker>
              {minutes.map(item => {
                return (
                  <li
                    key={item}
                    onClick={() => {
                    setMinute(item);
                    setIsMinuteDropDownOpen(false);
                    }}
                  >
                    {item}
                  </li>
                );
              })}
            </TimePicker>
          )}
        </TimeContainer>
        <ButtonContainer>
          <GlobalBtn
            text="알람 수정"
            onClick={() => {
              setIsHourDropDownOpen(true);
              setIsMinuteDropDownOpen(true);
            }}
            $padding={8}
          ></GlobalBtn>
        </ButtonContainer>
        <SettingContainer>
          <div className="settingItem">
            <h1>요일반복</h1>
            <ul className="optionContainer">
              {week.map((item, index) => {
                return (
                  <li
                  key={index}
                  className={alarmState.daysOfWeek[index] === '1' ? 'selected' : ''}
                  onClick={() => handleSelectWeek(index)}
                >
                    {item}
                  </li>
                );
              })}
            </ul>
          </div>
          <div className="settingItem">
            <h1>다시 울리기</h1>
            <div className="optionContainer">
              {!isTimeDropDownOpen ? (
                <button
                  onClick={() => {
                    console.log('발동');
                    setIsTimeDropDownOpen(true);
                  }}
                >
                  {alarmState.repeatFrequency}
                </button>
              ) : (
                <button></button>
              )}{' '}
              분 마다
              {!isRepeatDropDownOpen ? (
                <button
                  onClick={() => {
                    setIsRepeatDropDownOpen(true);
                  }}
                >
                  {alarmState.repeatInterval}
                </button>
              ) : (
                <button />
              )}{' '}
              번
            </div>
            {isTimeDropDownOpen && (
              <ul className="dropDown timeDropDown">
                {timeIntervalList.map(item => {
                  return (
                    <li
                      onClick={() => {
                      handleInputChange('repeatFrequency', item);
                      setIsTimeDropDownOpen(false);
                      }}
                    >
                      {item}
                    </li>
                  );
                })}
              </ul>
            )}
            {isRepeatDropDownOpen && (
              <ul className="dropDown repeatDropDown">
                {repeatCntList.map(item => {
                  return (
                    <li
                      onClick={() => {
                      handleInputChange('repeatInterval', item);
                      setIsRepeatDropDownOpen(false);
                      }}
                    >
                      {item}
                    </li>
                  );
                })}
              </ul>
            )}
          </div>
        </SettingContainer>
        <ButtonContainer>
          <GlobalBtn  // 여기를 수정해야 합니다.
            text="저 장"
            $padding={10}
            onClick={()=> {
              const updatedAlarmState = {
                ...alarmState,
                alarmTime: `${hour}:${minute}:00`
              };
              console.log("Updating alarm with:", updatedAlarmState);
              updateAlarm(updatedAlarmState);
            }}
          ></GlobalBtn>
        </ButtonContainer>
      </ContentsContainer>
      {isResultModalOpen && (
        <ModalOverlay>
          <Modal>
            <div className="description">
              <span>
                {reservedTime.getMonth() + 1}월 {reservedTime.getDate()}일,{' '}
                {reservedTime.getHours()}시 {reservedTime.getMinutes()}분{' '}
              </span>

              <span>알람이 설정되었습니다.</span>
            </div>
            <div className="buttonContainer">
              <GlobalBtn
                text="확인"
                onClick={() => {
                  navigate('/');
                }}
                $bgColor="var(--darkblue-color)"
                $textColor="var(--white-color)"
                $padding={5}
              ></GlobalBtn>
            </div>
          </Modal>
        </ModalOverlay>
      )}
      <NavBar props={SoleMainNavBarProps}></NavBar>
    </Container>
  );
};

export default AlarmSetting;

const Container = styled.div`
  display: flex;
  width: 100%;
  height: 100vh;
  color: var(--white-color);
  background-color: var(--darkblue-color);
  align-items: center;
  flex-direction: column;
  z-index: -1;

  // * {
  //   border: 1px solid lime;
  // }
`;
const ContentsContainer = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
  align-items: center;
  margin: 4rem 0 4rem 0;
`;

const TimeContainer = styled.div`
  display: flex;
  width: 100%;
  height: 60rem;
  box-sizing: border-box;
  padding: 4rem 0 4rem 0;
  hr {
    margin: 0;
  }
`;

const TimePicker = styled.ul`
  display: flex;
  flex-direction: column;
  overflow: scroll;
  align-items: center;

  width: 50%;
  &::-webkit-scrollbar {
    display: none;
  }
  li {
    font-size: 19rem;
    color: var(--lightblue-color);
  }
`;

const Time = styled.div`
  font-size: 25rem;
  font-weight: bold;

  color: white;
  display: flex;
  width: 50%;
  align-items: center;
  justify-content: center;
`;

const ToggleContainer =
  styled.div <
  {$isAlarmOn: boolean} >
  `
  display: flex;
  width: 100%;
  height: 10rem;
  padding: 3rem 0 3rem 0;
  justify-content: center;
  align-items: center;

  .toggleBackground {
  
    display: flex;
    position: relative;
    align-items: center;
    padding: 0;
    width: 15rem;
    height: 6rem;
    box-sizing: border-box;

    border-radius: 100px;
    background-color: ${props =>
      props.$isAlarmOn ? 'var(--green-color)' : 'grey'};
      
  }
  .circle {
    position: absolute;
    left: ${props => (props.$isAlarmOn ? '9rem' : '1rem')};
    display: flex;
    width: 5rem;
    height: 5rem;
    background-color: white;
    border-radius: 50px;
    transition: 0.3s;
  }
`;

const SettingContainer = styled.div`
  display: flex;
  width: 100%;
  height: 15rem;
  justify-content: center;
  margin: 8rem 0 8rem 0;
  h1 {
    display: flex;
    height: 35rem;
    justify-content: center;
    color: var(--white-color);
    font-size: 5rem;
    font-weight: bold;
    margin-bottom: 5rem;
  }
  ul {
    display: flex;
    height: 7rem;
    li {
      font-size: 3.5rem;
      margin: 0 0.2rem 0 0.2rem;
    }
  }
  .settingItem {
    display: flex;
    flex-direction: column;
    width: 50%;
    height: 18rem;
    .optionContainer {
      display: flex;
      height: 100%;
      align-items: end;
      justify-content: center;
      font-size: 3.5rem;
      font-weight: bold;
      color: var(--lightblue-color);
      position: relative;

      .selected {
        color: white;
        padding-bottom: 3rem;
      }

      button {
        font-family: 'Noto Sans KR';
        align-items: center;
        justify-content: center;
        width: 7rem;
        font-size: 3.9rem;
        font-weight: bold;
        display: flex;
        border: none;
        background-color: transparent;
        color: var(--lightblue-color);
        margin: 0 0.1rem 0 0.1rem;
      }
    }

    .dropDown {
      position: absolute;
      display: flex;
      flex-direction: column;
      overflow: scroll;
      height: 9rem;

      bottom: 39rem;
      li {
        font-family: 'Noto Sans KR';
        display: flex;
        box-sizing: border-box;
        padding: 0 0.5rem 0 0.5rem;
        font-size: 3.9rem;
        font-weight: bold;

        height: 7rem;
        justify-content: end;

        color: var(--lightblue-color);
      }
      &::-webkit-scrollbar {
        display: none;
      }
    }
    .timeDropDown {
      left: 36.5rem;
    }
    .repeatDropDown {
      left: 55rem;
    }
  }
`;

const ButtonContainer = styled.div`
  display: flex;
  width: 100%;
  height: 7rem;
  justify-content: center;
  margin: 1rem 0 1rem 0;
`;

const ModalOverlay = styled.div`
  display: flex;
  position: absolute;
  background-color: rgba(0, 0, 0, 0.7);
  width: 100%;
  height: 100%;
  justify-content: center;
  align-items: center;
  z-index: 1;
`;

const Modal = styled.div`
  display: flex;
  border: 1px solid black;
  background-color: white;
  border-radius: 15px;
  font-size: 4rem;
  color: black;
  width: 90%;
  height: 30%;
  z-index: 2;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  .description {
    display: flex;
    width: 90%;
    font-weight: bold;
    height: 40%;
    justify-content: space-evenly;
    align-items: center;
    flex-direction: column;

    box-sizing: border-box;
    padding: 0 2rem 0 2rem;
    line-height: 5rem;
    margin-bottom: 3rem;
  }

  .buttonContainer {
    display: flex;
    width: 100%;
    justify-content: center;
    height: 14%;
  }
`;