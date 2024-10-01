import React, {useState, useEffect} from 'react';
import styled from 'styled-components';
import {useNavigate} from 'react-router-dom';

const Container = styled.div`
  width: 1080px;
  height: 100vh;
  background: linear-gradient(45deg, #fdc727 0%, #ffe082 100%);
  margin: 0 auto;
  position: relative;
  overflow: hidden;
`;

const GlassCard = styled.div`
  background: rgba(253, 199, 39, 0.15);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(253, 199, 39, 0.3);
  box-shadow: 0 16px 64px 0 rgba(31, 38, 135, 0.2);
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  box-sizing: border-box;
`;

const Button = styled.button`
  padding: 20px 30px;
  font-size: 30px;
  background: #25387c;
  color: #ffffff;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin: 30px 0;
  width: 80%;

  &:hover {
    background: #1a2a5e;
    box-shadow: 0 8px 16px 0 rgba(37, 56, 124, 0.3);
  }
`;

const SkipButton = styled.button`
  background: none;
  border: none;
  color: #25387c;
  font-size: 24px;
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
  background: rgba(253, 199, 39, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 30px;
  border: 2px solid rgba(253, 199, 39, 0.3);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.2);
  padding: 30px;
  color: #25387c;
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
  background: rgba(253, 199, 39, 0.3);
  border-radius: 20px;
  padding: 20px;
  margin: 0 10px;
  font-size: 48px;
  font-weight: bold;
  min-width: 100px;
`;

const AmPm = styled.div`
  font-size: 36px;
  font-weight: bold;
  margin-left: 20px;
  display: flex;
  align-items: center;
`;

const ClockFace = styled.div`
  width: 400px;
  height: 400px;
  border-radius: 50%;
  position: relative;
  background: rgba(253, 199, 39, 0.2);
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
  width: 8px;
  height: 100px;
  background: #c4302b;
  border-radius: 4px;
`;

const MinuteHand = styled(ClockHand)`
  width: 6px;
  height: 140px;
  background: #5471ff;
  border-radius: 3px;
`;

const SecondHand = styled(ClockHand)`
  width: 2px;
  height: 160px;
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
  font-size: 36px;
  text-align: center;
`;

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

const AlarmPage = () => {
  const navigate = useNavigate();
  const [isAlarmOn, setIsAlarmOn] = useState(false);
  const [time, setTime] = useState(new Date());

  useEffect(() => {
    const alarmTime = new Date(time.getTime() + 5000); // 5초 후 알람

    const checkAlarm = setInterval(() => {
      const currentTime = new Date();
      if (currentTime >= alarmTime && !isAlarmOn) {
        setIsAlarmOn(true); // 알람 시작
      }
    }, 1000); // 매초 알람 여부 확인

    return () => clearInterval(checkAlarm); // 언마운트 시 타이머 정리
  }, [time, isAlarmOn]);

  const handleRemindLater = () => {
    const newTime = new Date();
    setTime(new Date(newTime.getTime() + 5 * 60000)); // 5분 뒤로 알람 설정
    setIsAlarmOn(false); // 알람 해제 후 다시 설정
    navigate('/sleep'); // 메인 페이지로 이동
  };

  const handleStartBriefing = () => {
    navigate('/briefing'); // 브리핑 페이지로 이동
  };

  const handleSkipBriefing = () => {
    setIsAlarmOn(false); // 알람 해제
    navigate('/'); // 메인 페이지로 이동
  };

  return (
    <Container>
      <GlassCard>
        <GlassmorphismClock />
        {isAlarmOn ? (
          <>
            <AlarmMessage>알람이 울리고 있습니다!</AlarmMessage>
            <audio autoPlay>
              <source
                src="https://cdn1.suno.ai/dc1d94fa-975b-4eab-a391-dc55eb4cdcc5.mp3"
                type="audio/mpeg"
              />
            </audio>
            <Button onClick={handleStartBriefing}>브리핑 시작</Button>
            <SkipButton onClick={handleSkipBriefing}>브리핑 스킵</SkipButton>
          </>
        ) : (
          <>
            <AlarmMessage>알람이 울리지 않고 있습니다.</AlarmMessage>
            <Button onClick={handleRemindLater}>5분 후 다시 알림</Button>
          </>
        )}
      </GlassCard>
    </Container>
  );
};

export default AlarmPage;
