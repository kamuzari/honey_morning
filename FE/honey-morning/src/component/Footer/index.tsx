import styled from 'styled-components';

const Footer = () => {
  return (
    <Container>
      <div>&copy; 2024. 싸피니까 All rights reserved.</div>
    </Container>
  );
};

const Container = styled.div`
  color: var(--yellow-color);
  font-size: 1vh;
  display: flex;
  justify-content: center;
  position: absolute;
  bottom: 2vh;
`;

export default Footer;
