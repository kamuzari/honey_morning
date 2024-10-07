import React, { useRef, useState, useEffect } from 'react';
import { useQueryClient, useMutation } from '@tanstack/react-query'
import { useNavigate  } from 'react-router-dom';
import { instance } from '@/api/axios'
import styled from 'styled-components';

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
  return { audioUrl: URL.createObjectURL(audioBlob), response };  // 오디오 Blob을 URL로 반환
};


const BriefingPage: React.FC = () => {

  const navigate = useNavigate();
  const queryClient = useQueryClient();

  //prettier-ignore
  const alarmStartData = queryClient.getQueryData<AlarmStartResponse>(['alarmStartData']);
  // console.log('alarmStartData:', alarmStartData);
  const briefingContent = alarmStartData?.briefingContent;
  const briefingContentUrl = alarmStartData?.briefingContentUrl;
  const briefingId = alarmStartData?.briefingId;

  // console.log('briefingContent: ', briefingContent);
  // console.log('briefingContentUrl: ', briefingContentUrl);
  // console.log('briefingId: ', briefingId);

  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const flippedCanvasRef = useRef<HTMLCanvasElement | null>(null);
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [animationId, setAnimationId] = useState<number | null>(null);
  
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
    
    // Add a subtle glow effect
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
      
      // Draw base circle
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
    mutationFn: () => fetchAudio(briefingId!),
    onSuccess: ({ audioUrl, response }) => {
      // console.log('Axios response:', response);
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
  
        // 시각화 함수 호출
        drawVisualization(canvas, analyser, dataArray, bufferLength);
        drawVisualization(flippedCanvas, analyser, dataArray, bufferLength, true);
  
        // 페이지 자동 전환
        audio.addEventListener('ended', () => {
          setTimeout(() => {
            navigate('/quizzie');
          }, 3000);
        })

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
    fetchAndPlayAudio();  // mutate 호출로 오디오 파일 가져오기 및 재생
  };
  
  const handleStopAudio = () => {
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
  margin: 5rem;
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
  padding: 1rem 2rem;
  font-size: 1.5rem;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 50px;
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
  gap: 1rem;
  margin-bottom: 2rem;
`;