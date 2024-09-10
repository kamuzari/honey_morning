import React, { useEffect, useState } from 'react';
import styled, { keyframes } from 'styled-components';
import { useNavigate } from 'react-router-dom';

const Overlay = styled.div<{ isScreenDimmed: boolean}>`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.95); // 화면을 어둡게 처리
  display: ${({ isScreenDimmed }) => (isScreenDimmed ? 'block' : 'none')}; // 화면을 어둡게 처리할지 여부
  z-index: 1000; // 다른 콘텐츠 위에 표시
`

// 물결 애니메이션 정의
const drift = keyframes`
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
`;

const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  height: 100vh;
  background-color: var(--lightblue-color); /* 배경색 설정 */
  position: relative;
  overflow: hidden;
`;

const WaveOne = styled.div`
  // opacity: 0.4;
  position: absolute;
  top: -330px;
  width: 3000px;
  height: 100%;
  transform-origin: 50% 48%;
  border-radius: 60%;
  background: var(--darkblue-color);
  animation: ${drift} 20000ms infinite linear;
  z-index: 1;
`;

const WaveTwo = styled(WaveOne)`
  background: #3773b7;
  opacity: 0.4;
  animation: ${drift} 16000ms infinite linear;
  z-index: 0;
`;

const WaveThree = styled(WaveOne)`
  background: #97a7e0;
  opacity: 0.3;
  animation: ${drift} 11000ms infinite linear;
  z-index: 0;
`;

const CurrentTime = styled.div`
  margin-top: 5rem;
  font-size: 10rem;
  color: white;
  font-weight: bold;
  z-index: 10;
`;

const BeeImage = styled.img`
  width: 50rem;
  height: 50rem;
  object-fit: cover;
  margin: 5px 0;
  border-radius: 16px;
  z-index: 10;
`;

const Divider = styled.div`
  width: 80%;
  height: 10px;
  background-color: white;
  z-index: 10;
`;

const AlarmInfo = styled.div`
  display: flex;
  width: 70%;
  align-items: center;
  justify-content: space-between;
  color: white;
  font-size: 8.5rem;
  font-weight: 600;
  gap: 10px;
  margin-bottom: 30px;
  .material-icons {
    font-size: 10rem;
    margin-top: 1.5rem;
  }
  z-index: 10;
`;


const EndButton = styled.button`
  background-color: rgba(255, 200, 40, 1);
  color: var(--darkblue-color);
  font-size: 4.5rem;
  font-weight: 600;
  border: none;
  padding: 25px 80px;
  border-radius: 12px;
  cursor: pointer;
  margin-bottom: 9rem;
  z-index: 10;
`;

const HelpIcon = styled.span`
.material-icons {
  position: absolute;
  top: 40px;
  right: 40px;
  color: white;
  font-size: 5rem;
  cursor: pointer;
  z-index: 10;
}
`;

// const WavyBackground = styled.div`
//   position: absolute;
//   bottom: 0;
//   width: 100%;
//   height: 150px;
//   background: url('wavy_background_image_url') no-repeat;
//   background-size: cover;
// `;



const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 1000;
`;

const Modal = styled.div`
  background-color: white;
  border-radius: 2rem;
  width: 45rem;
  min-height: 40rem;
  text-align: center;
  // padding: 2rem;
  z-index: 1001;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
`;

const ModalHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
  color: white;
  height: 7rem;
  background-color: var(--red-color);
  border-radius: 2rem 2rem 0 0;
  position: relative;
`;

const ModalIcon = styled.span`
  position: absolute;
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  font-size: 4rem;
  font-weight: light;
`;

const ModalTitle = styled.h2`
  margin: 0;
  font-size: 3rem;
  font-weight: bold;
`;

const ModalBody = styled.div`
  font-size: 2.5rem;
  font-weight: bold;
  margin: 5.5rem 5.5rem 1rem 5.5rem;
  text-align: center;
  white-space: pre-line; /* 줄바꿈이 가능하도록 설정 */
`;

const ModalActions = styled.div`
  display: flex;
  justify-content: center; /* 버튼을 중앙으로 배치 */
  padding: 1rem 4rem;
`;

const ActionButton = styled.button<{ disabled?: boolean }>`
  background-color: var(--darkblue-color);
  color: white;
  font-size: 2.3rem;
  font-weight: bold;
  min-width: 15rem;
  min-height: 7rem;
  border: none;
  padding: 1rem;
  margin: 3rem;
  border-radius: 8px;
  cursor: ${({ disabled }) => (disabled ? 'not-allowed' : 'pointer')};
`;

const HighlightedText = styled.span`
  color: red; /* 특정 텍스트만 빨간색으로 변경 */
`;

const AlertText = styled.span`
  color: red;
  font-size: 2rem;
`;

const HelpModalHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
  color: white;
  height: 7rem;
  background-color: var(--darkblue-color);
  border-radius: 2rem 2rem 0 0;
  position: relative;
`;

const HelpModalBody = styled.div`
  font-size: 2.5rem;
  font-weight: bold;
  margin: 5rem 5rem 3rem 5rem;
  text-align: center;
