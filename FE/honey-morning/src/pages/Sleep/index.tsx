import React, {useEffect, useState} from 'react';

// Home 컴포넌트 정의
const Home = () => {
  // wakeLock 상태를 관리하기 위해 useState 사용
  // wakeLock 상태는 WakeLockSentinel 객체를 저장,
  // 기본값은 null로 설정, wake lock이 활성화되지 않은 상태임을 나타낸다.
  const [wakeLock, setWakeLock] = useState<WakeLockSentinel | null>(null);

  // 화면이 잠기지 않도록 Wake Lock을 요청하는 함수
  const handleRequestWakeLock = async () => {
    try {
        // navigator.wakeLock API가 브라우저에서 지원되는지 확인
        if ('wakeLock' in navigator){
            // wakeLock.request() 메서드를 사용하여 화면이 잠기지 않도록 요청
            const lock = await (navigator as any).wakeLock.request('screen');
            // wakeLock 상태를 업데이트하여 요청한 lock 정보를 저장
            setWakeLock(lock);

            lock.addEventListener('release', () => {
                console.log('Wake Lock 해제');
            });
            console.log('Wake Lock 활성화');
        } else{
            // Wake Lock API가 브라우저에서 지원되지 않는 경우 처리
            console.log('해당 브라우저에서 Wake Lock이 지원되지 않습니다');
        }
    } catch (err) {
        if (err instanceof Error) {
          console.error(`${err.name}, ${err.message}`);
        } else {
          console.error('알 수 없는 오류...');
        }
    }
    };
    
  const handleReleaseWakeLock = async () => {
    if (wakeLock) {
        // wakeLock.release() 메서드를 사용하여 Wake Lock 해제
        await wakeLock.release();
        // wakeLock 상태를 null로 변경
        setWakeLock(null);
        console.log('Wake Lock 해제');
    }
  };

  // 컴포넌트가 언마운트될 때 Wake Lock을 자동으로 해제
  useEffect(() => {
    return () => {
        // wakeLock이 설정되어 있으면 해제 함수 호출
        if (wakeLock){
            handleReleaseWakeLock();
        }
    };
  }, [wakeLock]);   // wakeLock 상태가 변경될 때마다 이 효과가 실행됨

  return (
    <div>
        <div>React 기반 Wake Lock API 예제</div>
        <div>
            <button onClick={handleRequestWakeLock}>Wake Lock 요청</button>
        </div>
        <div>
            <button onClick={handleReleaseWakeLock} disabled={!wakeLock}>
            Wake Lock 해제
            </button>
        </div>
    </div>
  );
};


export default Home;