import React from 'react';
import {RouteObject} from 'react-router-dom';

import SignInPage from '@/pages/SignInPage';
import SignUpPage from '@/pages/SignUpPage';

import AlarmSetting from '@/pages/AlarmSetting';

const routerInfo: RouteObject[] = [
  {
    path: '/alarmsetting',
    element: <AlarmSetting />,
  },
  {
    path: '/login',
    element: <SignInPage />,
  },
  {
    path: '/signup',
    element: <SignUpPage />,
  },
];

export default routerInfo;
