import React, { useEffect, useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import  { instance } from '@/api/axios'
import styled, { keyframes } from 'styled-components';

import WaveBackGround from '@/component/WaveBackGround';
import Modal2 from '@/component/Modal2';
import Modal3 from '@/component/Modal3';

interface Quiz {
  id: number;
  question: string;
  answer: number;
  option1: string;
  option2: string;
  option3: string;
  option4: string;
  quizUrl: string;
}

interface AlarmStartResponse {
  morningCallUrl: string;
  quizzes: Quiz[];
  briefingContent: string;
  briefingContentUrl: string;
  briefingId: number;
}


// 알람 정보 조회
const fetchAlarmData = async () => {
  const token = sessionStorage.getItem('access');
  const response = await instance.get(`/api/alarms`, {
    headers: {
      access: token,
    },
  });
  return response.data;
};

// 10분전에 정보를 가져오기 위한 용도
const fetchAlarmStartDataFn = async (): Promise<AlarmStartResponse> => {
  const token = sessionStorage.getItem('access');
  const response = await instance.post<AlarmStartResponse>(`/api/alarms/start`,{}, {
    headers: {
      access: token,
    },
  });
  return response.data;
};


// SleepWakeLock 컴포넌트 정의
const SleepWakeLock = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  //prettier-ignore
  const userInfo = queryClient.getQueryData<{id: number, role: string, email: string, username: string}>(['userInfo']);
  const username = userInfo ? userInfo.username : null;

  // 알람을 조회해서 저장 api/alarms
  const { data: alarmData, error: alarmError, isSuccess: isAlarmDataSuccess } = useQuery({
    queryKey: ['alarmData'],
    queryFn: fetchAlarmData,
    enabled: true,
  });

  useEffect(() => {
    if (isAlarmDataSuccess && alarmData) {
      // console.log('AlarmData here:', alarmData); // 데이터를 확인하기 위한 콘솔 출력
      // 추가적으로 처리할 로직이 있다면 여기에 작성
    }
  
    if (alarmError) {
      console.error('AlarmData fetch error:', alarmError); // 에러 처리
    }
  }, [isAlarmDataSuccess, alarmData, alarmError]);

  // 컴포넌트가 처음 마운트될 때 localStorage에서 데이터를 불러오는 부분
  useEffect(() => {
    const storedAlarmStartData = localStorage.getItem('alarmStartData');
    if (storedAlarmStartData) {
      const parsedData = JSON.parse(storedAlarmStartData);
      queryClient.setQueryData(['alarmStartData'], parsedData); // queryClient에 저장
    }
  }, [queryClient]);

  // api/alarms/start 데이터 저장
  const { data: alarmStartData } = useQuery({
    queryKey: ['alarmStartData'],
    queryFn: fetchAlarmStartDataFn,
    enabled: false,
  })
  

  // 10분전
  const { mutate: startAlarmMutation } = useMutation<AlarmStartResponse, Error, void>({
    mutationFn: fetchAlarmStartDataFn,
    onSuccess: (data) => {
      // 요청 성공 시, 데이터를 전역 상태에 저장
      // console.log('AlarmStartData after mutation:', data);
      queryClient.setQueryData(['alarmStartData'], data);
      localStorage.setItem('alarmStartData', JSON.stringify(data)); 
    },
    onError: (error) => {
      console.error("Error fetching alarm start data:", error);
    }
  });

  useEffect(() => {
    // localStorage에서 데이터를 가져와 확인
    const storedAlarmStartData = localStorage.getItem('alarmStartData');
    if (storedAlarmStartData) {
      const parsedData = JSON.parse(storedAlarmStartData);
      // console.log('localStorage에 저장된 데이터:', parsedData); // localStorage에서 가져온 데이터 확인
      queryClient.setQueryData(['alarmStartData'], parsedData); // queryClient에 저장
    } else {
      // console.log('localStorage에 alarmStartData가 존재하지 않습니다.');
    }
  }, [queryClient]);
  
  
  const [wakeLock, setWakeLock] = useState<WakeLockSentinel | null>(null);
  const [isScreenDimmed, setIsScreenDimmed] = useState(false);
  const [timer, setTimer] = useState("00:00:00");
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [modalOpen, setModalOpen] = useState(false);
  const [helpModalOpen, setHelpModalOpen] =  useState(false);
  const [tooShortModalOpen, setTooShortModalOpen] = useState(false);
  
  const startTimeRef = useRef<number>(Date.now());
  const timerId = useRef<NodeJS.Timeout | null>(null);
  const timerIntervalId = useRef<NodeJS.Timeout | null>(null);

  // useEffect(() => {
  //   if (alarmData) {
  //     startTimer();
  //     handleRequestWakeLock();
  //   }
  // }, [alarmData]);
  useEffect(() => {
    if (alarmData) {
      startTimer();
      handleRequestWakeLock();
  
      // 테스트를 위해 컴포넌트 마운트 시 데이터를 요청하고 AlarmPage로 이동
      startAlarmMutation();
    }
  }, [alarmData]);
  
  // useEffect(() => {
  //   const fetchSleepData = async () => {
  //     try {
  //       const token = sessionStorage.getItem('access');
  //       console.log('Fetching sleep data...');
  //       const response = await instance.get(`/api/alarms/sleep`, {
  //         headers: {
  //           access: token,
  //         }
  //       });
  //       console.log('API response:', response.data);
  //       setIsLoading(false);
  //     } catch(error) {
  //       console.error('API 호출 중 오류 발생:', error);
  //       if (error.response) {
  //         console.log('응답 데이터:', error.response.data);
  //         console.log('응답 상태 코드:', error.response.status);
  //         console.log('응답 헤더:', error.response.headers);
  //         if (error.response.status === 400) {
  //           setTooShortModalOpen(true);
  //         }
  //       } else if (error.request) {
  //         console.log('요청이 전송되었지만 응답이 수신되지 않음:', error.request);
  //       } else {
  //         console.log('오류 설정 중 문제 발생:', error.message);
  //       }
  //       // setError('데이터를 불러오는 중 오류가 발생했습니다.');
  //       setIsLoading(false)
  //     }
  //   }
  //   fetchSleepData();
  // }, [])

  //진입 금지 무력화 -> 실제는 얘를 지우고 바로 위 useEffect를 살리면 됨
  useEffect(() => {
    setIsLoading(false);
  }, []);

  useEffect(() => {
    if (!isLoading && !tooShortModalOpen) {
      // console.log('Starting timer and wake lock...');
      startTimer();
      handleRequestWakeLock();
      resetTimer();

      window.addEventListener('touchstart', resetTimer);
      window.addEventListener('mousedown', resetTimer);

      return () => {
        if (timerIntervalId.current) clearInterval(timerIntervalId.current);
        if (timerId.current) clearTimeout(timerId.current);
        if (wakeLock) handleReleaseWakeLock();
        window.removeEventListener('touchstart', resetTimer);
        window.removeEventListener('mousedown', resetTimer);
      };
    }
  }, [isLoading, tooShortModalOpen]);

  
  const startTimer = () => {
    timerIntervalId.current = setInterval(() => {
      const currentTime = new Date();
      const hours = String(currentTime.getHours()).padStart(2, "0");
      const minutes = String(currentTime.getMinutes()).padStart(2, "0");
      const seconds = String(currentTime.getSeconds()).padStart(2, "0");
      setTimer(`${hours}:${minutes}:${seconds}`);

       // 요일 확인 (월화수목금토일 순서)
    const daysOfWeekMap = ['월', '화', '수', '목', '금', '토', '일'];
    let currentDayIndex = currentTime.getDay() - 1;
    if (currentDayIndex === -1) {
      currentDayIndex = 6; // 일요일을 6으로 설정
    }

    // 알람이 울리는 요일 및 시간 확인
    const alarmDays = alarmData?.daysOfWeek || "0000000";

    // 현재 요일에 알람이 활성화되어 있는지 확인
    if (alarmDays[currentDayIndex] === '1' && `${hours}:${minutes}` === alarmData.alarmTime.slice(0, 5)) {
      clearInterval(timerIntervalId.current); // 타이머 중지
      navigate('/alarm'); // 알람 페이지로 이동
    }

    // 알람 시간 10분 전일 때 데이터 요청
    if (alarmData) {
      const alarmTime = new Date();
      const [alarmHour, alarmMinute] = alarmData.alarmTime.split(':');
      alarmTime.setHours(Number(alarmHour));
      alarmTime.setMinutes(Number(alarmMinute));

      if (`${hours}:${minutes}` === `${String(alarmTime.getHours()).padStart(2, '0')}:${String(alarmTime.getMinutes()).padStart(2, '0')}`) {
        startAlarmMutation();
      }
    }

    }, 1000);
  };
  
  
  const calculateElapsedTime = (): number => {
    const now = Date.now();
    return Math.floor((now - startTimeRef.current) / 1000);
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
  
  
  const handleRequestWakeLock = async () => {
    try {
      if ('wakeLock' in navigator) {
        const lock = await (navigator as any).wakeLock.request('screen');
        setWakeLock(lock);

        lock.addEventListener('release', () => {
          // console.log('Wake Lock 해제');
          setIsScreenDimmed(false);
        });
        // console.log('Wake Lock 활성화');
      } else {
        console.log('해당 브라우저에서 Wake Lock이 지원되지 않습니다.');
      }
    } catch (err) {
      console.error(err);
    }
  };

  const handleReleaseWakeLock = async () => {
    if (wakeLock) {
      await wakeLock.release();
      setWakeLock(null);
      setIsScreenDimmed(false);
      // console.log('Wake Lock 해제');
    }
  };
  
  
  // 타이머를 리셋하고 화면을 어둡게 처리하는 함수
  const resetTimer = () => {
    if (timerId.current) {
      clearTimeout(timerId.current); // 기존 타이머 취소
    }
    // console.log('타이머 리셋');
    setIsScreenDimmed(false); // 터치 시 화면 밝게 설정

    // 일정 시간 후 화면을 어둡게 처리하는 새로운 타이머 설정(5초)
    timerId.current = setTimeout(() => {
      setIsScreenDimmed(true);
    }, 5000);
  };

  
  
  const getModalContent = () => {
    const elapsedTime = calculateElapsedTime();
    if (elapsedTime < 10) {
      // 2시간 미만일 때의 모달창
      return {
        header: '수면 경고',
        body: (
          <>
          수면 시간: {elapsedTime}초{"\n\n"}
          정말 수면을 종료하시겠습니까?{"\n\n"}
          수면 시간이 <HighlightedText>2시간</HighlightedText> 이하입니다.{"\n\n"}
          지금 수면을 종료하면 브리핑이 생성되지 않고, 기록이 남지 않습니다.{"\n\n\n"}
          계속하시겠습니까?
        </>
        ),
        actions:(
          <>
            <ActionButton onClick={() => navigate('/')}>네</ActionButton>
            <ActionButton onClick={() => setModalOpen(false)}>아니오</ActionButton>

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
          {userInfo.username}님의 관심사를 기준으로{"\n"}
          현재 시간까지의 브리핑을 준비했습니다.{"\n\n"}
          수면을 종료하고, 브리핑을 시작하시겠습니까?{"\n\n"}
          <AlertText>알람 시각보다 빨리 일어난 경우,{"\n"}브리핑 생성에 약 10분 소요됩니다.</AlertText>
        </>
        ),
        actions: (
          <>
            <ActionButton onClick={() => navigate('/briefing')}>브리핑 듣기</ActionButton>
            <ActionButton onClick={() => navigate('/')} style={{ background: 'grey' }}>
              넘어가기
            </ActionButton>
          </>
        ),
      };
    }
  };
  
  const modalContent = getModalContent();

  if (isLoading) {
    return <div>로딩 중...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  if (tooShortModalOpen) {
    return (
      <Modal3
        isOpen={tooShortModalOpen}
        isClose={() => {
          setTooShortModalOpen(false);
          navigate('/');
        }}
        header="수면 불가"
        icon="error_outline"
        btnText="확인"
      >
        수면시간이 너무 짧아서<br/> 알람이 울릴 수 없습니다. <br/><br/>
        브리핑 서비스를 이용하시려면 <br/> 5시간 이상 취침하시기 바랍니다.
      </Modal3>
    );
  }
  
  
  
  
  
  return (
    <>
      <Container>
      <WaveBackGround /> 
      <HelpIcon onClick={() => setHelpModalOpen(true)}>
        <span className="material-icons">help</span>
      </HelpIcon>
        <CurrentTime>{timer}</CurrentTime>
        <BeeImage src="/images/sleepingBee.png" alt="Sleeping Bee" />
        <Divider />
        <AlarmInfo>
          <span className="material-icons">alarm</span>
          {/* span 태그 안에 내가 설정한 알람 시각 조회해서 보여줘야 함 */}
          <span>{alarmData && alarmData.alarmTime.slice(0,5)}</span>
        </AlarmInfo>
  
        <EndButton onClick={() => setModalOpen(true)}>잠자기 종료</EndButton>
  
        {modalOpen && (
        <Modal2
          isOpen={modalOpen}
          isClose={() => setModalOpen(false)}
          header={modalContent.header}
          icon="error_outline"
          actions={modalContent.actions}
        >
          {modalContent.body}
        </Modal2>
      )}

      {/* Help Modal with Modal3 content */}
      {helpModalOpen && (
        <Modal3
        isOpen={helpModalOpen}
        isClose={closeHelpModalHandler}
        header="도움말"
        icon="info"
        btnText="닫기"
      >
        올바른 브리핑 생성을 위해<br /><br />
        화면을 끄지 않고,<br /><br />
        핸드폰을 뒤집어 주세요.<br /><br />
        화면은 자동으로 어두워집니다.<br /><br /><br />
        배터리 잔량이 거의 없는 경우<br /><br />
        핸드폰에 충전기를 연결하고<br /><br />
        수면을 취하세요.<br />
      </Modal3>
      )}

        {/* 일정 시간 후 화면을 어둡게 처리 */}
        {isScreenDimmed && <Overlay />}
      </Container>
    </>
  );
};

export default SleepWakeLock;

const Overlay = styled.div`
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.95); // 화면을 어둡게 처리
    z-index: 1000; // 다른 콘텐츠 위에 표시
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

