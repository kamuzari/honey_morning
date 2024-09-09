import axios from 'axios';

export const instance = axios.create({
  baseURL: import.meta.env.VITE_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

// 요청 시 토큰 자동 추가
instance.interceptors.request.use(
  config => {
    const access = sessionStorage.getItem('access');
    if (access) {
      config.headers['Authorization'] = `Bearer ${access}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  },
);

// 응답 처리
instance.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    return Promise.reject(error);
  },
);
