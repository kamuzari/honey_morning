import React from 'react';
import {RouteObject} from 'react-router-dom';

import LoginPage from '@pages/LoginPage';

import AlarmSetting from '@/pages/AlarmSetting';

const routerInfo: RouteObject[] = [
  {
    path: '/alarmsetting',
    element: <AlarmSetting />,
  },
  {
    path: '/login',
    element: <LoginPage />,
  },
];

export default routerInfo;
