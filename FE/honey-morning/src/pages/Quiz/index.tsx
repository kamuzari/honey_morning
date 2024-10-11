import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query'
import styled from 'styled-components';
import { instance } from '@/api/axios'
import STT from '@/component/STT';

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

export interface QuizData {
  id: number,
  briefId: number,
  question: string,
  answerNumber: number,
  option1: string,
  option2: string,
  option3: string,
  option4: string,
  selection: number,
}

interface ProgressBarProps {
  progress: number;
  isActive: boolean;
}

interface ProgressStepProps {
  completed: boolean;
  active: boolean;
  position?: string;
}

interface SelectBoxProps {
  isSelected: boolean;
}

// get 요청
const fetchQuizData = async(briefId: number): Promise<QuizData[]> => {
  try {
    const response = await instance.get(`/api/quizzes/${briefId}`);
    return response.data;
  } catch(error: any) {
    // console.log("fetchQuizData Error", error);
    throw error;
  }
}

const fetchAudio = async (quizId: number) => {
  try {
    const response = await instance.get(`/api/quizzes/audio/${quizId}`, {
      responseType: 'blob',
    });
    const audioBlob = new Blob([response.data], { type: 'audio/mpeg' });
    const audioUrl = URL.createObjectURL(audioBlob);
    // console.log('오디오를 성공적으로 받아왔습니다:', audioUrl);
    return { audioUrl, response };
  } catch (error) {
    // console.error('오디오를 받아오는데 실패했습니다:', error);
    throw error;
  }
};


// patch 요청
const saveQuizResult = async(quizResult: {id: number; selection: number}) => {
  try{
    const response = await instance.patch(`/api/quizzes`, quizResult)
    return response.data;
  } catch(error: any) {
    // console.log("saveQuizResult Error", error)
  }
}


