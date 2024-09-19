import {instance} from '@/api/axios';

// 태그 전체 조회
const readAllTags = async () => {
  const response = await instance.get(`/api/tags`);
  return response.data;
};

// 태그 추가
const addNewTags = async payload => {
  const response = await instance.post(`/api/tags`, payload);
  return response.data;
};

// 태그 삭제
const deleteExistTags = async payload => {
  const response = await instance.delete(`/api/tags`, {data: payload});
  return response.data;
};

// 태그 선택 여부 수정
const modifySelectedTags = async payload => {
  const response = await instance.patch(`/api/tags`, payload);
  return response.data;
};

export {readAllTags, addNewTags, deleteExistTags, modifySelectedTags};
