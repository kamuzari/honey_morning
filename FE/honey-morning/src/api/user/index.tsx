import {instance} from '@/api/axios';

const fetchUserByAccess = async () => {
  try {
    const res = await instance.get('/api/auth/userInfo');
    return res.data.result;
  } catch (error) {
    console.error('Failed to fetch user info:', error);
    return null;
  }
};

export {fetchUserByAccess};

//React Query로 사용자 정보를 가져올 때
// const fetchUserInfo = async () => {
//   const { data } = await instance.get('/api/userInfo');
//   return data;
// };
