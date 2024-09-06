import React, {useState} from 'react';
import styled, {createGlobalStyle} from 'styled-components';

const Title = styled.div`
  display: flex;
  justify-content: center;

  font-size: 50px;
`;

const Container = styled.div`
  display: flex;
  fix-direction: column;
  justify-content: center;
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

const SignUpProcess: React.FC = () => {
  const [email, setEmail] = useState('');
  const [name, setName] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  return (
    <>
      <Title>Sign Up</Title>
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
            <label>Email</label>
            <input
              type="name"
              value={name}
              placeholder="이메일을 입력해주세요."
              onChange={e => setName(e.target.value)}
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
          <div className="inputGroup">
            <label>Confirm Password</label>
            <input
              type="confrimPassword"
              value={confirmPassword}
              placeholder="비밀번호를 입력해주세요."
              onChange={e => setConfirmPassword(e.target.value)}
              required
            />
          </div>

          <SubmitButton>회원가입</SubmitButton>
        </div>
      </Container>
    </>
  );
};

export default SignUpProcess;
