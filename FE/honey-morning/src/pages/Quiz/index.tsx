// import QuizOption from '@/component/QuizOption'
import {useState} from 'react';
import styled from 'styled-components';

interface ProgressBarProps {
  progress: number;
}

interface ProgressStepProps {
  completed: boolean;
  active: boolean;
  position?: string;
}

const QuizSolution: React.FC = () => {
  //prettier-ignore
  const [progress, setProgress] = useState<number>(0);

  const nextStep = () => {
    setProgress(prev => Math.min(prev + 50, 100));
  };

  return (
    <Container>
      <ProgressBarArea>
        <ProgressBarBackground />
        <ProgressBarFill progress={progress} />
        <ProgressStep
          completed={progress >= 50}
          active={progress >= 0}
          position="50%"
        >
          1
        </ProgressStep>
        <ProgressStep
          completed={progress === 100}
          active={progress >= 50}
          position="100%"
        >
          2
        </ProgressStep>
      </ProgressBarArea>
      <CharacterArea>캐릭터존</CharacterArea>
      <NoticeArea>정답을 선택하거나 말하세요.</NoticeArea>
      <SelectArea>
        <div className="SelectRow">
          <SelectionBox />
          <SelectionBox />
        </div>

        <div className="SelectRow">
          <SelectionBox />
          <SelectionBox />
        </div>
      </SelectArea>
      <button onClick={nextStep}>다음 문제</button>
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
  margin: 30px 0;
  position: relative;
`;

const ProgressBarBackground = styled.div`
  width: 100%;
  height: 100%;
  background-color: #e0e0e0;
  border-radius: 10px;
`;

//prettier-ignore
const ProgressBarFill = styled.div<ProgressBarProps>`
  width: ${props => props.progress}%;
  height: 100%;
  background-color: #3498db;
  border-radius: 10px;
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
  width: 30px;
  height: 30px;
  background-color: ${props => (props.completed ? '#94d382' : props.active ? '#3498db' : '#e0e0e0')};
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  color: white;
  transition: background-color 0.3s ease;
`;

const CharacterArea = styled.div`
  display: flex;
  justify-content: center;
  margin: 50px;
`;

const NoticeArea = styled.div`
  display: flex;
  justify-content: center;

  font-size: 50px;
`;

const SelectArea = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;

  .SelectRow {
    display: flex;
    justify-content: center;
  }
`;

const SelectionBox = styled.div`
  background-color: var(--lightblue-color);
  color: var(--darkblue-color);

  width: 350px;
  height: 350px;
  margin: 30px;
  border: none;
  border-radius: 20px;
`;
