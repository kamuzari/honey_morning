import React from 'react';

const LoginProcess = () => {
  return (
    <div>
      <div>Sign In</div>
      <form>
        <div>
          <label>Email</label>
        </div>
        <div>
          <label>Password</label>
        </div>
        <div>
          <button>로그인</button>
        </div>
        <div>
          <div>아직 회원이 아니신가요?</div>
          <button>회원가입 하기</button>
        </div>
      </form>
    </div>
  );
};

export default LoginProcess;
