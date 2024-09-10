import {RouteObject} from 'react-router-dom';

import SignInPage from '@/pages/SignInPage';
import SignUpPage from '@/pages/SignUpPage';

import Quiz from '@/pages/Quiz';

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
    path: '/signin',
    element: <SignInPage />,
  },
  {
    path: '/signup',
    element: <SignUpPage />,
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
  {
    path: '/quizzie',
    element: <Quiz />,
  },
];

export default routerInfo;
