// import React from 'react';
// import ReactDOM from 'react-dom/client';
// import App from './App';

// // 라우터 생성
// const router = createBrowserRouter(routerInfo);
// import {createBrowserRouter, RouterProvider} from 'react-router-dom';
// import routerInfo from '@/router/index'; // 라우팅 정보 불러오기

// // React Query
// import {QueryClient, QueryClientProvider} from '@tanstack/react-query';
// const queryClient = new QueryClient();

// // root 요소 찾기 및 애플리케이션 렌더링
// const rootElement = document.getElementById('root');
// if (rootElement) {
//   ReactDOM.createRoot(rootElement).render(
//     <React.StrictMode>
//       <QueryClientProvider client={queryClient}>
//         <RouterProvider router={router} />
//         <App></App>
//       </QueryClientProvider>
//     </React.StrictMode>,
//   );
// } else {
//   console.error('Root element not found!');
// }

import React from 'react';
import ReactDOM from 'react-dom/client';
import {createBrowserRouter, RouterProvider} from 'react-router-dom';
import {QueryClient, QueryClientProvider} from '@tanstack/react-query';
import App from './App';
import '@/styles/global.css';
import routerInfo from '@/router/index'; // 기존 라우팅 정보

const queryClient = new QueryClient();

// 여기서 App 컴포넌트와 라우터 정보를 결합하여 createBrowserRouter로 라우터를 생성합니다.
const router = createBrowserRouter([
  {
    path: '/',
    element: <App />, // 최상위 App 컴포넌트
    children: routerInfo, // 자식 라우트로 기존의 라우터 정보 연결
  },
]);

// root 요소 찾기 및 애플리케이션 렌더링
const rootElement = document.getElementById('root');
if (rootElement) {
  ReactDOM.createRoot(rootElement).render(
    <React.StrictMode>
      <QueryClientProvider client={queryClient}>
        <RouterProvider router={router} />
      </QueryClientProvider>
    </React.StrictMode>,
  );
} else {
  console.error('Root element not found!');
}
