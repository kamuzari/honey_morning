import React from 'react';
import styled from 'styled-components';
import {Outlet} from 'react-router-dom';

const LayoutWrapper = styled.div`
  width: 100vw;
  height: 100vh;
  min-width: 1080px;
  min-height: 2400px;
  margin: 0 auto;
  overflow: auto;

  @media (min-width: 1280px) {
    padding: 20px;
  }

  @media (max-width: 1080px) {
    width: 100%;
  }
`;

const MainLayout: React.FC = () => {
  return (
    <LayoutWrapper>
      <Outlet />
    </LayoutWrapper>
  );
};

export default MainLayout;
