// import QuizOption from '@/component/QuizOption'
import {useState, useEffect, useRef} from 'react';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';

// ??? question과 같은 변수명 뭔지, 정답은 어떤 방식으로 넘겨주는지
interface QuizData {
  "id": number,
  "briefId": number,
  "question": string,
  "answer": number,
  "option1": string,
  "option2": string,
  "option3": string,
  "option4": string,
  "selection": number,
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

const QuizSolution: React.FC = () => {
  const navigate = useNavigate();

  //timer
  const [timeLeft, setTimeLeft] = useState(10);

  // quiz
  // prettier-ignore
  const [quizData, setQuizData] = useState<QuizData[]>([]);
  const [currentQuizIndex, setCurrentQuizIndex] = useState(0);
  //prettier-ignore
  const [selectedAnswer, setSelectedAnswer] = useState<number|null>(null);
  const [isCorrect, setIsCorrect] = useState(false);
  const [isQuizActive, setIsQuizActive] = useState(false);
  const correctCountRef = useRef(0);

  // 정답 모달
  const  [showModal, setShowModal] = useState(false);

// 임시 퀴즈 데이터 -> 백 또는 서버 연동 예정
useEffect(() => {
  const fetchQuizData = async() => {
    const data: QuizData[] = [
      {
        id: 1,
        briefId: 1,
        question: "우리팀의 이름은?",
        option1: "이세돌",
        option2: "징버거",
        option3: "플레이브",
        option4: "싸피니까",
        answer: 3,
        selection: 0
      },
      {
        id: 2,
        briefId: 1,
        question: "우리팀 팀장의 이름은?",
        option1: "송창용",
        option2: "한도형",
        option3: "김병연",
        option4: "김윤홍",
        answer: 0,
        selection: 0
      }
    ];
    setQuizData(data);
  }
  fetchQuizData();
}, []);

// 현재 퀴즈 위치
useEffect(() => {
  if(quizData.length > 0){
    speakQuestion();
    setIsQuizActive(true);
  }
}, [currentQuizIndex])

useEffect(() => {
  if (quizData.length > 0 && currentQuizIndex === 0) {
    setIsQuizActive(true); 
  }
}, [quizData]);


const speakQuestion = () => {
  const speech = new SpeechSynthesisUtterance(quizData[currentQuizIndex].question);
  window.speechSynthesis.speak(speech);
};

// SpeechSynthesisUtterance.lang: 언어 설정
// SpeechSynthesisUtterance.pitch: 음높이 설정
// SpeechSynthesisUtterance.rate: 말하는 속도 설정
// SpeechSynthesisUtterance.text: 이용할 텍스트 설정
// SpeechSynthesisUtterance.voice: 보이스 설정
// SpeechSynthesisUtterance.volume: 볼륨 설정


// 타이머 설정
const handleTimeUp = () => {
  setIsQuizActive(false);

  const isAnswerCorrect = selectedAnswer === quizData[currentQuizIndex].answer;
  setIsCorrect(isAnswerCorrect);

  if (isAnswerCorrect) {
    correctCountRef.current += 1;
  }

  console.log(correctCountRef);

  setShowModal(true);
  setTimeout(() => {
    setShowModal(false);
    if (currentQuizIndex < quizData.length - 1) {
      setCurrentQuizIndex(currentQuizIndex + 1);
      setTimeLeft(10);
      setSelectedAnswer(null);
      setIsQuizActive(true);
    } else {
      navigate('/quizresult', { state: { correctCount: correctCountRef.current } });
    }
  }, 5000);  //모달 시간 조절
};

useEffect(() => {
  let timer: NodeJS.Timeout;
  if (timeLeft > 0 && isQuizActive) {
    timer = setTimeout(() => setTimeLeft(timeLeft - 1), 1000);
  } else if (timeLeft === 0 && isQuizActive) {
    handleTimeUp();
  }
  return () => clearTimeout(timer);
}, [timeLeft, isQuizActive, currentQuizIndex]);

// 정답 처리
const handleAnswer = (index: number ) => {
  setSelectedAnswer(index);
}

const currentOptions = [
  quizData[currentQuizIndex]?.option1,
  quizData[currentQuizIndex]?.option2,
  quizData[currentQuizIndex]?.option3,
  quizData[currentQuizIndex]?.option4,
];

const progress = (currentQuizIndex / quizData.length) * 100 + 50;

  return (
    <Container>
      <ProgressBarArea>
        <ProgressBarBackground />
        <ProgressBarFill progress={progress} isActive={isQuizActive} />
        {quizData.map((_, index) => (
        <ProgressStep
          key={index}
          completed={currentQuizIndex > index || (currentQuizIndex === index && showModal)}
          active={currentQuizIndex >= index}
          position={`${((index + 1) / quizData.length) * 100}%`}
        >
          {index + 1}
        </ProgressStep>
      ))}
      </ProgressBarArea>
      <CharacterArea>캐릭터존</CharacterArea>
      <NoticeArea>정답을 선택하거나 말하세요.</NoticeArea>
      <div>남은 시간: {timeLeft} 초</div>
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
            {isCorrect ? '정답입니다!' : '오답입니다.'}
            <br />
            정답: {currentOptions[quizData[currentQuizIndex].answer]}
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
  height: 15px;
  margin: 30px auto;
  position: relative;
`;

const ProgressBarBackground = styled.div`
  width: 100%;
  height: 100%;
  background-color: var(--disabled-color);
  border-radius: 10px;
`;

//prettier-ignore
const ProgressBarFill = styled.div<ProgressBarProps>`
  width: ${props => props.progress}%;
  height: 100%;
  background-color: var(--darkblue-color);
  border-radius: 10px;
  position: absolute;
  top: 0;
  left: 0;
  transition: width 0.3s ease;
`;

//prettier-ignore
const ProgressStep = styled.div<ProgressStepProps>`
  position: absolute;
  top: -10px;
  left: ${props => props.position};
  transform: translateX(-50%);
  width: 35px;
  height: 35px;
  background-color: ${props => (props.completed ? 'var(--green-color)' : props.active ? 'var(--lightblue-color)' : 'var(--disabled-color)')};
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
  margin: 50px;

  height: 800px;
  width: 800px;

  border: solid 1px black;
`;

const NoticeArea = styled.div`
  display: flex;
  justify-content: center;
  margin: 50px;

  font-size: 50px;
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
`

const ModalContent= styled.div`
  font-size: 35px;
  padding: 20px;
`