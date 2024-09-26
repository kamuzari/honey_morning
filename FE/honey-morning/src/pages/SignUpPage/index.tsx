import React, {useState, useEffect} from 'react';
import {useForm, Controller} from 'react-hook-form';
import {useNavigate} from 'react-router-dom';
import {useMutation} from '@tanstack/react-query';
import styled, {createGlobalStyle} from 'styled-components';
import {instance} from '@/api/axios';
import Modal from '@/component/Modal';
import Footer from '@/component/Footer';

interface SignUpFormData {
  email: string;
  username: string;
  password: string;
  confirmPassword: string;
}

const SignUpProcess: React.FC = () => {
  const navigate = useNavigate();
  const [isPortrait, setIsPortrait] = useState(true);

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
  const [isEmailChecked, setIsEmailChecked] = useState<boolean>(false);
  //prettier-ignore
  const [emailMessage, setEmailMessage] = useState<string>('');
  //prettier-ignore
  const [emailMessageType, setEmailMessageType] = useState<string>('');
  //prettier-ignore
  const [confirmPassword, setConfirmPassword] = useState<string>('');

  //prettier-ignore
  const [modalTitle, setModalTitle] = useState<string>('');
  //prettier-ignore
  const [modalMessage, setModalMessage] = useState<string>('');
  //prettier-ignore
  const [modalBtnText, setModalBtnText] = useState<string>('');
  //prettier-ignore
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

  //prettier-ignore
  const {
    handleSubmit,
    control,
    watch,
    formState: {errors},
    setError,
    clearErrors,
  } = useForm<SignUpFormData>({mode: 'onChange'});

  const openModal = (title: string, message: string, btnText: string) => {
    setModalTitle(title);
    setModalMessage(message);
    setModalBtnText(btnText);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    navigate('/signin');
  };

  const email = watch('email', '');
  const password = watch('password', '');

  const validateEmail = (email: string) => {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailPattern.test(email) || '이메일의 형식이 올바르지 않습니다.';
  };

  const checkEmailDuplicate = async (email: string) => {
    if (!email) {
      setError('email', {
        message: '이메일을 입력해주세요.',
      });
      setEmailMessage('');
      setEmailMessageType('');
      return;
    }

    try {
      const response = await instance.get(
        `/api/users/check/email?email=${email}`,
      );
      console.log(response);

      if (response.data === true) {
        setEmailMessage('이미 사용 중인 이메일입니다.');
        setEmailMessageType('error');
        setIsEmailChecked(false);
      } else {
        setEmailMessage('사용 가능한 이메일입니다.');
        setEmailMessageType('success');
        setIsEmailChecked(true);
      }
    } catch (error) {
      setEmailMessage('이메일 확인에 실패했습니다.');
      setEmailMessageType('error');
    }
  };

  // const validateForm = data => {
  //   let isValid = true;

  //   if (!isEmailChecked) {
  //     isValid = false;
  //   }
  // };

  const handlePasswordChange = (value: string) => {
    setConfirmPassword(value);
  };

  const passwordsMatch = confirmPassword === password;

  const handleSignUp = async (data: SignUpFormData) => {
    try {
      const response = await instance.post('/api/auth/register', data, {
        headers: {
          'Content-Type': 'application/json',
        },
      });

      console.log(data);

      if (response.status === 200) {
        openModal('Success', '회원가입에 성공했습니다.', '확인');
      }
    } catch (error) {
      console.log('회원가입 실패', error);
      alert('회원가입 실패');
    }
  };

  const isEmailValid = validateEmail(email) === true;

  return (
    <>
      <GlobalStyle isPortrait={isPortrait} />
      <Container isPortrait={isPortrait}>
        <form
          onSubmit={handleSubmit(handleSignUp)}
          className="signUpForm"
        >
          <Title isPortrait={isPortrait}>Sign Up</Title>
          <div className="inputGroup">
            <label>Email</label>
            <div className="emailInputGroup">
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
                    className="inputField"
                  />
                )}
              />
              <button
                type="button"
                className="duplicateButton"
                onClick={() => checkEmailDuplicate(email)}
              >
                중복확인
              </button>
            </div>
            {emailMessage && (
              <EmailMessage
                type={emailMessageType}
                isPortrait={isPortrait}
              >
                {emailMessage}
              </EmailMessage>
            )}
          </div>
          <div className="inputGroup">
            <label>Name</label>
            <Controller
              name="username"
              control={control}
              defaultValue=""
              rules={{required: '이름은 필수 입력 항목입니다.'}}
              render={({field}) => (
                <Input
                  {...field}
                  type="username"
                  placeholder="이름을 입력해주세요"
                  isPortrait={isPortrait}
                  className="inputField"
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
              rules={{required: '비밀번호는 필수 입력 항목입니다.'}}
              render={({field}) => (
                <Input
                  {...field}
                  type="password"
                  placeholder="비밀번호를 입력해주세요"
                  isPortrait={isPortrait}
                  className="inputField"
                />
              )}
            />
          </div>
          <div className="inputGroup">
            <label>Confirm Password</label>
            <Controller
              name="confirmPassword"
              control={control}
              defaultValue=""
              rules={{
                required: '비밀번호를 다시 입력해주세요.',
                validate: value =>
                  value === password || '비밀번호가 일치하지 않습니다.',
              }}
              render={({field}) => (
                <Input
                  {...field}
                  value={confirmPassword}
                  onChange={e => {
                    field.onChange(e);
                    handlePasswordChange(e.target.value);
                  }}
                  type="password"
                  placeholder="비밀번호를 다시 입력해주세요"
                  isPortrait={isPortrait}
                  className="inputField"
                  style={{
                    borderColor: confirmPassword
                      ? passwordsMatch
                        ? 'green'
                        : 'red'
                      : '',
                  }}
                />
              )}
            />
            {confirmPassword && (passwordsMatch !== undefined) ? (
              !passwordsMatch ? (
                <MessageText isPortrait={isPortrait} isError={true}>
                  비밀번호가 일치하지 않습니다.
                </MessageText>
              ) : (
                <MessageText isPortrait={isPortrait} isError={false}>
                  비밀번호가 일치합니다.
                </MessageText>
              )
            ) : null}
          </div>

          <SubmitButton
            type="submit"
            isPortrait={isPortrait}
          >
            회원가입
          </SubmitButton>
        </form>
      </Container>
      <Footer />

      <Modal
        isOpen={isModalOpen}
        isClose={closeModal}
        title={modalTitle}
        message={modalMessage}
        btnText={modalBtnText}
      />
    </>
  );
};

