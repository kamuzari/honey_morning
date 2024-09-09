import React, {ReactNode} from 'react';
import styled from 'styled-components';
import {Outlet} from 'react-router-dom';
import {useQuery} from '@tanstack/react-query';
import {fetchUserByAccess} from '@/api/user';

// interface LayoutProps {
//   children: ReactNode;
// }

// const MainLayoutWrapper = styled.div`
//   width: 1080px;
//   height: 2400px;
//   margin: 0 auto;
// `

// const MainLayout: React.FC<LayoutProps> = ({children}) => {
//   return <MainLayoutWrapper>{children}</MainLayoutWrapper>
// }

// export default MainLayout;

const LayoutWrapper = styled.div`
  width: 1080px;
  height: 2400px;
`;

const MainLayout: React.FC = () => {
  useQuery({
    queryKey: ['userInfo'],
    queryFn: fetchUserByAccess,
    staleTime: Infinity,
  });

  return (
    <LayoutWrapper>
      <Outlet />
    </LayoutWrapper>
  );
};

export default MainLayout;
