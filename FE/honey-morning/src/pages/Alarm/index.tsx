import React, {useState, useEffect} from 'react';
import styled from 'styled-components';
import {useNavigate} from 'react-router-dom';
import {useQueryClient, useQuery} from '@tanstack/react-query';

const GlassmorphismClock = () => {
  const [time, setTime] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => {
      setTime(new Date());
    }, 1000);

    return () => {
      clearInterval(timer);
    };
  }, []);

  const secondsStyle = {
    transform: `translateX(-50%) rotate(${time.getSeconds() * 6}deg)`,
  };
  const minutesStyle = {
    transform: `translateX(-50%) rotate(${time.getMinutes() * 6}deg)`,
  };
  const hoursStyle = {
    transform: `translateX(-50%) rotate(${
      (time.getHours() % 12) * 30 + time.getMinutes() * 0.5
    }deg)`,
  };

  const formatTimeUnit = unit => {
    return unit.toString().padStart(2, '0');
  };

  const hours = time.getHours();
  const ampm = hours >= 12 ? 'PM' : 'AM';
  const displayHours = hours % 12 || 12;

  return (
    <ClockContainer>
      <TimeDisplay>
        <TimeUnit>
          <TimeBox>{formatTimeUnit(displayHours)}</TimeBox>
          <TimeBox>{formatTimeUnit(time.getMinutes())}</TimeBox>
          <TimeBox>{formatTimeUnit(time.getSeconds())}</TimeBox>
          <AmPm>{ampm}</AmPm>
        </TimeUnit>
      </TimeDisplay>
      <ClockFace>
        <HourHand style={hoursStyle} />
        <MinuteHand style={minutesStyle} />
        <SecondHand style={secondsStyle} />
        <CenterDot />
      </ClockFace>
    </ClockContainer>
  );
};

interface AlarmStartResponse {
  morningCallUrl: string;
  //quizzes: Quiz[];
  briefingContent: string;
  briefingContentUrl: string;
}

interface AlarmData {
  id: number;
  alarmTime: string;
  daysOfWeek: string;
  repeatFrequency: number;
  repeatInterval: number;
  isActive: number;
}

const AlarmPage = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  //prettier-ignore
  const alarmStartData = queryClient.getQueryData<AlarmStartResponse>(['alarmStartData']);
  let morningCallUrl =
    alarmStartData?.morningCallUrl ??
    'https://suno.com/song/0c1495dc-d5de-48e6-b1a5-ecc2fd7b758c';

  //prettier-ignore
  const alarmData = queryClient.getQueryData<AlarmData>(['alarmData']);
  const repeatFrequency = alarmData?.repeatFrequency ?? 1;
  const repeatInterval = alarmData?.repeatInterval || 0;

  const [isAlarmOn, setIsAlarmOn] = useState(false);
  const [alarmCount, setAlarmCount] = useState(0);
  const [time, setTime] = useState(new Date());

  useEffect(() => {
    const alarmTime = new Date(time.getTime() + 1000);

    const checkAlarm = setInterval(() => {
      const currentTime = new Date();
      if (
        currentTime >= alarmTime &&
        !isAlarmOn &&
        alarmCount < repeatFrequency
      ) {
        setIsAlarmOn(true);
        setAlarmCount(prevCount => prevCount + 1);
      }
    }, 1000);

    return () => clearInterval(checkAlarm);
  }, [time, isAlarmOn, alarmCount, repeatFrequency]);

  const handleRemindLater = () => {
    const newTime = new Date();
    setTime(new Date(newTime.getTime() + repeatInterval * 60000));
    setIsAlarmOn(false);
    navigate('/sleep');
  };

  const handleStartBriefing = () => {
    navigate('/briefing');
  };

  const handleSkipBriefing = () => {
    setIsAlarmOn(false);
    navigate('/');
  };

  return (
    <Container>
      <GlassCard>
        <GlassmorphismClock />
        {isAlarmOn ? (
          <>
            <audio autoPlay>
              <source
                src={morningCallUrl}
                type="audio/mpeg"
              />
            </audio>
            <Button onClick={handleStartBriefing}>브리핑 시작</Button>
            <SkipButton onClick={handleSkipBriefing}>
              오늘은 건너뛰기
            </SkipButton>
          </>
        ) : (
          <>
            <Button onClick={handleRemindLater}>
              {repeatInterval}분 후 다시 알림
            </Button>
          </>
        )}
      </GlassCard>
    </Container>
  );
};

export default AlarmPage;

const Container = styled.div`
  width: 1080px;
  height: 100vh;
  background: linear-gradient(45deg, #fdc727 0%, #ffe082 100%);
  margin: 0 auto;
  position: relative;
  overflow: hidden;
`;

const GlassCard = styled.div`
  // border: 2px solid rgba(0, 0, 0);
  background: rgba(253, 199, 39, 0.2);
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 50px;
  box-sizing: border-box;
`;

const Button = styled.button`
  padding: 20px 30px;
  font-size: 60px;
  background: #25387c;
  color: #ffffff;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin: 30px 0;
  width: 70%;
  padding: 25px 20px;

  &:hover {
    background: #1a2a5e;
    box-shadow: 0 8px 16px 0 rgba(37, 56, 124, 0.3);
  }
`;

const SkipButton = styled.button`
  background: none;
  border: none;
  color: black;
  font-size: 45px;
  cursor: pointer;
  text-decoration: underline;
  margin-top: 20px;
  transition: all 0.3s ease;

  &:hover {
    color: #1a2a5e;
  }
`;

const ClockContainer = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const TimeDisplay = styled.div`
  background: #25387c;
  backdrop-filter: blur(10px);
  border-radius: 30px;
  border: 2px solid rgba(253, 199, 39, 0.3);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.2);
  padding: 25px 10px;
  color: rgb(255, 255, 255);
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40px;
  width: 80%;
`;

const TimeUnit = styled.div`
  display: flex;
  justify-content: center;
  width: 100%;
`;

const TimeBox = styled.div`
  border-radius: 20px;
  padding: 25px;
  margin: 0 12px;
  font-size: 70px;
  font-weight: bold;
  min-width: 100px;
`;

const AmPm = styled.div`
  font-size: 60px;
  font-weight: bold;
  margin-left: 20px;
  display: flex;
  align-items: center;
`;

const ClockFace = styled.div`
  width: 500px;
  height: 500px;
  border-radius: 50%;
  position: relative;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  border: 2px solid rgba(253, 199, 39, 0.3);
  box-shadow: 0 16px 64px 0 rgba(31, 38, 135, 0.2);
  margin: 40px 0;
`;

const ClockHand = styled.div`
  position: absolute;
  bottom: 50%;
  left: 50%;
  transform-origin: 50% 100%;
  z-index: 10;
`;

const HourHand = styled(ClockHand)`
  width: 12px;
  height: 140px;
  background: #c4302b;
  border-radius: 4px;
`;

const MinuteHand = styled(ClockHand)`
  width: 10px;
  height: 180px;
  background: #5471ff;
  border-radius: 3px;
`;

const SecondHand = styled(ClockHand)`
  width: 6px;
  height: 200px;
  background: #1d3557;
`;

const CenterDot = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #25387c;
  transform: translate(-50%, -50%);
  z-index: 11;
`;

const AlarmMessage = styled.h2`
  color: #25387c;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin: 30px 0;
  font-size: 45px;
  text-align: center;
`;
