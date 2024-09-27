import React, {useState, useEffect} from 'react';
import styled from 'styled-components';
import {useNavigate} from 'react-router-dom';
import {useQuery, useQueryClient} from '@tanstack/react-query';
import {instance} from '@/api/axios';

interface AlarmStartData {
  songUrl: string;
  quizzes: Array<{
    id: number,
    briefId: number,
    question: string,
    answer: number,
    option1: string,
    option2: string,
    option3: string,
    option4: string,
    selection: number,
  }>;
  content: string;
}

const AlarmPage = () => {
  const navigate = useNavigate();
  const [isAlarmOn, setIsAlarmOn] = useState(false);
  const [time, setTime] = useState(new Date());
  const [currentTimer, setCurrentTimer] = useState('00:00:00');

  const queryClient = useQueryClient(); // 여기다 쓰는게 맞나?

  // 알람 가져오기
  const fetchAlarmData = async (): Promise<AlarmStartData> => {
    const {data} = await instance.post(`api/alarms/start`);
    return data;
  };

  //prettier-ignore
  const {
    data: alarmData,
    isLoading,
    error,
  } = useQuery<AlarmStartData>({
    queryKey: ['alarmData'],
    queryFn: fetchAlarmData,
  });

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
    navigate('/'); // 메인 페이지로 이동
  };

  if (isLoading) return <h2>로딩 중</h2>;
  if (error) return <h2>에러</h2>;

  return (
    <Container>
      <RemindButton onClick={handleRemindLater}>
        5분 뒤에 다시 알림
      </RemindButton>
      <CurrentTime>{currentTimer}</CurrentTime>
      {isAlarmOn ? (
        <div>
          <h2>알람이 울리고 있습니다!</h2>
          <audio autoPlay>
            {/* <source
              src="https://cdn1.suno.ai/dc1d94fa-975b-4eab-a391-dc55eb4cdcc5.mp3"
              type="audio/mpeg"
            /> */}
            <source
              src={alarmData?.songUrl}
              type="audio/mpeg"
            />
          </audio>
          <ButtonContainer>
            <StartBriefButton onClick={handleStartBriefing}>
              알람 해제+브리핑 시작
            </StartBriefButton>
            <SkipBriefButton onClick={handleSkipBriefing}>
              브리핑 스킵
            </SkipBriefButton>
          </ButtonContainer>
        </div>
      ) : (
        <h2>알람이 울리지 않고 있습니다.</h2>
      )}
    </Container>
  );
};

export default AlarmPage;

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

const RemindButton = styled.button``;

const ButtonContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 3rem;
`;

const StartBriefButton = styled.button`
  background-color: var(--darkblue-color);
  padding: 1em;
  border-radius: 30px;

  color: white;
  font-size: 3em;
`;

const SkipBriefButton = styled.button`
  text-decoration: underline;
  color: black;
  font-size: 2em;

  border: none;
  background-color: var(--yellow-color);

  margin: 1em auto 2em;
`;
