import {useQueryClient, useMutation} from '@tanstack/react-query';
import {useNavigate} from 'react-router-dom';
import {instance} from '@/api/axios';
import styled from 'styled-components';

const LogoutProcess: React.FC = () => {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  const userLogout = async () => {
    try {
      sessionStorage.removeItem('access');
      queryClient.setQueryData(['userInfo'], null);
      // const response = await instance.post(
      //   '/api/auth/logout',
      //   {},
      //   {
      //     withCredentials: true,
      //   },
      // );
      // console.log('서버 응답:', response.data);
      // return response.data;
    } catch (error) {
      if (error.response) {
        console.error('Logout error response:', error.response.data);
      } else {
        console.error('Logout error:', error.message);
      }
      throw error;
    }
  };

  // const {mutate: logoutMutation} = useMutation({
  //   mutationFn: userLogout,
  //   onSuccess: () => {
  //     sessionStorage.removeItem('access');
  //     queryClient.setQueryData(['userInfo'], null);
  //     navigate('/');
  //   },
  //   onError: error => {
  //     console.error('로그아웃 실패', error);
  //   },
  // });

  return <LogoutButton onClick={userLogout}>Logout</LogoutButton>;
  // return <LogoutButton onClick={() => logoutMutation()}>Logout</LogoutButton>;
};

export default LogoutProcess;

const LogoutButton = styled.div`
  background-color: white;
  border: none;
  border-radius: 10px;
  color: var(--darkblue-color);
  padding: 1rem;
  font-size: 2rem;
`;