export default SignUpProcess;

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

//prettier-ignore
const Title = styled.div<{isPortrait: boolean}>`
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
    isPortrait ? "url('/images/login3.png')" : "url('/images/loginDesktop.png')"};
  background-size: 100vw auto;
  background-position: top;
  background-repeat: no-repeat;
  overflow: hidden;

  .signUpForm {
    display: flex;
    flex-direction: column;
    width: ${({ isPortrait }) => (isPortrait ? '50vw' : '40vw')};

    padding: ${({ isPortrait }) => (isPortrait ? '3vw' : '5vw')};
    margin: 0 auto;
    border-radius: 20px;
    background-color: white;
    
    box-shadow: 0 0 1vh rgba(0, 0, 0, 0.5);

  
   .inputGroup {
      margin-bottom: 1vh;
      font-size: ${({ isPortrait }) => (isPortrait ? '2.5vw' : '1.5vw')};

      .emailInputGroup {
        display: flex;
        align-items: center;
        gap: 30px;

        .duplicateButton {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 15vw;
          height: ${({ isPortrait }) => (isPortrait ? '5.5vw' : '3.5vw')};
          border-radius: 10px;
          background-color: var(--yellow-color);
          color: white;
          border: none;
          padding: 1vh 1vw;
          font-size: ${({ isPortrait }) => (isPortrait ? '2vw' : '1.5vw')};
        }
      }

      .inputField {
      }   
    }
  }
`;

const Input =
  styled.input <
  {isPortrait: boolean} >
  `
  width: 100%;
  padding: ${({isPortrait}) => (isPortrait ? '1vh 0.5vw' : '2vh 0.5vw')};
  margin: ${({isPortrait}) => (isPortrait ? '0.5vh 0' : '1vh 0')};
  border-radius: 5px;
  border: 1px solid black;
  font-size: ${({isPortrait}) => (isPortrait ? '1vh' : '2vh')};

   &::placeholder {
      color: grey;
      padding-left: 10px; 
    }
`;

const SubmitButton =
  styled.button <
  {isPortrait: boolean} >
  `
  background-color: var(--yellow-color);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: ${({isPortrait}) => (isPortrait ? '2vw' : '1.3vw')};

  display: flex;
  justify-content: center;
  padding: 1.5vw 1vh;
  margin: 2vh auto 1vh;
  width: 100%;

  cursor: pointer;
`;

//prettier-ignore
const EmailMessage = styled.p<{ type: string, isPortrait: boolean}>`
  color: ${({ type }) => (type === 'success' ? 'var(--darkgreen-color)' : 'var(--red-color)')};
  font-size: ${({isPortrait}) => (isPortrait ? '1vh' : '1vw')};
`;

//prettier-ignore
const MessageText = styled.div<{ isPortrait: boolean; isError: boolean }>`
  font-size: ${({ isPortrait }) => (isPortrait ? '1vh' : '1vh')};
  color: ${({ isError }) => (isError ? 'var(--red-color)' : 'var(--darkgreen-color)')};
`;
