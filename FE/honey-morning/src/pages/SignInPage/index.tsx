import React, {useState} from 'react';
import styled, {createGlobalStyle} from 'styled-components';

const GlobalStyle = createGlobalStyle`
 body {
  display: flex;
  justify-content: center;
  width: 100%;
 }
`;

const WaveTop = styled.div`
  position: absolute;
  top: 0;
  height: 150px;
`;

const Title = styled.div`
  display: flex;
  justify-content: center;

  font-size: 50px;
`;

const Container = styled.div`
  display: flex;
  flex-direction: column;

  align-items: center;
  justify-content: center;

  padding: 10px;
  border: solid 1px black;
  background-color: white;

  width: 700px;

  .loginForm {
  display: flex;
  flex-direction: column;

  border: solid 1px red;
  padding: 20px;
  box-shadow: 5px 5px 3px #666;

  
    .inputGroup {
    background-color: blue;
    }
  }


  .signUpYet {
  display: flex;
  align-items: center;
  
  }
  }
`;

const SubmitButton = styled.button`
  background-color: var(--yellow-color);
  color: white;
  border: none;
  border-radius: 10%;

  display: flex;
  justify-content: center;
  padding: 1em 5em;
  cursor: pointer;
`;

const MoveToSignUp = styled.button`
  color: var(--mediumblue-color);
  background-color: transparent;
  cursor: pointer;
  border: none;
  text-decoration: underline;
`;

const LoginProcess: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  return (
    <>
      <GlobalStyle />
      <WaveTop />
      <Title>Sign In</Title>
      <Container>
        <div className="loginForm">
          <div className="inputGroup">
            <label>Email</label>
            <input
              type="email"
              value={email}
              placeholder="이메일을 입력해주세요."
              onChange={e => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="inputGroup">
            <label>Password</label>
            <input
              type="password"
              value={password}
              placeholder="비밀번호를 입력해주세요."
              onChange={e => setPassword(e.target.value)}
              required
            />
          </div>

          <SubmitButton>로그인</SubmitButton>
        </div>
        <div className="signUpYet">
          <div>아직 회원이 아니신가요?</div>
          <MoveToSignUp>회원가입 하기</MoveToSignUp>
        </div>
      </Container>
    </>
  );
};

export default LoginProcess;
