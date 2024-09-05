import React from 'react';
import styled from 'styled-components';

const SubmitButton = styled.button`
  background-color: var(--mediumblue-color);
  color: white;
  padding: 1em 5em;
  border: none;
  rounded: 20%;
  cursor: pointer;
`;

const MoveToSignUp = styled.button`
  color: var(--mediumblue-color);
  background-color: none;
  cursor: pointer;
`;

const LoginProcess = () => {
  return (
    <div>
      <div>Sign In</div>
      <form>
        <div>
          <label>Email</label>
          <input
            type="email"
            placeholder="이메일을 입력해주세요."
            required
          />
        </div>
        <div>
          <label>Password</label>
          <input
            type="password"
            placeholder="비밀번호를 입력해주세요."
            required
          />
        </div>
        <div>
          <SubmitButton>로그인</SubmitButton>
        </div>
        <div>
          <div>아직 회원이 아니신가요?</div>
          <MoveToSignUp>회원가입 하기</MoveToSignUp>
        </div>
      </form>
    </div>
  );
};

export default LoginProcess;
