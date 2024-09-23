import {useState} from 'react';
import styled, {createGlobalStyle} from 'styled-components';
import {useWatch, useForm, Controller, SubmitHandler} from 'react-hook-form';
import {useNavigate, Link} from 'react-router-dom';
import {useQueryClient, useMutation} from '@tanstack/react-query';
import {instance} from '@/api/axios';
import Logout from '@/component/Logout';

interface LoginFormData {
  email: string;
  password: string;
}

const LoginProcess: React.FC = () => {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  //prettier-ignore
  const {
    handleSubmit,
    control,
    watch,
    formState: {errors},
    reset,
  } = useForm<LoginFormData>({mode: 'onChange'});

  const loginUser = async (payload: LoginFormData) => {
    try {
      const formData = new FormData();
      formData.append('email', payload.email);
      formData.append('password', payload.password);

      const res = await instance.post('/api/auth/login', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      const accessToken = res.headers['access'];
      console.log('로그인 응답:', res);

      if (accessToken) {
        sessionStorage.setItem('access', accessToken);
        return accessToken;
      }
      if (res.data && res.data.result) {
        return res.data.result;
      } else {
        throw new Error('로그인 응답에 사용자 정보가 없습니다.');
      }
    } catch (error) {
      console.error('로그인 실패', error);
      throw error;
    }
  };

  // 유저 정보 가져오기
  const fetchUserInfo = async () => {
    const token = sessionStorage.getItem('access');

    if (!token) {
      throw new Error('토큰이 존재하지 않습니다.');
    }

    const res = await instance.get('/api/auth/userInfo', {
      headers: {
        // Authorization: `Bearer ${token}`,
        access: token,
      },
    });

    console.log('유저 정보 응답111: ', res.data);

    if (res.data && res.data.username) {
      console.log('유저 정보 응답: ', res);
      return res.data;
    } else {
      console.error('유저 정보가 비어 있습니다.');
      throw new Error('유저 정보가 비어 있습니다.');
    }
  };

  // const {mutate: signMutate} = useMutation({
  //   mutationFn: loginUser,
  //   onSuccess: data => {
  //     queryClient.setQueryData(['userInfo'], data);
  //     console.log('data:', data);
  //     alert('로그인 성공');
  //     navigate('/');
  //   },
  //   onError: (error: any) => {
  //     if (error.message) {
  //       alert(`로그인 실패: ${error.message}`);
  //     } else {
  //       alert('로그인 실패');
  //     }
  //     reset({email: '', password: ''});
  //   },
  // });

  // mutation 사용
  const {mutate: signMutate} = useMutation({
    mutationFn: loginUser,
    onSuccess: async accessToken => {
      try {
        if (accessToken) {
          const userInfo = await fetchUserInfo();
          queryClient.setQueryData(['userInfo'], userInfo);
          console.log('유저 정보:', userInfo);
          alert('로그인 성공');
          navigate('/');
        } else {
          console.error('토큰이 존재하지 않습니다.');
        }
      } catch (error) {
        console.error('유저 정보 가져오기 실패', error);
      }
    },
    onError: (error: any) => {
      alert('로그인 실패');
      reset({email: '', password: ''});
    },
  });

  const onSubmit: SubmitHandler<LoginFormData> = data => {
    signMutate(data, {
      onSuccess: () => {},
      onError: () => {
        reset({email: '', password: ''});
      },
    });
  };

  return (
    <>
      <GlobalStyle />
      <Title>Sign In</Title>
      <Container>
        <form
          onSubmit={handleSubmit(onSubmit)}
          className="loginForm"
        >
          <div className="inputGroup">
            <label>Name</label>
            <Controller
              name="email"
              control={control}
              defaultValue=""
              rules={{required: '이메일은 필수 입력 항목입니다.'}}
              render={({field}) => (
                <Input
                  {...field}
                  type="email"
                  placeholder="이메일을 입력해주세요"
                />
              )}
            />
          </div>
          <div className="inputGroup">
            <label>Password</label>
            <Controller
              name="password"
              control={control}
              defaultValue=""
              rules={{required: '이메일은 필수 입력 항목입니다.'}}
              render={({field}) => (
                <Input
                  {...field}
                  type="password"
                  placeholder="이메일을 입력해주세요"
                />
              )}
            />
          </div>

          <SubmitButton type="submit">로그인</SubmitButton>
        </form>
        <div className="signUpYet">
          <div>아직 회원이 아니신가요?</div>
          <MoveToSignUp to="/signup">회원가입 하기</MoveToSignUp>
        </div>
      </Container>
      <Logout />
    </>
  );
};

export default LoginProcess;

const GlobalStyle = createGlobalStyle`
 body {
  display: flex;
  justify-content: center;
  width: 100%;
 }
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

const Input = styled.input`
  padding: 10px;
  margin: 10px 0;
  border-radius: 5px;
  border: 1px solid black;
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

const MoveToSignUp = styled(Link)`
  color: var(--mediumblue-color);
  background-color: transparent;
  cursor: pointer;
  border: none;
  text-decoration: underline;
`;
