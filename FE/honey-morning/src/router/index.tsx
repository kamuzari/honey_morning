import {RouteObject} from 'react-router-dom';
import AlarmSetting from '@/pages/AlarmSetting';

const routerInfo: RouteObject[] = [
  {
    path: '/alarmsetting',
    element: <AlarmSetting />,
  },
];

export default routerInfo;
