import {useState, useEffect} from 'react';

const useWakeLock = () => {
  const [wakeLock, setWakeLock] = (useState < WakeLockSentinel) | (null > null);
  const [isScreenDimmed, setIsScreenDimmed] = useState(false);

  const handleRequestWakeLock = async () => {
    // ... (기존 Wake Lock 요청 로직)
  };

  const handleReleaseWakeLock = async () => {
    // ... (기존 Wake Lock 해제 로직)
  };

  useEffect(() => {
    handleRequestWakeLock();
    return () => {
      if (wakeLock) {
        handleReleaseWakeLock();
      }
    };
  }, []);

  return {isScreenDimmed, setIsScreenDimmed};
};

export default useWakeLock;
