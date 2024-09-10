import React from 'react';
import styled from 'styled-components';
import {useState} from 'react';
import GlobalBtn from '@/component/GlobalBtn';
import {useNavigate} from 'react-router-dom';

const Container = styled.div`
  display: flex;
  width: 100%;
  height: 100vh;
  color: var(--white-color);
  background-color: var(--darkblue-color);
  justify-content: center;
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
    margin-bottom: 7rem;
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
    .optionContainer {
      display: flex;
      height: 40rem;
      align-items: center;
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

const week = ['일', '월', '화', '수', '목', '금', '토'];
const timeIntervalList = [1, 5, 10, 15, 20, 25, 30];
const repeatCntList = [1, 2, 3, 5, 10];
const init = {
  selectedWeek: ['화', '목', '토'],
  hour: '12',
  minute: '17',
  time: 1,
  repeat: 10,
};
// "00"부터 "23"까지의 시간 배열 생성
const hours = Array.from({length: 24}, (_, i) => String(i).padStart(2, '0'));

// "00"부터 "59"까지의 분 배열 생성
const minutes = Array.from({length: 60}, (_, i) => String(i).padStart(2, '0'));

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
  const [isAlarmOn, setIsAlarmOn] = useState(true);
  const [timeInterval, setTimeInterval] = useState(init.time);
  const [repeatCnt, setRepeatCnt] = useState(init.repeat);
  const [isTimeDropDownOpen, setIsTimeDropDownOpen] = useState(false);
  const [isRepeatDropDownOpen, setIsRepeatDropDownOpen] = useState(false);
  const [isHourDropDownOpen, setIsHourDropDownOpen] = useState(false);
  const [isMinuteDropDownOpen, setIsMinuteDropDownOpen] = useState(false);
  const [isResultModalOpen, setIsResultModalOpen] = useState(false);
  const [selectedWeak, setSelectedWeak] = useState(init.selectedWeek);
  const [hour, setHour] = useState(init.hour);
  const [minute, setMinute] = useState(init.minute);
  const [reservedTime, setReservedTime] = useState(new Date());

  const navigate = useNavigate();

  const handleSelectWeak = (item: string) => {
    if (selectedWeak.includes(item)) {
      setSelectedWeak(prevItems => prevItems.filter(i => i !== item));
    } else {
      setSelectedWeak([item, ...selectedWeak]);
    }
  };

  return (
    <Container
      onClick={() => {
        if (isRepeatDropDownOpen) setIsRepeatDropDownOpen(false);
        if (isTimeDropDownOpen) setIsTimeDropDownOpen(false);
      }}
    >
      <ContentsContainer>
        <ToggleContainer $isAlarmOn={isAlarmOn}>
          <div
            className="toggleBackground"
            onClick={() => {
              setIsAlarmOn(!isAlarmOn);
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
          <hr></hr>
          {!isMinuteDropDownOpen ? (
            <Time>{minute}</Time>
          ) : (
            <TimePicker>
              {minutes.map(item => {
                return (
                  <li
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
              {week.map(item => {
                return (
                  <li
                    className={selectedWeak.includes(item) ? 'selected' : ''}
                    onClick={() => {
                      handleSelectWeak(item);
                    }}
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
                  {timeInterval}
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
                  {repeatCnt}
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
                        setTimeInterval(item);
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
                        setRepeatCnt(item);
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
          <GlobalBtn
            text="저 장"
            $padding={10}
            onClick={() => {
              setReservedTime(
                getAdjustedTime(parseInt(hour, 10), parseInt(minute, 10)),
              );
              setIsResultModalOpen(true);
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
                  navigate('/main');
                }}
                $bgColor="var(--darkblue-color)"
                $textColor="var(--white-color)"
                $padding={5}
              ></GlobalBtn>
            </div>
          </Modal>
        </ModalOverlay>
      )}
    </Container>
  );
};

export default AlarmSetting;
