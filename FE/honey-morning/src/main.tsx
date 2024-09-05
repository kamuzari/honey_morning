// import { StrictMode } from "react";
// import { createRoot } from "react-dom/client";
// import App from "./App.tsx";

// createRoot(document.getElementById("root")!).render(
//   <StrictMode>
//     <App />
//   </StrictMode>
// );

import React from 'react';
import ReactDOM from 'react-dom/client';
import {createBrowserRouter, RouterProvider} from 'react-router-dom';
import routerInfo from '@/router/index'; // 라우팅 정보 불러오기
import './index.css'; // 스타일 불러오기

// 라우터 생성
const router = createBrowserRouter(routerInfo);

// root 요소 찾기 및 애플리케이션 렌더링
const rootElement = document.getElementById('root');
if (rootElement) {
  ReactDOM.createRoot(rootElement).render(
    <React.StrictMode>
      <RouterProvider router={router} />
    </React.StrictMode>,
  );
} else {
  console.error('Root element not found!');
}
