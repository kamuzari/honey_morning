import {RouteObject} from 'react-router-dom';

import LoginPage from '@/pages/LoginPage';

import AlarmSetting from '@/pages/AlarmSetting';
import MyPage from '@/pages/MyPage';
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
    path: '/mypage',
    element: <MyPage />,
  },
];

export default routerInfo;
