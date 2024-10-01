import React, {useEffect, useState} from 'react';
import {Link} from 'react-router-dom';
import styled from 'styled-components';
import {useQueryClient} from '@tanstack/react-query';
import Logout from '@/component/Logout';

const Header = () => {
  const queryClient = useQueryClient();
  //prettier-ignore
  const userInfo = queryClient.getQueryData<{id: number, role: string, email: string, username: string}>(['userInfo']);

  const username = userInfo ? userInfo.username : null;

  console.log('Header에서 가져온 유저 정보:', userInfo);

  if (!userInfo) {
    return (
      <HeaderContainer>
        <p>
          <Link to="/signin">로그인</Link>해 주세요.
        </p>
      </HeaderContainer>
    );
  }

  return (
    <HeaderContainer>
      {userInfo ? (
        <p>{userInfo.username}님, 반갑습니다!</p>
      ) : (
        <p>
          <Link to="/signin">로그인</Link>해 주세요.
        </p>
      )}
      {userInfo && <Logout />}
    </HeaderContainer>
  );
};

export default Header;

const HeaderContainer = styled.header`
  display: flex;
  justify-content: space-between;
  padding: 1rem;
  background-color: var(--yellow-color);
  color: white;
`;
