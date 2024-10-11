// import React, { useRef, useState, useEffect } from 'react';
// import { useQueryClient, useMutation } from '@tanstack/react-query'
// import { useNavigate  } from 'react-router-dom';
// import { instance } from '@/api/axios';
// import styled from 'styled-components';
// import Modal4 from '../../component/Modal4';

// interface AlarmStartResponse {
//   morningCallUrl: string;
//   briefingContent: string;
//   briefingContentUrl: string;
//   briefingId: number;
// }

// interface AlarmData {
//   id: number;
//   alarmTime: string;
//   daysOfWeek: string;
//   repeatFrequency: number;
//   repeatInterval: number;
//   isActive: number;
// }

// const fetchAudio = async (briefingId: number) => {
//   const response = await instance.get(`/api/briefs/audio/summary/${briefingId}`, {
//     responseType: 'blob',
//   });
//   const audioBlob = new Blob([response.data], { type: 'audio/mpeg' });
//   return { audioUrl: URL.createObjectURL(audioBlob), response }; 
// };


// const BriefingPage: React.FC = () => {

//   const navigate = useNavigate();
//   const queryClient = useQueryClient();

//   //prettier-ignore
//   const [alarmStartData, setAlarmStartData] = useState<AlarmStartResponse | null>(
//     queryClient.getQueryData<AlarmStartResponse>(['alarmStartData']) || null
//   );
//   // console.log('alarmStartData:', alarmStartData);
//   // localStorage에서 alarmStartData 불러오기
//   useEffect(() => {
//     if (!alarmStartData) {
//       const storedData = localStorage.getItem('alarmStartData');
//       if (storedData) {
//         const parsedData = JSON.parse(storedData) as AlarmStartResponse;
//         setAlarmStartData(parsedData); // 상태 업데이트
//         console.log('localStorage에서 가져온 데이터:', parsedData);
//       }
//     }
//   }, [alarmStartData]);
//   console.log(alarmStartData?.briefingContentUrl);

//   useEffect(() => {
//     if (alarmStartData) {
//       // alarmStartData가 정상적으로 업데이트되었을 때 처리
//       console.log('알람 시작 데이터:', alarmStartData);
//     }
//   }, [alarmStartData]);
  

//   const briefingContent = alarmStartData?.briefingContent;
//   const briefingContentUrl = alarmStartData?.briefingContentUrl;
//   const briefingId = alarmStartData?.briefingId ?? 109;

//   const canvasRef = useRef<HTMLCanvasElement | null>(null);
//   const flippedCanvasRef = useRef<HTMLCanvasElement | null>(null);
//   const audioRef = useRef<HTMLAudioElement | null>(null);
//   const [isPlaying, setIsPlaying] = useState(false);
//   const [animationId, setAnimationId] = useState<number | null>(null);
//   const [isModalOpen, setIsModalOpen] = useState(false);
  
//   const currentDate = new Date();
//   const currentMonth = String(currentDate.getMonth() + 1);
//   const currentDay = String(currentDate.getDate());

//   useEffect(() => {
//     initializeCanvas(canvasRef.current);
//     initializeCanvas(flippedCanvasRef.current, true);
    
//     return () => {
//       if (animationId !== null ) {
//         cancelAnimationFrame(animationId);
//       }
//     };
//   }, []); //처음 렌더링 시에만 실행
  
//   const initializeCanvas = (canvas: HTMLCanvasElement | null, isFlipped: boolean = false) => {
//     if (!canvas) return;
    
//     const ctx = canvas.getContext('2d');
//     if (!ctx) return;
    
//     const WIDTH = canvas.width;
//     const HEIGHT = canvas.height;
//     const centerX = WIDTH / 2;
//     const centerY = HEIGHT / 2;
//     const baseRadius = Math.min(WIDTH, HEIGHT) / 2 - 50;
    
