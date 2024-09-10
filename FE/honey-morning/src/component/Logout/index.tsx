import {useQueryClient, useMutation} from '@tanstack/react-query';
import {useNavigate} from 'react-router-dom';
import {instance} from '@/api/axios';

const LogoutProcess: React.FC = () => {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  const userLogout = async () => {
    await instance.post('api/auth/logout');
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

  // const handleLogout = async () => {
  //   try {
  //     await instance.post('api/auth/logout');

  //     sessionStorage.removeItem('access');

  //     queryClient.removeQueries({
  //       queryKey: ['userInfo'],
  //       exact: true,
  //     });

  //     console.log('로그아웃 성공');
  //   } catch (error) {
  //     console.error('로그아웃 실패: ', error);
  //   }

  //   navigate('/');
  // };

  return <button onClick={() => logoutMutation()}>Logout</button>;
};

export default LogoutProcess;
