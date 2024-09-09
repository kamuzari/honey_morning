import {RouteObject} from 'react-router-dom';

import LoginPage from '@/pages/LoginPage';

import AlarmSetting from '@/pages/AlarmSetting';
import MyPage from '@/pages/MyPage';
import BriefingDetail from '@/pages/BriefingDetail';
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
    path: '/mypage',
    element: <MyPage />,
  },
  {
    path: '/sleep',
    element: <SleepPage />,
  },
  {
    path: '/briefingdetail',
    element: <BriefingDetail />,
  },
];

export default routerInfo;