//     ctx.clearRect(0, 0, WIDTH, HEIGHT);
//     ctx.beginPath();
//     ctx.arc(centerX, centerY, baseRadius, 0, 2 * Math.PI);
    
//     const mainGradient = ctx.createRadialGradient(
//       centerX,
//       centerY,
//       0,
//       centerX,
//       centerY,
//       baseRadius
//     );
//     mainGradient.addColorStop(0, '#FDC727');  
//     mainGradient.addColorStop(1, '#FDB827');  
    
//     ctx.fillStyle = mainGradient;
//     ctx.fill();
    
//     const glowGradient = ctx.createRadialGradient(
//       centerX,
//       centerY,
//       baseRadius - 10,
//       centerX,
//       centerY,
//       baseRadius + 10
//     );
//     glowGradient.addColorStop(0, 'rgba(253, 199, 39, 0.5)');
//     glowGradient.addColorStop(1, 'rgba(253, 199, 39, 0)');
    
//     ctx.beginPath();
//     ctx.arc(centerX, centerY, baseRadius, 0, 2 * Math.PI);
//     ctx.fillStyle = glowGradient;
//     ctx.fill();
//   };
  
//   const drawVisualization = (
//     canvas: HTMLCanvasElement,
//     analyser: AnalyserNode,
//     dataArray: Uint8Array,
//     bufferLength: number,
//     isFlipped: boolean = false
//   ) => {
//     const canvasCtx = canvas.getContext('2d');
//     if (!canvasCtx) return;
    
//     const WIDTH = canvas.width;
//     const HEIGHT = canvas.height;
//     const centerX = WIDTH / 2;
//     const centerY = HEIGHT / 2;
//     const baseRadius = Math.min(WIDTH, HEIGHT) / 2 - 50;
//     const maxWaveHeight = 50;
    
//     function draw() {
//       const id = requestAnimationFrame(draw);
//       setAnimationId(id);
      
//       analyser.getByteFrequencyData(dataArray);
      
//       canvasCtx.clearRect(0, 0, WIDTH, HEIGHT);
      
//       canvasCtx.beginPath();
//       canvasCtx.arc(centerX, centerY, baseRadius, 0, 2 * Math.PI);
//       const baseGradient = canvasCtx.createRadialGradient(
//         centerX,
//         centerY,
//         0,
//         centerX,
//         centerY,
//         baseRadius
//       );
//       baseGradient.addColorStop(0, '#FDC727');
//       baseGradient.addColorStop(1, '#FDB827');
//       canvasCtx.fillStyle = baseGradient;
//       canvasCtx.fill();
      
//       canvasCtx.beginPath();
//       for (let i = 0; i < bufferLength; i++) {
//         const angle = (i / bufferLength) * 2 * Math.PI;
//         let amplitude = dataArray[i] / 255.0;
//         amplitude = Math.pow(amplitude, 2);
//         const waveHeight = amplitude * maxWaveHeight;
//         const r = baseRadius + waveHeight;
//         const x = centerX + Math.cos(angle) * r;
//         const y = centerY + (isFlipped ? -Math.sin(angle) : Math.sin(angle)) * r;
        
//         if (i === 0) {
//           canvasCtx.moveTo(x, y);
//         } else {
//           canvasCtx.lineTo(x, y);
//         }
//       }
      
//       canvasCtx.closePath();
      
//       const waveGradient = canvasCtx.createRadialGradient(
//         centerX,
//         centerY,
//         baseRadius,
//         centerX,
//         centerY,
//         baseRadius + maxWaveHeight
//       );
//       waveGradient.addColorStop(0, '#FDC727');
//       waveGradient.addColorStop(1, '#FDB827');
//       canvasCtx.fillStyle = waveGradient;
//       canvasCtx.fill();
//     }
    
//     draw();
//   };

