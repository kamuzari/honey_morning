import styled from 'styled-components';
import {Container} from '../MyPage';
import Object from '@/component/Main/Object';
import NavBar from '@/component/NavBar/NavBar';
import {NavIconProps} from '@/component/NavBar/NavIcon';

const Main = () => {
  const bgColor = 'var(--darkblue-color)';
  const textColor = 'var(--white-color)';
  const NavIcons: NavIconProps[] = [
    {
      $bgColor: bgColor,
      $textColor: textColor,
      text: 'alarm',
      to: '/alarmsetting',
    },
    {
      $bgColor: bgColor,
      $textColor: textColor,
      text: 'person',
      to: '/mypage',
    },
  ];
  return (
    <Container>
      <Object></Object>
      <NavBar props={NavIcons}></NavBar>
    </Container>
  );
};

export default Main;