const QuizSolution: React.FC = () => {
  
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  
  const briefId = 81;


  //prettier-ignore
  const [alarmStartData, setAlarmStartData] = useState<AlarmStartResponse | null>(
    queryClient.getQueryData<AlarmStartResponse>(['alarmStartData']) || null
  );
  // console.log('alarmStartData:', alarmStartData);
  // localStorage에서 alarmStartData 불러오기
  useEffect(() => {
    if (!alarmStartData) {
      const storedData = localStorage.getItem('alarmStartData');
      if (storedData) {
        const parsedData = JSON.parse(storedData) as AlarmStartResponse;
        setAlarmStartData(parsedData); // 상태 업데이트
        // console.log('localStorage에서 가져온 데이터:', parsedData);
      }
    }
  }, [alarmStartData]);

  //timer
  const [timeLeft, setTimeLeft] = useState(15);

  // quiz
  // prettier-ignore
  // const [quizData, setQuizData] = useState<QuizData[]>([]);
  const quizData = [
    {
      id: 9,
      briefId: 81,
      question: '최근 미슐랭 가이드에서 주목받고 있는 레스토랑은 무엇인가요?',
      answerNumber: 1,
      option1: '모수서울',
      option2: '그릴하우스',
      option3: '미슐랭레스토랑',
      option4: '파르마식당',
      quizUrl: '89c30570-b599-4331-bd6b-3f82f9f50d6c.mp3'
    },
    {
      id: 10,
      briefId: 81,
      question: '2040세대에서 증가하고 있는 건강 문제는 무엇인가요?',
      answerNumber: 1,
      option1: '무릎퇴행성관절염',
      option2: '심장병',
      option3: '당뇨병',
      option4: '비만',
      quizUrl: 'ef7e8ca4-378b-4fbf-90ab-bdb15abaa238.mp3'
    },
  ]
  const [currentQuizIndex, setCurrentQuizIndex] = useState(0);
  //prettier-ignore
  const [selectedAnswer, setSelectedAnswer] = useState<number|null>(null);
  const [isCorrect, setIsCorrect] = useState(false);
  const [isQuizActive, setIsQuizActive] = useState(false);
  const correctCountRef = useRef(0);

  // 정답 모달
  const  [showModal, setShowModal] = useState(false);

  // const [briefId, setBriefId] = useState<number | null>(null);

  const audioRef = useRef<HTMLAudioElement | null>(null);
  // console.log('퀴즈 페이지의 alarmStartData:', alarmStartData);

  // get
const { mutate: fetchQuizDataMutate } = useMutation({
  mutationFn: (id: number) => fetchQuizData(id),
  onSuccess: (data: QuizData[]) => {
    // console.log("퀴즈 데이터를 불러오는데 성공", data)
    // setQuizData(data);
    setIsQuizActive(true);
  },
  onError: (error) => {
    // console.error("퀴즈 데이터를 불러오는데 실패", error)
  }
})

//patch
const { mutate: saveQuizResultMutate } = useMutation({
  mutationFn: saveQuizResult,
  onSuccess: (response) => {
    // console.log("퀴즈 결과 저장 보냄", response);
  },
  onError: (error) => {
    // console.log("퀴즈 결과 저장 실패", error)
  }
})

// 퀴즈 페이지 로드 시 데이터 가져오기
useEffect(()=> {
  // if(briefId !== null) {
    fetchQuizDataMutate(briefId);
  // }
}, [briefId])


useEffect(() => {
  if (quizData.length > 0 && currentQuizIndex >= 0) {
    const currentQuiz = quizData[currentQuizIndex];
    // console.log("currentQuiz",currentQuiz);
    if (currentQuiz && currentQuiz.id !== undefined) {
      // console.log('현재 퀴즈 ID:', currentQuiz.id);
      fetchAudio(currentQuiz.id).then(({ audioUrl }) => {
        // console.log('오디오 URL:', audioUrl);
        if (audioRef.current) {
          audioRef.current.src = audioUrl;
          audioRef.current.play();
        }
      }).catch((error) => {
        console.error("오디오를 가져오는데 실패했습니다:", error);
      });
      setTimeLeft(15);
      setIsQuizActive(true);
    } else {
      console.error('현재 퀴즈 또는 퀴즈 ID가 정의되지 않았습니다:', currentQuiz);
    }
  }
}, [currentQuizIndex]);

useEffect(() => {
  let timer: NodeJS.Timeout;
  if (timeLeft > 0 && isQuizActive) {
    timer = setTimeout(() => setTimeLeft(timeLeft - 1), 1000);
  } else if (timeLeft === 0 && isQuizActive) {
    handleTimeUp();
  }
  return () => clearTimeout(timer);
}, [timeLeft, isQuizActive]);




// 타이머 설정
const handleTimeUp = () => {
  setIsQuizActive(false);

  const isAnswerCorrect = selectedAnswer === quizData[currentQuizIndex].answerNumber - 1;
  setIsCorrect(isAnswerCorrect);
  
  if (isAnswerCorrect) {
    correctCountRef.current += 1;
  }
  
  // console.log(correctCountRef);
  
  setShowModal(true);
  setTimeout(() => {
    setShowModal(false);

    // console.log(quizData);

    saveQuizResultMutate({
      id: alarmStartData.quizzes[currentQuizIndex].id,
      selection: selectedAnswer,
    })
    if (currentQuizIndex < quizData.length - 1) {
      setCurrentQuizIndex(currentQuizIndex + 1);
      setTimeLeft(15);
      setSelectedAnswer(null);
      setIsQuizActive(true);
    }
    
    if (currentQuizIndex >= 1) {navigate('/quizresult', { state: {correctCount: correctCountRef.current}})}
      
    
  }, 3000);  //모달 시간 조절
};

// 정답 처리
const handleAnswer = (index: number ) => {
  // console.log("정답 처리 메서드 실행")
  setSelectedAnswer(index);
  //handleTimeUp(); 선택 시 즉시 다음 문제로 (일정한 시간이라면 이 줄 삭제)
}

const currentOptions = quizData[currentQuizIndex] 
    ? [
        quizData[currentQuizIndex].option1,
        quizData[currentQuizIndex].option2,
        quizData[currentQuizIndex].option3,
        quizData[currentQuizIndex].option4,
      ]
    : [];

const progress = (currentQuizIndex / quizData.length) * 100 + 50;

  return (
    <Container>
      <audio ref={audioRef} controls hidden />
      <ProgressBarArea>
        <ProgressBarBackground />
        <ProgressBarFill progress={progress} isActive={isQuizActive} />
        {quizData.map((_, index) => (
        <ProgressStep
          key={index}
          completed={currentQuizIndex > index}
          active={currentQuizIndex >= index}
          position={`${((index + 1) / quizData.length) * 100}%`}
        >
          {index + 1}
        </ProgressStep>
      ))}
      </ProgressBarArea>
      <CharacterArea><img src="./images/happyBee.png"/></CharacterArea>
      <NoticeArea>정답을 선택하거나 말하세요. 
      </NoticeArea>
        <TimeLeftText timeLeft={timeLeft}>남은 시간: {timeLeft} 초</TimeLeftText>
      <Notices>
        { quizData[currentQuizIndex] &&
        <STT currentOptions={currentOptions} answer={currentOptions[quizData[currentQuizIndex].answerNumber - 1]} setAnswer={setSelectedAnswer}></STT>}
      </Notices>
      
      <SelectArea>
        {currentOptions.map((option, index) => (
          <SelectionBox 
            key={index} 
            onClick={() => !showModal && handleAnswer(index)}
            $isSelected={selectedAnswer === index}
            $isDisabled={showModal}
          >
            {option}
          </SelectionBox>
        ))}
      </SelectArea>
      {showModal && (
        <Modal $isCorrect={isCorrect}>
          <ModalContent>
            <p>{isCorrect ? '정답입니다!' : '오답입니다.'}</p>
            <p>정답: {currentOptions[quizData[currentQuizIndex]?.answerNumber - 1]}</p>
          </ModalContent>
        </Modal>
      )}
    </Container>
  );
};

