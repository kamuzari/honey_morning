import React, {useEffect, useState} from 'react';
import styled from 'styled-components';
import {useQuery, useQueryClient} from '@tanstack/react-query';
import {instance} from '@/api/axios';
import Logout from '@/component/Logout';

const fetchUserInfo = async () => {
  const {data} = await instance.get(`/api/auth/userInfo`);
  return data;
};

const Header = () => {
  const {
    data: userInfo,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['userInfo'],
    queryFn: fetchUserInfo,
  });

  console.log(userInfo);

  if (isLoading) {
    return <HeaderContainer>Loading...</HeaderContainer>;
  }

  if (isError) {
    return <HeaderContainer>Error fetching user info</HeaderContainer>;
  }

  return (
    <HeaderContainer>
      {userInfo ? (
        <p>{userInfo.username}님, 반갑습니다!</p>
      ) : (
        <p>로그인해 주세요.</p>
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
