import styled from 'styled-components';

export interface StreakProps {
  count: number;
  isAttending: number;
  createdAt: string;
  size: number;
}

const Streak: React.FC<StreakProps> = ({
  count,
  isAttending,
  createdAt,
  size,
}) => {
  const calcPercent = count + isAttending;
  var colors = [];
  colors.length;
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
      'gray',
      'var(--yellow-color)',
      'gray',
      'var(--yellow-color)',
      'var(--yellow-color)',
      'var(--yellow-color)',
    ];
  if (calcPercent == 1)
    colors = [
      'gray',
      'var(--yellow-color)',
      'gray',
      'var(--yellow-color)',
      'gray',
      'gray',
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
  return (
    <Shape $size={size}>
      {/* {createdAt} */}
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
const Shape =
  styled.div <
  {$size: number} >
  `
  width: ${props => props.$size}rem;
  height: ${props => props.$size * 1.15}rem;
  display: inline-flex;
  flex-wrap: wrap;
  div {
    display: inline-flex;
    border: none;
    margin-left: -1px;
    margin-right: -1px;
  }
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
  height: 47%;
  margin-top: -32%;
  background-color: ${props => props.$color};
  clip-path: polygon(0 0, 0 100%, 100% 50%);
`;
const Four =
  styled.div <
  {$color} >
  `
  width: 50%;
  height: 47%;
  margin-top: -32%;

  background-color: ${props => props.$color};
  clip-path: polygon(100% 0, 100% 100%, 0 50%);
`;
const Five =
  styled.div <
  {$color} >
  `
  width: 50%;
  height: 50%;
  margin-top: -32%;

  background-color: ${props => props.$color};
  clip-path: polygon(100% 0, 100% 100%, 0 50%);
`;
const Six =
  styled.div <
  {$color} >
  `
  width: 50%;
  height: 50%;
  margin-top: -32%;
  background-color: ${props => props.$color};
  clip-path: polygon(0 0, 0 100%, 100% 50%);
`;
