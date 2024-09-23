import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

// 라우터 생성
const router = createBrowserRouter(routerInfo);
import {createBrowserRouter, RouterProvider} from 'react-router-dom';
import routerInfo from '@/router/index'; // 라우팅 정보 불러오기

// React Query
import {QueryClient, QueryClientProvider} from '@tanstack/react-query';
const queryClient = new QueryClient();

// root 요소 찾기 및 애플리케이션 렌더링
const rootElement = document.getElementById('root');
if (rootElement) {
  ReactDOM.createRoot(rootElement).render(
    <React.StrictMode>
      <QueryClientProvider client={queryClient}>
        <RouterProvider router={router} />
        <App></App>
      </QueryClientProvider>
    </React.StrictMode>,
  );
} else {
  console.error('Root element not found!');
}
