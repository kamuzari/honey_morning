import {instance} from '@/api/axios';

const getFullBriefing = async () => {
  const response = await instance.get(`/api/briefs/all`);
  return response.data;
};

const getOneBriefing = async payload => {
  const response = await instance.get(`/api/briefs/${payload.brief_id}`);
  return response.data;
};

export {getFullBriefing, getOneBriefing};
