import styled from 'styled-components';

const GlobalBtn = () => {
  return <Button>글로벌 버튼입니다.</Button>;
};

const Button = styled.button`
  display: flex;
  background-color: var(--yellow-color);
`;

export default GlobalBtn;
