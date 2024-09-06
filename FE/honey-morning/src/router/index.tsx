import React from 'react';
import {RouteObject} from 'react-router-dom';

import LoginPage from '@/pages/LoginPage';

import AlarmSetting from '@/pages/AlarmSetting';

import SleepPage from '@/pages/Sleep';

const routerInfo: RouteObject[] = [
  {
    path: '/alarmsetting',
    element: <AlarmSetting />,
  },
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/sleep',
    element: <SleepPage />,
  },
];

export default routerInfo;
