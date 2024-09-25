import {instance} from '@/api/axios';

const getBriefs = async (page: number) => {
  const response = await instance.get(`/api/briefs/all?page=${page}`, {
    responseEncoding: 'utf8',
  });
  return response.data;
};

const getBrief = async (briefId: string) => {
  const response = await instance.get(`/api/briefs/${briefId}`, {
    responseEncoding: 'utf8',
  });
  return response.data;
};

export {getBriefs, getBrief};
