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

  if (!userInfo) {
    return (
      <HeaderContainer>
        <p>
          <StyledLink to="/signin">로그인</StyledLink>해 주세요.
        </p>
      </HeaderContainer>
    );
  }

  return (
    <HeaderContainer>
      {userInfo ? (
        <p>{userInfo.username}님, 활기찬 아침을 맞을 준비가 되셨나요?</p>
      ) : (
        <p>
          <StyledLink to="/signin">로그인</StyledLink>해 주세요.
        </p>
      )}
      {userInfo && <Logout />}
    </HeaderContainer>
  );
};

export default Header;

const HeaderContainer = styled.header`
  display: flex;
  font-size: 2rem;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  gap: 2rem;
  background-color: var(--darkblue-color);
  border: none;
  border-radius: 20px;
  color: white;
`;

const StyledLink = styled(Link)`
  color: var(--yellow-color);
  text-decoration: none;
  &:visited {
    color: var(--yellow-color);
  }
`;
