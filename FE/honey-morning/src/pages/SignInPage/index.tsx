import {useState, useEffect} from 'react';
import styled, {createGlobalStyle} from 'styled-components';
import {useWatch, useForm, Controller, SubmitHandler} from 'react-hook-form';
import {useNavigate, Link} from 'react-router-dom';
import {useQueryClient, useMutation} from '@tanstack/react-query';
import {instance} from '@/api/axios';
import Footer from '@/component/Footer';

interface LoginFormData {
  email: string;
  password: string;
}

const LoginProcess: React.FC = () => {
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const [isPortrait, setIsPortrait] = useState(true);

  // 가로/세로 모드
  useEffect(() => {
    const handleResize = () => {
      const isPortraitMode = window.innerHeight > window.innerWidth;
      setIsPortrait(isPortraitMode);
    };

    handleResize();
    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, []);

  //prettier-ignore
  const {
    handleSubmit,
    control,
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
        access: token,
      },
    });

    if (res.data && res.data.username) {
      return res.data;
    } else {
      throw new Error('유저 정보가 비어 있습니다.');
    }
  };

  // mutation 사용
  const {mutate: signMutate} = useMutation({
    mutationFn: loginUser,
    onSuccess: async accessToken => {
      try {
        if (accessToken) {
          const userInfo = await fetchUserInfo();
          queryClient.setQueryData(['userInfo'], userInfo);
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
      <GlobalStyle isPortrait={isPortrait} />
      <Container isPortrait={isPortrait}>
        <form
          onSubmit={handleSubmit(onSubmit)}
          className="loginForm"
        >
          <Title isPortrait={isPortrait}>Sign In</Title>
          <div className="inputGroup">
            <label>Email</label>
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
                  isPortrait={isPortrait}
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
              rules={{required: '비밀번호 필수 입력 항목입니다.'}}
              render={({field}) => (
                <Input
                  {...field}
                  type="password"
                  placeholder="비밀번호를 입력해주세요"
                  isPortrait={isPortrait}
                />
              )}
            />
          </div>

          <SubmitButton
            type="submit"
            isPortrait={isPortrait}
          >
            로그인
          </SubmitButton>
          <div className="signUpYet">
            <div>아직 회원이 아니신가요?</div>
            <MoveToSignUp to="/signup">회원가입 하기</MoveToSignUp>
          </div>
        </form>
        <Footer />
      </Container>
    </>
  );
};

export default LoginProcess;

//prettier-ignore
const GlobalStyle = createGlobalStyle<{isPortrait: boolean}>`
 body, html {
    margin: 0;
    padding: 0;
    height: 100%;
    width: 100%;
    box-sizing: border-box;
    overflow: hidden;
  }

  #root {
    height: 100%;
    width: 100%;
  }
`;

const Title =
  styled.div <
  {isPortrait: boolean} >
  `
  display: flex;
  justify-content: center;
  font-size: ${({isPortrait}) => (isPortrait ? '5vw' : '3vw')}; 
  margin-bottom: ${({isPortrait}) => (isPortrait ? '3vw' : '4vw')}; 
`;

//prettier-ignore
const Container = styled.div<{isPortrait: boolean}>`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  width: 100vw;
  height: 100vh;
  padding: 0;

  background-image: ${({ isPortrait }) =>
    isPortrait ? "url('/images/login1.png')" : "url('/images/loginDesktop.png')"};
  background-size: 100vw auto;
  background-position: top;
  background-repeat: no-repeat;
  overflow: hidden;


  .loginForm {
    display: flex;
    flex-direction: column;
    width: ${({ isPortrait }) => (isPortrait ? '50vw' : '35vw')};

    padding: 3vh 5vw 1vh;
    margin: 0 auto;
    border-radius: 20px;
    background-color: white;
    
    box-shadow: 0 0 1vh rgba(0, 0, 0, 0.5);

  
   .inputGroup {
      margin-bottom: 1vh;
      font-size: ${({ isPortrait }) => (isPortrait ? '2.5vw' : '1.5vw')};
    }
  }


  .signUpYet {
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 2vh;
    gap: 1vw;
    font-size: ${({ isPortrait }) => (isPortrait ? '2vw' : '1vw')};
  }
  }
`;

const Input =
  styled.input <
  {isPortrait: boolean} >
  `
  width: 100%;
  padding: ${({isPortrait}) => (isPortrait ? '1vh 0' : '2vh 0')};
  margin: ${({isPortrait}) => (isPortrait ? '0.5vh 0' : '1vh 0')};
  border-radius: 5px;
  border: 1px solid black;
  font-size: ${({isPortrait}) => (isPortrait ? '1vh' : '2vh')};
`;

const SubmitButton =
  styled.button <
  {isPortrait: boolean} >
  `
  background-color: var(--yellow-color);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: ${({isPortrait}) => (isPortrait ? '3vw' : '1.3vw')};

  display: flex;
  justify-content: center;
  padding: 1vw 1vh;
  margin: 1vw auto;
  width: 100%;
  height: ${({isPortrait}) => (isPortrait ? '7vw' : '1.3vw')};

  cursor: pointer;
`;

const MoveToSignUp = styled(Link)`
  color: var(--mediumblue-color);
  background-color: transparent;
  cursor: pointer;
  border: none;
  text-decoration: underline;
`;
