import styled from 'styled-components';
import {Link} from 'react-router-dom';

const MainPage = () => {
  return (
    <>
      <Container>
        <div>
          <StyledLink to="/sleep">수면페이지</StyledLink>
        </div>
        <div>
          <StyledLink to="/signin">로그인페이지</StyledLink>
        </div>
        <div>
          <StyledLink to="/signup">회원가입페이지</StyledLink>
        </div>
        <div>
          <StyledLink to="/alarm">알람페이지</StyledLink>
        </div>
        <div>
          <StyledLink to="/briefing">브리핑페이지</StyledLink>
        </div>
        <div>
          <StyledLink to="/quizzie">퀴즈페이지</StyledLink>
        </div>
      </Container>
    </>
  );
};

export default MainPage;

const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  gap: 20px;
  border: 1px solid lime;
  font-size: 5rem;
  color: white;
`;

const StyledLink = styled(Link)`
  color: white;
  text-decoration: none;
`;
