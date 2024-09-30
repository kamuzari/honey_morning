import React, {useState, useEffect} from 'react';
import styled from 'styled-components';
import {useNavigate} from 'react-router-dom';

const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  height: 100vh;
  background-color: var(--yellow-color);
  position: relative;
  overflow: hidden;
`;

const CurrentTime = styled.div`
  margin-top: 5rem;
  font-size: 10rem;
  color: black;
  font-weight: bold;
`;

const ButtonContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 3rem;
`;

const Button = styled.button`
  padding: 1rem 2rem;
  font-size: 1.5rem;
  background-color: var(--blue-color); /* 버튼 배경색 */
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;

  &:hover {
    background-color: var(--dark-blue-color); /* 버튼 호버 시 색상 */
  }
`;
interface AlarmData{
  morningCallUrl: string,
  quizzes : Quiz[],
  briefingContent: string,
  briefingContentUrl: string
}

interface Quiz{
  id: number,
  question: string,
  answer : number,
  option1 : string,
  option2 : string,
  option3 : string,
  option4 : string,
  quizUrl : string,
}
const AlarmPage = () => {
  const navigate = useNavigate();
  const [isAlarmOn, setIsAlarmOn] = useState(false);
  const [time, setTime] = useState(new Date());
  const [currentTimer, setCurrentTimer] = useState('00:00:00');

  const currentTime = () => {
    const currentDate = new Date();
    const hours = String(currentDate.getHours()).padStart(2, '0');
    const minutes = String(currentDate.getMinutes()).padStart(2, '0');
    const seconds = String(currentDate.getSeconds()).padStart(2, '0');
    setCurrentTimer(`${hours}:${minutes}:${seconds}`);
  };

  const startTimer = () => {
    setInterval(currentTime, 1000); // 매초 시간 업데이트
  };

  useEffect(() => {
    startTimer(); // 페이지 로드 시 타이머 시작

    const alarmTime = new Date(time.getTime() + 5000); // 5초 후 알람

    const checkAlarm = setInterval(() => {
      const currentTime = new Date();
      if (currentTime >= alarmTime && !isAlarmOn) {
        setIsAlarmOn(true); // 알람 시작
      }
    }, 1000); // 매초 알람 여부 확인

    return () => clearInterval(checkAlarm); // 언마운트 시 타이머 정리
  }, [time, isAlarmOn]);

  // 5분 뒤 다시 알람 기능
  const handleRemindLater = () => {
    const newTime = new Date();
    setTime(new Date(newTime.getTime() + 5 * 60000)); // 5분 뒤로 알람 설정
    setIsAlarmOn(false); // 알람 해제 후 다시 설정
  };

  // 브리핑 시작
  const handleStartBriefing = () => {
    navigate('/briefing'); // 브리핑 페이지로 이동
  };

  // 브리핑 스킵 및 메인 페이지 이동
  const handleSkipBriefing = () => {
    setIsAlarmOn(false); // 알람 해제
    navigate('/main'); // 메인 페이지로 이동
  };

  return (
    <Container>
      <Button onClick={handleRemindLater}>5분 뒤에 다시 알림</Button>
      <CurrentTime>{currentTimer}</CurrentTime>
      {isAlarmOn ? (
        <div>
          <h2>알람이 울리고 있습니다!</h2>
          <audio autoPlay>
            <source
              src="https://cdn1.suno.ai/dc1d94fa-975b-4eab-a391-dc55eb4cdcc5.mp3"
              type="audio/mpeg"
            />
          </audio>
          <ButtonContainer>
            <Button onClick={handleStartBriefing}>브리핑 시작</Button>
            <Button onClick={handleSkipBriefing}>브리핑 스킵</Button>
          </ButtonContainer>
        </div>
      ) : (
        <h2>알람이 울리지 않고 있습니다.</h2>
      )}
    </Container>
  );
};

export default AlarmPage;
