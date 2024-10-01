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

// //React Query로 사용자 정보를 가져올 때
// import {useQueryClient} from '@tanstack/react-query';
// // 유저 정보 가져오기
// const queryClient = useQueryClient();
// //prettier-ignore
// const userInfo = queryClient.getQueryData<{id: number, role: string, email: string, username: string}>(['userInfo']);
// // 이름을 가져오는 예시 const username = userInfo ? userInfo.username : null;
