import {RouteObject} from 'react-router-dom';

import SignInPage from '@/pages/SignInPage';
import SignUpPage from '@/pages/SignUpPage';

import AlarmPage from '@/pages/Alarm';
import AlarmSetting from '@/pages/AlarmSetting';
import MyPage from '@/pages/MyPage';
import BriefingPage from '@/pages/Briefing';
import BriefingDetail from '@/pages/BriefingDetail';
import SleepPage from '@/pages/Sleep';

const routerInfo: RouteObject[] = [
  {
    path: '/alarm',
    element: <AlarmPage />,
  },
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
    path: '/briefing',
    element: <BriefingPage />,
  },
  {
    path: '/briefingdetail',
    element: <BriefingDetail />,
  },
  // {path: '/interestsetting', element: <InterestSetting />},
];

export default routerInfo;
