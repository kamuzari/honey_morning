import React, { useRef, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';

const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  height: 100vh;
  position: relative;
  background-color: var(--darkblue-color);
  overflow: hidden;
`;

const BriefTitle = styled.div`
  margin: 5rem;
  font-weight: bold;
  font-size:5rem;
  color: white;
`;

const CanvasContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 10rem;
`;

const Canvas = styled.canvas`
  width: 800px;
  height: 1000px;
`;

const BeeImage = styled.img`
  width: 25rem;
  height: 25rem;
  object-fit: cover;
  margin: 5px 0;
  border-radius: 16px;
  z-index: 10;
`;

const Button = styled.button`
  padding: 1rem 2rem;
  font-size: 1.5rem;
  background-color: #2a4b9c;
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  margin-top: 1rem;

  &:hover {
    background-color: #1f3b7a;
  }
`;

const BriefingPage = () => {
  const navigate = useNavigate(); 

  const canvasRefLeft = useRef<HTMLCanvasElement | null>(null);
  const canvasRefRight = useRef<HTMLCanvasElement | null>(null);
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [animationId, setAnimationId] = useState<number | null>(null);


  const currentDate = new Date();
  // 오늘 날짜
  const currentMonth = String(currentDate.getMonth() + 1);
  const currentDay = String(currentDate.getDate());
  

  const handlePlayAudio = () => {
    if (isPlaying) return;

    const audio = new Audio('sample_briefing.mp3');
    audioRef.current = audio;

    // 오디오가 끝났을 때 페이지 이동 처리
    audio.addEventListener('ended', () => {
      setTimeout(() => {
        navigate('/quizzie');
      }, 3000); // 3초 대기 후 페이지 전환
    });

    const canvasLeft = canvasRefLeft.current;
    const canvasRight = canvasRefRight.current;

    if (canvasLeft && canvasRight && audio) {
      const audioContext = new (window.AudioContext || (window as any).webkitAudioContext)();
      const analyser = audioContext.createAnalyser();
      analyser.fftSize = 256; 

      const source = audioContext.createMediaElementSource(audio);
      source.connect(analyser);
      analyser.connect(audioContext.destination);

      const bufferLength = analyser.frequencyBinCount;
      const dataArray = new Uint8Array(bufferLength);

      const canvasCtxLeft = canvasLeft.getContext('2d');
      const canvasCtxRight = canvasRight.getContext('2d');
      if (!canvasCtxLeft || !canvasCtxRight) return;

      const WIDTH = canvasLeft.width;
      const HEIGHT = canvasLeft.height;

      function draw() {
        const id = requestAnimationFrame(draw);
        setAnimationId(id);

        analyser.getByteFrequencyData(dataArray);

        // 좌측 캔버스 그리기
        canvasCtxLeft!.clearRect(0, 0, WIDTH, HEIGHT);
        const gradientLeft = canvasCtxLeft!.createLinearGradient(0, 0, 0, HEIGHT);
        gradientLeft.addColorStop(0, '#FFE29D');
        gradientLeft.addColorStop(1, '#F29B05');
        canvasCtxLeft!.fillStyle = gradientLeft;

        const barWidth = WIDTH / bufferLength;
        let x = 0;

        for (let i = 0; i < bufferLength; i++) {
          const barHeight = dataArray[i] / 1.5;
          canvasCtxLeft!.fillRect(x, (HEIGHT / 2) - barHeight / 2, barWidth, barHeight);
          x += barWidth;
        }

        // 우측 캔버스 그리기 (좌우 반전)
        canvasCtxRight!.clearRect(0, 0, WIDTH, HEIGHT);
        canvasCtxRight!.save();
        canvasCtxRight!.scale(-1, 1);
        canvasCtxRight!.translate(-WIDTH, 0);

        const gradientRight = canvasCtxRight!.createLinearGradient(0, 0, 0, HEIGHT);
        gradientRight.addColorStop(0, '#FFE29D');
        gradientRight.addColorStop(1, '#F29B05');
        canvasCtxRight!.fillStyle = gradientRight;

        x = 0;

        for (let i = 0; i < bufferLength; i++) {
          const barHeight = dataArray[i] / 1.5;
          canvasCtxRight!.fillRect(x, (HEIGHT / 2) - barHeight / 2, barWidth, barHeight);
          x += barWidth;
        }

        canvasCtxRight!.restore();
      }

      draw();
      audio.play().catch((error) => {
        console.error('Audio play failed:', error);
      });

      setIsPlaying(true);
    }
  };

  const handleStopAudio = () => {
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current.currentTime = 0;
    }

    if (animationId) {
      cancelAnimationFrame(animationId);
    }

    setIsPlaying(false);
  };

  useEffect(() => {
    handlePlayAudio();
  }, [])

  return (
    <Container>
    <BriefTitle>{currentMonth}월 {currentDay}일 꿀모닝 브리핑</BriefTitle>
      <CanvasContainer>
        <Canvas ref={canvasRefRight} width="400" height="300" />
        <Canvas ref={canvasRefLeft} width="400" height="300" />
      </CanvasContainer>
      <BeeImage src="sleepingbee.webp" alt="Sleeping Bee" />
      <Button onClick={handlePlayAudio} disabled={isPlaying}>
        브리핑 시작
      </Button>
      <Button onClick={handleStopAudio} disabled={!isPlaying}>
        브리핑 중지
      </Button>
    </Container>
  );
};

export default BriefingPage;
