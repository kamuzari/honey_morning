// import '@/styles/global.css';

// function App() {
//   return <></>;
// }

// export default App;

import {useEffect, useState} from 'react';
import {useNavigate, Outlet} from 'react-router-dom';
import {useQueryClient} from '@tanstack/react-query';

function App() {
  const [alarmTime, setAlarmTime] = useState('11:15:00');
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  useEffect(() => {
    const checkAlarmTime = () => {
      const currentTime = new Date();
      const hours = String(currentTime.getHours()).padStart(2, '0');
      const minutes = String(currentTime.getMinutes()).padStart(2, '0');
      const seconds = String(currentTime.getSeconds()).padStart(2, '0');
      const formattedTime = `${hours}:${minutes}:${seconds}`;

      if (formattedTime === alarmTime) {
        console.log('navigating to alarm page');
        navigate('/alarm');
      }
    };

    const intervalId = setInterval(checkAlarmTime, 1000);
    return () => clearInterval(intervalId);
  }, [alarmTime, navigate]);

  return (
    <>
      {' '}
      <Outlet />
    </>
  );
}

export default App;
