import {instance} from '@/api/axios';

// 알람 결과 조회
const readAlarmResult = async () => {
  const response = await instance.get(`/api/alarms/result`);
  return response.data;
};

// 알람 결과 저장
const saveAlarmResult = async payload => {
  const response = await instance.post(`/api/alarms/result`, payload);
  return response.data;
};

// 사용자 알람 조회
const readUserAlarm = async () => {
  const response = await instance.get(`/api/alarms`);
  return response.data;
};

// 설정 일부 수정
const modifyAlarmSettings = async payload => {
  const response = await instance.patch(`/api/alarms`, payload);
  return response.data;
};

// 알람 카테고리 조회
const findAlarmCategory = async () => {
  const response = await instance.get(`/api/alarms/category`);
  return response.data;
};

export {
  readAlarmResult,
  saveAlarmResult,
  readUserAlarm,
  modifyAlarmSettings,
  findAlarmCategory,
};
