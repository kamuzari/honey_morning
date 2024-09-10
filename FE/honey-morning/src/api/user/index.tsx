// import {instance} from '@/api/axios';

// const fetchUserByAccess = async () => {
//   const access = sessionStorage.getItem('access');

//   if (!access) return null;

//   const res = await instance.get('api/users/user-info');
//   return res.data.result;
// };

// export {fetchUserByAccess};

import {instance} from '@/api/axios';

const fetchUserByAccess = async () => {
  try {
    const res = await instance.get('/api/users/user-info');
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
