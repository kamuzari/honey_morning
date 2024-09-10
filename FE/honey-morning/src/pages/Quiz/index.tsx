// import QuizOption from '@/component/QuizOption'
import styled from 'styled-components';

const QuizSolution = () => {
  return (
    <Container>
      <ProgressBar>프로그래스바</ProgressBar>
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
    </Container>
  );
};

export default QuizSolution;

const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
`;

const ProgressBar = styled.div`
  display: flex;
  justify-content: center;
  margin: 30px;
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
