import {useState, useCallback} from 'react';
import {useNavigate} from 'react-router-dom';
import Modal from '@/component/Modal';
import {useQuery} from '@tanstack/react-query';
import {instance} from '@/api/axios';
import styled from 'styled-components';
import {Container} from '../MyPage';
import Header from '@/component/Header';
import Object from '@/component/Main/Object';
import NavBar from '@/component/NavBar/NavBar';
import {NavIconProps} from '@/component/NavBar/NavIcon';

const fetchUserInfo = async () => {
  try {
    const {data} = await instance.get(`/api/auth/userInfo`);
    return data;
  } catch (error) {
    return null;
  }
};

const Main = () => {
  const navigate = useNavigate();

  //prettier-ignore
  const [modalTitle, setModalTitle] = useState<string>('');
  //prettier-ignore
  const [modalMessage, setModalMessage] = useState<string>('');
  //prettier-ignore
  const [modalBtnText, setModalBtnText] = useState<string>('');
  //prettier-ignore
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

  const {
    data: userInfo,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['userInfo'],
    queryFn: fetchUserInfo,
  });

  const openModal = useCallback(
    (title: string, message: string, btnText: string) => {
      console.log('Opening modal');
      setModalTitle(title);
      setModalMessage(message);
      setModalBtnText(btnText);
      setIsModalOpen(true);
    },
    [],
  );

  const closeModal = useCallback(() => {
    setIsModalOpen(false);
    navigate('/signin');
  }, [navigate]);

  const handleNavClick = useCallback(
    (path: string) => {
      console.log('Nav clicked, userInfo:', userInfo);
      console.log('Nav clicked');
      if (!userInfo) {
        openModal('Caution', '로그인을 해주세요.', '확인');
      } else {
        console.log('Navigating to:', path);
        navigate(path);
      }
    },
    [userInfo, navigate, openModal],
  );

  const bgColor = 'var(--darkblue-color)';
  const textColor = 'var(--white-color)';
  const NavIcons: NavIconProps[] = [
    {
      $bgColor: bgColor,
      $textColor: textColor,
      text: 'alarm',
      // to: '/alarmsetting',
      onClick: () => handleNavClick('/alarmsetting'),
    },
    {
      $bgColor: bgColor,
      $textColor: textColor,
      text: 'person',
      // to: '/mypage',
      onClick: () => handleNavClick('/mypage'),
    },
  ];

  if (isLoading) {
    return <div>로딩 중...</div>;
  }

  return (
    <Container>
      <Header />
      <Object />
      <NavBar props={NavIcons} />
      <Modal
        isOpen={isModalOpen}
        isClose={closeModal}
        title={modalTitle}
        message={modalMessage}
        btnText={modalBtnText}
      />
    </Container>
  );
};

export default Main;
