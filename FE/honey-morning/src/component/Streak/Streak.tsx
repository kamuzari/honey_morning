import styled from 'styled-components';

interface StreakProps {
  count: number;
  isAttending: number;
  createdAt: string;
}

const Streak: React.FC<StreakProps> = ({count, isAttending, createdAt}) => {
  const calcPercent = count + isAttending;
  var colors = [];
  if (calcPercent == 3)
    colors = [
      'var(--yellow-color)',
      'var(--yellow-color)',
      'var(--yellow-color)',
      'var(--yellow-color)',
      'var(--yellow-color)',
      'var(--yellow-color)',
    ];
  if (calcPercent == 2)
    colors = [
      'var(--black-color)',
      'var(--yellow-color)',
      'var(--yellow-color)',
      'var(--yellow-color)',
      'var(--yellow-color)',
      'var(--black-color)',
    ];
  if (calcPercent == 1)
    colors = [
      'var(--black-color)',
      'var(--yellow-color)',
      'var(--yellow-color)',
      'var(--black-color)',
      'var(--black-color)',
      'var(--black-color)',
    ];
  if (calcPercent == 0)
    colors = [
      'var(--darkblue-color)',
      'var(--darkblue-color)',
      'var(--darkblue-color)',
      'var(--darkblue-color)',
      'var(--darkblue-color)',
      'var(--darkblue-color)',
    ];
  console.log(colors);
  return (
    <Shape>
      <One $color={colors[0]}></One>
      <Two $color={colors[1]}></Two>
      <Three $color={colors[2]}></Three>
      <Four $color={colors[3]}></Four>
      <Five $color={colors[4]}></Five>
      <Six $color={colors[5]}></Six>
    </Shape>
  );
};

export default Streak;
// 도형을 렌더링할 스타일드 컴포넌트 예시
const Shape = styled.div`
  width: 52px;
  height: 60px;
  display: inline-flex;
  flex-wrap: wrap;
  div {
    display: inline-flex;
    border: none;
  }
  border: red 1px solid;
`;
const One =
  styled.div <
  {$color: string} >
  `
  width: 50%;
  height: 50%;
  background-color: ${props => props.$color};
  clip-path: polygon(100% 0, 100% 100%, 0 50%);
`;
const Two =
  styled.div <
  {$color} >
  `
  width: 50%;
  height: 50%;
  background-color: ${props => props.$color};
  clip-path: polygon(0 0, 0 100%, 100% 50%);
`;
const Three =
  styled.div <
  {$color} >
  `
  width: 50%;
  height: 50%;
  margin-top: -31%;
  background-color: ${props => props.$color};
  clip-path: polygon(0 0, 0 100%, 100% 50%);
`;
const Four =
  styled.div <
  {$color} >
  `
  width: 50%;
  height: 50%;
  margin-top: -31%;

  background-color: ${props => props.$color};
  clip-path: polygon(100% 0, 100% 100%, 0 50%);
`;
const Five =
  styled.div <
  {$color} >
  `
  width: 50%;
  height: 50%;
  margin-top: -31%;

  background-color: ${props => props.$color};
  clip-path: polygon(100% 0, 100% 100%, 0 50%);
`;
const Six =
  styled.div <
  {$color} >
  `
  width: 50%;
  height: 50%;
  margin-top: -31%;
  background-color: ${props => props.$color};
  clip-path: polygon(0 0, 0 100%, 100% 50%);
`;
