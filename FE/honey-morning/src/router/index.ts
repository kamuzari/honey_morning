import React from 'react';
import {createBrowserRouter, RouteObject} from 'react-router-dom';
import AlarmSetting from '@/pages/AlarmSetting';

const routerInfo: RouteObject[] = [
  {
    path: '/alarmsetting',
    element: <AlarmSetting />, // 라우트에서 렌더링할 컴포넌트
  },
];

export default routerInfo;
