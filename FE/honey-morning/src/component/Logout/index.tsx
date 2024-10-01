import {useQueryClient, useMutation} from '@tanstack/react-query';
import {useNavigate} from 'react-router-dom';
import {instance} from '@/api/axios';

const LogoutProcess: React.FC = () => {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  const userLogout = async () => {
    try {
      const response = await instance.post(
        '/api/auth/logout',
        {},
        {
          withCredentials: true,
        },
      );
      console.log('서버 응답:', response.data);
      return response.data;
    } catch (error) {
      if (error.response) {
        console.error('Logout error response:', error.response.data);
      } else {
        console.error('Logout error:', error.message);
      }
      throw error;
    }
  };

  const {mutate: logoutMutation} = useMutation({
    mutationFn: userLogout,
    onSuccess: () => {
      sessionStorage.removeItem('access');
      queryClient.setQueryData(['userInfo'], null);
      navigate('/');
    },
    onError: error => {
      console.error('로그아웃 실패', error);
    },
  });

  return <button onClick={() => logoutMutation()}>Logout</button>;
};

export default LogoutProcess;

// import React from 'react';
// import { useQueryClient, useMutation } from '@tanstack/react-query';
// import { useNavigate } from 'react-router-dom';
// import { instance } from '@/api/axios';

// const LogoutProcess: React.FC = () => {
//   const queryClient = useQueryClient();
//   const navigate = useNavigate();

//   const userLogout = async () => {
//     console.log('Attempting logout...'); // 디버깅용 로그
//     try {
//       const response = await instance.post(
//         'api/auth/logout',
//         {},
//         {
//           withCredentials: true,
//         }
//       );
//       console.log('Logout response:', response); // 디버깅용 로그
//       return response.data;
//     } catch (error) {
//       console.error('Logout error:', error); // 더 자세한 에러 로깅
//       throw error;
//     }
//   };

//   const { mutate: logoutMutation, isLoading, isError, error } = useMutation({
//     mutationFn: userLogout,
//     onSuccess: () => {
//       console.log('Logout successful'); // 디버깅용 로그
//       sessionStorage.removeItem('access');
//       queryClient.setQueryData(['userInfo'], null);
//       navigate('/');
//     },
//     onError: (error: any) => {
//       console.error('Logout failed', error);
//       // 사용자에게 에러 메시지를 보여줄 수 있습니다.
//     },
//   });

//   const handleLogout = () => {
//     console.log('Logout button clicked'); // 디버깅용 로그
//     logoutMutation();
//   };

//   return (
//     <div>
//       <button onClick={handleLogout} disabled={isLoading}>
//         {isLoading ? 'Logging out...' : 'Logout'}
//       </button>
//       {isError && <p>Error: {(error as Error).message}</p>}
//     </div>
//   );
// };

// export default LogoutProcess;