//   const { mutate: fetchAndPlayAudio } = useMutation({
//     mutationFn: () => {
//       if(briefingId){
//         return fetchAudio(briefingId);
//       }
//     },
//     onSuccess: ({ audioUrl }) => {
//       const audio = new Audio(audioUrl);
//       audioRef.current = audio;
//       const canvas = canvasRef.current;
//       const flippedCanvas = flippedCanvasRef.current;
  
//       if (canvas && flippedCanvas && audio) {
//         const audioContext = new (window.AudioContext || (window as any).webkitAudioContext)();
//         const analyser = audioContext.createAnalyser();
//         analyser.fftSize = 256;
  
//         const source = audioContext.createMediaElementSource(audio);
//         source.connect(analyser);
//         analyser.connect(audioContext.destination);
  
//         const bufferLength = analyser.frequencyBinCount;
//         const dataArray = new Uint8Array(bufferLength);
  
//         drawVisualization(canvas, analyser, dataArray, bufferLength);
//         drawVisualization(flippedCanvas, analyser, dataArray, bufferLength, true);
  
//         audio.addEventListener('ended', () => {
//           setTimeout(() => {
//             navigate('/quizzie');
//           }, 3000);
//         });
  
//         audio.play().catch((error) => console.error('Audio play failed:', error));
//         setIsPlaying(true);
//       }
//     },
//     onError: (error: any) => {
//       console.error('Failed to fetch or play audio:', error);
//     },
//   });
  

//   const handlePlayAudio = () => {
//     if (isPlaying || !briefingContentUrl) return;
//     fetchAndPlayAudio(); 
//   };
  
//   const handleStopAudio = () => {
//     setIsModalOpen(true); 

//   };
  
//   const confirmStopAudio = () => {
//     if (audioRef.current) {
//       audioRef.current.pause();
//       audioRef.current.currentTime = 0;
//     }
    
//     if (animationId !== null) {
//       cancelAnimationFrame(animationId);
//     }

//     setIsPlaying(false);
//     initializeCanvas(canvasRef.current);
//     initializeCanvas(flippedCanvasRef.current, true);
//     setIsModalOpen(false); 
//     navigate('/');  
//   };

//   const closeModal = () => {
//     setIsModalOpen(false);
//   };

//   return (
//     <Container>
//       <BriefTitle>{currentMonth}월 {currentDay}일 꿀모닝 브리핑</BriefTitle>
//       <CanvasContainer>
//         <Canvas ref={canvasRef} width="400" height="400" />
//         <Canvas ref={flippedCanvasRef} width="400" height="400" />
//       </CanvasContainer>
//       <ButtonContainer>
//         <Button onClick={handlePlayAudio} disabled={isPlaying}>
//           브리핑 시작
//         </Button>
//         <Button onClick={handleStopAudio} disabled={!isPlaying}>
//           브리핑 중지
//         </Button>
//       </ButtonContainer>

//       <ButtonContainer>
//         <Button
//           onClick={() => {
//             navigate('/quizzie');
//           }}
//         >
//           퀴즈 페이지로 이동하기
//         </Button>
//       </ButtonContainer>

//       <Modal4
//         isOpen={isModalOpen}
//         isClose={closeModal}
//         header="브리핑 중지"
//         icon="warning"
//         onConfirm={confirmStopAudio}
//       >
//         <p>브리핑을 중지하시겠습니까?</p>
//         <p>지금 중지하시면, 퀴즈를 풀 수 없고,</p>
//         <p>메인 페이지로 돌아갑니다.</p>
        
//       </Modal4>

//     </Container>
//   );
// };

// export default BriefingPage;

// const Container = styled.div`
//   display: flex;
//   flex-direction: column;
//   justify-content: space-between;
//   align-items: center;
//   width: 100%;
//   height: 100vh;
//   position: relative;
//   background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
//   overflow: hidden;
// `;

// const BriefTitle = styled.div`
//   margin: 10rem;
//   font-weight: bold;
//   font-size: 5rem;
//   color: white;
//   text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
//   z-index: 10;
// `;

