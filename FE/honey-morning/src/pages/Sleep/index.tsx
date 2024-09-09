import React, { useEffect, useState } from 'react';
import styled, { keyframes } from 'styled-components';

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




// SleepWakeLock 컴포넌트 정의
const SleepWakeLock = () => {
  const [wakeLock, setWakeLock] = useState<WakeLockSentinel | null>(null);
  const [isScreenDimmed, setIsScreenDimmed] = useState(false); // 화면 어둡게 처리할지 여부

  const [timer, setTimer] = useState("00:00:00");
  
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
    }, 10000);
  }


  // 페이지가 로드될 때 Wake Lock을 자동으로 요청
  // 터치 이벤트를 등록
  useEffect(() => {
    handleRequestWakeLock(); // 컴포넌트 로드 시 Wake Lock 자동 요청


    // 페이지 로드 시 타이머 1회 실행
    resetTimer();

    // 사용자 입력 이벤트 등록(터치나 클릭이 발생하면 타이머 리셋)
    window.addEventListener('touchstart', resetTimer);
    window.addEventListener('mousedown', resetTimer);


    // 컴포넌트 언마운트 시 Wake Lock 해제 및 터치 이벤트 제거
    return () => {
      if (wakeLock) {
        handleReleaseWakeLock(); // 다른 페이지로 이동할 때 Wake Lock 해제
      }
      window.removeEventListener('touchstart', resetTimer);
      window.removeEventListener('mousedown', resetTimer);
    };
  }, []);


  return (
    <>
    
    <Container>
      <WaveOne />
      <WaveTwo />
      <WaveThree />
      <HelpIcon>
        <span className="material-icons">help</span>
      </HelpIcon>
      <CurrentTime>{timer}</CurrentTime>
      <BeeImage src="sleepingbee.webp" alt="Sleeping Bee" />
      <Divider />
      <AlarmInfo>
        <span className="material-icons">alarm</span>
        {/* 06:05 span 태그 안에 내가 설정한 알람 시각 조회해서 보여줘야 함 */}
        <span>06:05</span>
      </AlarmInfo>
      <EndButton>잠자기 종료</EndButton>
        {/* 일정 시간 후 화면을 어둡게 처리 */}
        <Overlay isScreenDimmed={isScreenDimmed} />
    </Container>
    
    
    </>
    
  );
};

export default SleepWakeLock;
