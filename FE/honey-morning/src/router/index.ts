import React from 'react';
import {RouteObject} from 'react-router-dom';
import AlarmSetting from '@/pages/AlarmSetting';

const routerInfo: RouteObject[] = [
  {
    path: '/alarmsetting',
    element: React.createElement(AlarmSetting),
  },
];

export default routerInfo;