// const CanvasContainer = styled.div`
//   display: flex;
//   justify-content: center;
//   align-items: center;
//   margin-top: 2rem;
//   z-index: 10;
//   position: relative;
// `;

// const Canvas = styled.canvas`
//   width: 500px;
//   height: 500px;
//   border-radius: 50%;
//   position: absolute;
// `;

// const Button = styled.button`
//   padding: 1.5rem 2rem;
//   font-size: 3.5rem;
//   font-weight: 800;
//   background: rgba(255, 255, 255, 0.2);
//   color: white;
//   border: 1px solid rgba(255, 255, 255, 0.3);
//   border-radius: 40px;
//   cursor: pointer;
//   margin-top: 2rem;
//   backdrop-filter: blur(10px);
//   transition: all 0.3s ease;
//   z-index: 10;

//   &:hover {
//     background: rgba(255, 255, 255, 0.3);
//     box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
//   }

//   &:disabled {
//     opacity: 0.5;
//     cursor: not-allowed;
//   }
// `;

// const ButtonContainer = styled.div`
//   display: flex;
//   gap: 4rem;
//   margin-bottom: 8rem;
// `;

import React, { useRef, useState, useEffect } from 'react';
import { useQueryClient, useMutation } from '@tanstack/react-query'
import { useNavigate  } from 'react-router-dom';
import { instance } from '@/api/axios';
import styled from 'styled-components';
import Modal4 from '../../component/Modal4';

interface AlarmStartResponse {
  morningCallUrl: string;
  briefingContent: string;
  briefingContentUrl: string;
  briefingId: number;
}

interface AlarmData {
  id: number;
  alarmTime: string;
  daysOfWeek: string;
  repeatFrequency: number;
  repeatInterval: number;
  isActive: number;
}

const fetchAudio = async (briefingId: number) => {
  const response = await instance.get(`/api/briefs/audio/summary/${briefingId}`, {
    responseType: 'blob',
  });
  const audioBlob = new Blob([response.data], { type: 'audio/mpeg' });
  return { audioUrl: URL.createObjectURL(audioBlob), response }; 
};