export default QuizSolution;


const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
`;

const ProgressBarArea = styled.div`
  width: 80%;
  height: 60px;
  margin: 50px auto;
  position: relative;
  padding: 0; /* Remove padding so that the progress bar fits properly */
`;

const ProgressBarBackground = styled.div`
  width: 100%;
  height: 100%;
  background-color: var(--disabled-color);
  border-radius: 30px;
  position: relative; /* Keep it relative to align with fill */
  overflow: hidden; /* Ensure that the fill doesn't overflow */
`;

//prettier-ignore
const ProgressBarFill = styled.div<ProgressBarProps>`
  width: ${props => props.progress}%;
  height: 100%; /* Match the background's height */
  background-color: var(--darkblue-color);
  border-radius: 30px;
  position: absolute;
  top: 0;
  left: 0;
  transition: width 0.3s ease;
`;

//prettier-ignore
const ProgressStep = styled.div<ProgressStepProps>`
  position: absolute;
  top: -20px;
  left: ${props => props.position};
  transform: translateX(-50%);
  width: 50px;
  height: 50px;
  background-color: ${props => (props.completed ? 'var(--green-color)' : props.active ? '#000000' : 'var(--disabled-color)')};
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  color: white;
  transition: background-color 1s ease;
`;

const CharacterArea = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 30px 0 30px 0;
  height: 650px;
`;

const NoticeArea = styled.div`
  display: flex;
  justify-content: center;
  margin: 100px;
  font-size: 60px;
  font-weight: 700;
`;

const Notices = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const SelectArea = styled.div`
  display: grid;
  grid-template-columns: repeat(2,1fr);
  grid-gap: 20px;
  justify-content: center;
  max-width: 800px;
  margin: 0 auto;
`;

const SelectionBox = styled.div<{$isSelected: boolean, $isDisabled:boolean}>`
  background-color: ${props => props.$isDisabled ? 'var(--disabled-color)' :  props.$isSelected ? 'var(--darkblue-color)' : 'var(--lightblue-color)'};
  color: ${props => props.$isDisabled ? 'white' : props.$isSelected ? 'white' : 'var(--darkblue-color)'};
  pointer-events: ${props => props.$isDisabled ? 'none' : 'auto'};

  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 50px;
  cursor: pointer;
  font-weight: 800;

  width: 350px;
  height: 350px;
  margin: 50px;
  border: none;
  border-radius: 20px;
`;

const Modal = styled.div<{$isCorrect: boolean}>`
  background-color: ${props => props.$isCorrect ? 'var(--green-color)' : 'var(--red-color)'};
  color: ${props => props.$isCorrect ? 'var(--darkgreen-color)' : 'white'};
  height: 150px;
  width: 100%;
  bottom: 0;
  padding: 30px;
  // opacity: 80%;
  position: fixed;
`;

const ModalContent= styled.div`
  text-align: center;
  font-size: 50px;
  padding: 20px;
  font-weight: 500;
  p {
    padding: 5px;
  }
`;

const TimeLeftText = styled.div<{ timeLeft: number }>`
  font-size: 50px;
  font-weight: bold;
  text-align: center;
  margin: 0 0 5rem 0;
  color: ${({ timeLeft }) => 
    timeLeft > 5 
    ? 'black' 
    : `rgb(${255 - timeLeft * 20}, 0, 0)`};
  transition: color 0.3s ease;
`;