`;


// SleepWakeLock 컴포넌트 정의
const SleepWakeLock = () => {
  const navigate = useNavigate();

  const [wakeLock, setWakeLock] = useState<WakeLockSentinel | null>(null);
  const [isScreenDimmed, setIsScreenDimmed] = useState(false); // 화면 어둡게 처리할지 여부
  const [timer, setTimer] = useState("00:00:00");
  const [modalOpen, setModalOpen] = useState(false);
  const [elapsedTime, setElapsedTime] = useState(0); // 경과 시간을 관리하는 상태
  const [startTime, setStartTime] = useState<Date | null>(null); // 페이지 방문 시작 시간
  const [helpModalOpen, setHelpModalOpen] =  useState(false);
  const [alarmTime, setAlarmTime] = useState("13:29"); // 예시 알람 시간 임시 설정, 해당 시간이 되면 알람 페이지로 자동 이동



  let timerId: NodeJS.Timeout | null = null;

  const currentTimer = () => {
    const currentDate = new Date();
    const hours = String(currentDate.getHours()).padStart(2, "0");
    const minutes = String(currentDate.getMinutes()).padStart(2, "0");
    const seconds = String(currentDate.getSeconds()).padStart(2, "0");
    setTimer(`${hours}:${minutes}:${seconds}`)
  }
  const startTimer = () => {
    setInterval(currentTimer, 1000)
  }


  const calculateElapsedTime = () => {
    if (startTime) {
      const currentTime = new Date();
      const timeDiff = (currentTime.getTime() - startTime.getTime()) / 1000; // 초 단위 경과 시간
      setElapsedTime(timeDiff);
    }
  };

  const openModalHandler = () => {
    calculateElapsedTime(); // 모달 열기 전에 경과 시간 계산
    setModalOpen(true);
  };

  const closeModalHandler = () => {
    setModalOpen(false);
  };


  const openHelpModalHandler = () => {
    setHelpModalOpen(true); // 도움말 모달 열기
  };

  const closeHelpModalHandler = () => {
    setHelpModalOpen(false); // 도움말 모달 닫기
  };

  const handleOverlayClick = (e: React.MouseEvent<HTMLDivElement>) => {
    // e.target이 ModalOverlay이면 모달을 닫음
    if (e.target === e.currentTarget) {
      closeModalHandler();
      closeHelpModalHandler();
    }
  };


  const handleYesClick = () => {
    navigate('/'); // 메인 페이지로 이동
  };

  const handleNoClick = () => {
    closeModalHandler(); // 모달 닫기
  };

  const handleBriefingStart = () => {
    navigate('/briefing'); // 브리핑 페이지로 이동
  };

  const handleBriefingSkip = () => {
    navigate('/'); // 메인 페이지로 이동
  };



  startTimer()
  



  // Wake Lock을 요청하는 함수
  const handleRequestWakeLock = async () => {
    try {
      if ('wakeLock' in navigator) {
        const lock = await (navigator as any).wakeLock.request('screen');
        setWakeLock(lock);

        lock.addEventListener('release', () => {
          console.log('Wake Lock 해제');
          setIsScreenDimmed(false); // Wake Lock 해제 시 화면 밝게 설정
        });
        console.log('Wake Lock 활성화');
      } else {
        console.log('해당 브라우저에서 Wake Lock이 지원되지 않습니다');
      }
    } catch (err) {
      if (err instanceof Error) {
        console.error(`${err.name}, ${err.message}`);
      } else {
        console.error('알 수 없는 오류...');
      }
    }
  };

  // Wake Lock 해제 함수
  const handleReleaseWakeLock = async () => {
    if (wakeLock) {
      await wakeLock.release();
      setWakeLock(null);
      setIsScreenDimmed(false); // Wake Lock 해제 시 화면 밝게 설정
      console.log('Wake Lock 해제');
    }
  };


  // 타이머를 리셋하고 화면을 어둡게 처리하는 함수
  const resetTimer = () => {
    if (timerId){
      clearTimeout(timerId);  // 기존 타이머 취소
    }
    console.log('타이머 리셋')
    setIsScreenDimmed(false); // 터치 시 화면 밝게 설정

    // 일정 시간 후 화면을 어둡게 처리하는 새로운 타이머 설정(10초)
    timerId = setTimeout(() => {
      setIsScreenDimmed(true);
    }, 5000);
  }


  // 페이지가 로드될 때 Wake Lock을 자동으로 요청
  // 터치 이벤트를 등록
  useEffect(() => {

    // 페이지 초기 로드 시 현재 시간을 시작 시간으로 설정
    setStartTime(new Date());
    startTimer();

    const checkAlarmTime = setInterval(() => {
      const currentDate = new Date();
      const currentHours = String(currentDate.getHours()).padStart(2, "0");
      const currentMinutes = String(currentDate.getMinutes()).padStart(2, "0");
      const currentAlarm = `${currentHours}:${currentMinutes}`;

      if (currentAlarm === alarmTime) {
        navigate('/alarm'); // 알람 시간이 되면 Alarm 페이지로 이동
      }
    }, 1000); // 매초 알람 시간 확인


    handleRequestWakeLock(); // 컴포넌트 로드 시 Wake Lock 자동 요청

    // 페이지 로드 시 타이머 1회 실행
    resetTimer();

    // 사용자 입력 이벤트 등록(터치나 클릭이 발생하면 타이머 리셋)
    window.addEventListener('touchstart', resetTimer);
    window.addEventListener('mousedown', resetTimer);


    // 컴포넌트 언마운트 시 Wake Lock 해제 및 터치 이벤트 제거
    return () => {
      clearInterval(checkAlarmTime);
      if (wakeLock) {
        handleReleaseWakeLock(); // 다른 페이지로 이동할 때 Wake Lock 해제
      }
      window.removeEventListener('touchstart', resetTimer);
      window.removeEventListener('mousedown', resetTimer);
    };
  }, [alarmTime, navigate]);

  const getModalContent = () => {
    if (elapsedTime < 10) {
      // 2시간 미만일 때의 모달창
      return {
        header: '수면 경고',
        body: (
        <>
          체류시간 확인용: {elapsedTime}{"\n\n"}
          정말 수면을 종료하시겠습니까?{"\n\n"}
          수면 시간이 <HighlightedText>2시간</HighlightedText> 이하입니다.{"\n\n"}
          지금 수면을 종료하면 브리핑이 생성되지 않고, 기록이 남지 않습니다.{"\n\n\n"}
          계속하시겠습니까?
        </>
        ),
        actions:(
          <>
            <ActionButton onClick={handleYesClick}>네</ActionButton>
            <ActionButton onClick={handleNoClick}>아니오</ActionButton>
          </>
        )
      };
    } else {
      // 2시간 이상일 때의 모달 내용
      return {
        header: '수면 종료',
        body: (
        <>
          정말 수면을 종료하시겠습니까?{"\n\n"}
          OOO님의 관심사를 기준으로{"\n"}
          현재 시간까지의 브리핑을 준비했습니다.{"\n\n"}
          수면을 종료하고, 브리핑을 시작하시겠습니까?{"\n\n"}
          <AlertText>알람 시각보다 빨리 일어난 경우,{"\n"}브리핑 생성에 약 n분 소요됩니다.</AlertText>
        </>
        ),
        actions: (
          <>
            <ActionButton onClick={handleBriefingStart}>브리핑 듣기</ActionButton>
            <ActionButton onClick={handleBriefingSkip} style={{background:'grey'}}>넘어가기</ActionButton>
          </>
        ),
      };
    }
  };

  const modalContent = getModalContent();





  return (
    <>
      <Container>
        <WaveOne />
        <WaveTwo />
        <WaveThree />
        <HelpIcon onClick={openHelpModalHandler}>
          <span className="material-icons">help</span>
        </HelpIcon>
        <CurrentTime>{timer}</CurrentTime>
        <BeeImage src="sleepingbee.webp" alt="Sleeping Bee" />
        <Divider />
        <AlarmInfo>
          <span className="material-icons">alarm</span>
          {/* span 태그 안에 내가 설정한 알람 시각 조회해서 보여줘야 함 */}
          <span>{alarmTime}</span>
        </AlarmInfo>
  
        <EndButton onClick={openModalHandler}>잠자기 종료</EndButton>
  
        {modalOpen && (
          <ModalOverlay onClick={handleOverlayClick}>
            <Modal>
              <ModalHeader>
                <ModalIcon className="material-icons">error_outline</ModalIcon>
                <ModalTitle>{modalContent.header}</ModalTitle>
              </ModalHeader>
              <ModalBody>{modalContent.body}</ModalBody>
              <ModalActions>
                {modalContent.actions}
              </ModalActions>
            </Modal>
          </ModalOverlay>
        )}
  
        {/* 도움말 모달 */}
        {helpModalOpen && (
          <ModalOverlay onClick={handleOverlayClick}>
            <Modal>
              <HelpModalHeader>
                <ModalIcon className="material-icons">info</ModalIcon>
                <ModalTitle>도움말</ModalTitle>
              </HelpModalHeader>
              <HelpModalBody>
                  올바른 브리핑 생성을 위해<br/><br/>
                  화면을 끄지 않고,<br/><br/>
                  핸드폰을 뒤집어 주세요.<br/><br/>
                  화면은 자동으로 어두워집니다.<br/><br/><br/>
                  배터리 잔량이 거의 없는 경우<br/><br/>
                  핸드폰에 충전기를 연결하고<br/><br/>
                  수면을 취하세요.<br/>
              </HelpModalBody>
              <ModalActions>
                <ActionButton onClick={closeHelpModalHandler}>닫기</ActionButton>
              </ModalActions>
            </Modal>
          </ModalOverlay>
        )}

        {/* 일정 시간 후 화면을 어둡게 처리 */}
        <Overlay isScreenDimmed={isScreenDimmed} />
      </Container>
    </>
  );
};

export default SleepWakeLock;