const BriefingPage: React.FC = () => {

  const navigate = useNavigate();
  const queryClient = useQueryClient();

  //prettier-ignore
  const alarmStartData = queryClient.getQueryData<AlarmStartResponse>(['alarmStartData']);
  // console.log('alarmStartData:', alarmStartData);
  const briefingContent = alarmStartData?.briefingContent;
  const briefingContentUrl = alarmStartData?.briefingContentUrl ?? '4357e443-9b1d-4417-857f-3ba4c16c7929.mp3';
  const defaultBriefingContentUrl = 'cbdfdd98-9519-4eda-9f85-f753a74bce7d.mp3';
  const briefingId = 81;

  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const flippedCanvasRef = useRef<HTMLCanvasElement | null>(null);
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [animationId, setAnimationId] = useState<number | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  
  const currentDate = new Date();
  const currentMonth = String(currentDate.getMonth() + 1);
  const currentDay = String(currentDate.getDate());

  useEffect(() => {
    initializeCanvas(canvasRef.current);
    initializeCanvas(flippedCanvasRef.current, true);
    
    return () => {
      if (animationId !== null ) {
        cancelAnimationFrame(animationId);
      }
    };
  }, []); //처음 렌더링 시에만 실행
  
  const initializeCanvas = (canvas: HTMLCanvasElement | null, isFlipped: boolean = false) => {
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    
    const WIDTH = canvas.width;
    const HEIGHT = canvas.height;
    const centerX = WIDTH / 2;
    const centerY = HEIGHT / 2;
    const baseRadius = Math.min(WIDTH, HEIGHT) / 2 - 50;
    
    ctx.clearRect(0, 0, WIDTH, HEIGHT);
    ctx.beginPath();
    ctx.arc(centerX, centerY, baseRadius, 0, 2 * Math.PI);
    
    const mainGradient = ctx.createRadialGradient(
      centerX,
      centerY,
      0,
      centerX,
      centerY,
      baseRadius
    );
    mainGradient.addColorStop(0, '#FDC727');  
    mainGradient.addColorStop(1, '#FDB827');  
    
    ctx.fillStyle = mainGradient;
    ctx.fill();
    
    const glowGradient = ctx.createRadialGradient(
      centerX,
      centerY,
      baseRadius - 10,
      centerX,
      centerY,
      baseRadius + 10
    );
    glowGradient.addColorStop(0, 'rgba(253, 199, 39, 0.5)');
    glowGradient.addColorStop(1, 'rgba(253, 199, 39, 0)');
    
    ctx.beginPath();
    ctx.arc(centerX, centerY, baseRadius, 0, 2 * Math.PI);
    ctx.fillStyle = glowGradient;
    ctx.fill();
  };
  
  const drawVisualization = (
    canvas: HTMLCanvasElement,
    analyser: AnalyserNode,
    dataArray: Uint8Array,
    bufferLength: number,
    isFlipped: boolean = false
  ) => {
    const canvasCtx = canvas.getContext('2d');
    if (!canvasCtx) return;
    
    const WIDTH = canvas.width;
    const HEIGHT = canvas.height;
    const centerX = WIDTH / 2;
    const centerY = HEIGHT / 2;
    const baseRadius = Math.min(WIDTH, HEIGHT) / 2 - 50;
    const maxWaveHeight = 50;
    
    function draw() {
      const id = requestAnimationFrame(draw);
      setAnimationId(id);
      
      analyser.getByteFrequencyData(dataArray);
      
      canvasCtx.clearRect(0, 0, WIDTH, HEIGHT);
      
      canvasCtx.beginPath();
      canvasCtx.arc(centerX, centerY, baseRadius, 0, 2 * Math.PI);
      const baseGradient = canvasCtx.createRadialGradient(
        centerX,
        centerY,
        0,
        centerX,
        centerY,
        baseRadius
      );
      baseGradient.addColorStop(0, '#FDC727');
      baseGradient.addColorStop(1, '#FDB827');
      canvasCtx.fillStyle = baseGradient;
      canvasCtx.fill();
      
      canvasCtx.beginPath();
      for (let i = 0; i < bufferLength; i++) {
        const angle = (i / bufferLength) * 2 * Math.PI;
        let amplitude = dataArray[i] / 255.0;
        amplitude = Math.pow(amplitude, 2);
        const waveHeight = amplitude * maxWaveHeight;
        const r = baseRadius + waveHeight;
        const x = centerX + Math.cos(angle) * r;
        const y = centerY + (isFlipped ? -Math.sin(angle) : Math.sin(angle)) * r;
        
        if (i === 0) {
          canvasCtx.moveTo(x, y);
        } else {
          canvasCtx.lineTo(x, y);
        }
      }
      
      canvasCtx.closePath();
      
      const waveGradient = canvasCtx.createRadialGradient(
        centerX,
        centerY,
        baseRadius,
        centerX,
        centerY,
        baseRadius + maxWaveHeight
      );
      waveGradient.addColorStop(0, '#FDC727');
      waveGradient.addColorStop(1, '#FDB827');
      canvasCtx.fillStyle = waveGradient;
      canvasCtx.fill();
    }
    
    draw();
  };

  const { mutate: fetchAndPlayAudio } = useMutation({
    mutationFn: () => {
      if (briefingId) {
        // briefingId가 있으면, 해당 ID로 API 호출
        return fetchAudio(briefingId);
      } else {
        // briefingId가 없으면 기본 URL을 사용해 오디오 설정
        return Promise.resolve({ audioUrl: 'cbdfdd98-9519-4eda-9f85-f753a74bce7d.mp3' });
      }
    },
    onSuccess: ({ audioUrl }) => {
      const audio = new Audio(audioUrl);
      audioRef.current = audio;
      const canvas = canvasRef.current;
      const flippedCanvas = flippedCanvasRef.current;
  
      if (canvas && flippedCanvas && audio) {
        const audioContext = new (window.AudioContext || (window as any).webkitAudioContext)();
        const analyser = audioContext.createAnalyser();
        analyser.fftSize = 256;
  
        const source = audioContext.createMediaElementSource(audio);
        source.connect(analyser);
        analyser.connect(audioContext.destination);
  
        const bufferLength = analyser.frequencyBinCount;
        const dataArray = new Uint8Array(bufferLength);
  
        drawVisualization(canvas, analyser, dataArray, bufferLength);
        drawVisualization(flippedCanvas, analyser, dataArray, bufferLength, true);
  
        audio.addEventListener('ended', () => {
          setTimeout(() => {
            navigate('/quizzie');
          }, 3000);
        });
  
        audio.play().catch((error) => console.error('Audio play failed:', error));
        setIsPlaying(true);
      }
    },
    onError: (error: any) => {
      console.error('Failed to fetch or play audio:', error);
    },
  });
  

  const handlePlayAudio = () => {
    if (isPlaying ) return;
    fetchAndPlayAudio(); 
  };
  
  const handleStopAudio = () => {
    setIsModalOpen(true); 

  };
  
  const confirmStopAudio = () => {
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current.currentTime = 0;
    }
    
    if (animationId !== null) {
      cancelAnimationFrame(animationId);
    }

    setIsPlaying(false);
    initializeCanvas(canvasRef.current);
    initializeCanvas(flippedCanvasRef.current, true);
    setIsModalOpen(false); 
    navigate('/');  
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  return (
    <Container>
      <BriefTitle>{currentMonth}월 {currentDay}일 꿀모닝 브리핑</BriefTitle>
      <CanvasContainer>
        <Canvas ref={canvasRef} width="400" height="400" />
        <Canvas ref={flippedCanvasRef} width="400" height="400" />
      </CanvasContainer>
      <ButtonContainer>
        <Button onClick={handlePlayAudio} disabled={isPlaying}>
          브리핑 시작
        </Button>
        <Button onClick={handleStopAudio} disabled={!isPlaying}>
          브리핑 중지
        </Button>
      </ButtonContainer>

      <ButtonContainer>
        <Button
          onClick={() => {
            navigate('/quizzie');
          }}
        >
          퀴즈 페이지로 이동하기
        </Button>
      </ButtonContainer>

      <Modal4
        isOpen={isModalOpen}
        isClose={closeModal}
        header="브리핑 중지"
        icon="warning"
        onConfirm={confirmStopAudio}
      >
        <p>브리핑을 중지하시겠습니까?</p>
        <p>지금 중지하시면, 퀴즈를 풀 수 없고,</p>
        <p>메인 페이지로 돌아갑니다.</p>
        
      </Modal4>

    </Container>
  );
};

export default BriefingPage;

const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  height: 100vh;
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
`;

const BriefTitle = styled.div`
  margin: 10rem;
  font-weight: bold;
  font-size: 5rem;
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  z-index: 10;
`;

const CanvasContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
  z-index: 10;
  position: relative;
`;

const Canvas = styled.canvas`
  width: 500px;
  height: 500px;
  border-radius: 50%;
  position: absolute;
`;

const Button = styled.button`
  padding: 1.5rem 2rem;
  font-size: 3.5rem;
  font-weight: 800;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 40px;
  cursor: pointer;
  margin-top: 2rem;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  z-index: 10;

  &:hover {
    background: rgba(255, 255, 255, 0.3);
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`;

const ButtonContainer = styled.div`
  display: flex;
  gap: 4rem;
  margin-bottom: 8rem;
`;