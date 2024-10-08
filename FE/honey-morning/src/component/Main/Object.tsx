import React, { useRef, useState, useEffect, Suspense, useCallback } from 'react';
import { Canvas, useFrame, useLoader } from '@react-three/fiber';
import { OrbitControls, Html } from '@react-three/drei';
import { Vector3, Mesh, TextureLoader, SphereGeometry, MeshPhongMaterial } from 'three';
import { useNavigate } from 'react-router-dom';
import {useQueryClient} from '@tanstack/react-query';
import styled from 'styled-components';
import Modal3 from '@/component/Modal3';

const Globe = ({ handleNavClick }: { handleNavClick: (path: string) => void }) => {
  const queryClient = useQueryClient();
  //prettier-ignore
  const userInfo = queryClient.getQueryData<{id: number, role: string, email: string, username: string}>(['userInfo']);

  const globeRef = useRef<Mesh>(null!);
  const [alarmVisible, setAlarmVisible] = useState(true);
  const [mypageVisible, setMypageVisible] = useState(true);
  const [signinVisible, setSigninVisible] = useState(true);
  const [signupVisible, setSignupVisible] = useState(true);
  const [sleepVisible, setSleepVisible] = useState(true);
  const [buttonScale, setButtonScale] = useState(1);

  const colorMap = useLoader(TextureLoader, './images/world.jpg');

  useEffect(() => {
    if (globeRef.current) {
      const geometry = new SphereGeometry(3, 64, 64);
      const material = new MeshPhongMaterial({
        map: colorMap,
        specular: 0x333333,
        shininess: 15,
        bumpMap: colorMap,
        bumpScale: 0.01,
      });
      globeRef.current.geometry = geometry;
      globeRef.current.material = material;
    }
  }, [colorMap]);

  const isButtonVisible = (buttonPosition: Vector3, cameraPosition: Vector3, globePosition: Vector3) => {
    const directionToButton = buttonPosition.clone().sub(globePosition).normalize(); // 지구에서 버튼으로의 벡터
    const directionToCamera = cameraPosition.clone().sub(globePosition).normalize(); // 지구에서 카메라로의 벡터
  
    // 두 벡터 사이의 각도가 90도 이내이면 버튼이 카메라에서 보이는 상태로 처리
    const dotProduct = directionToButton.dot(directionToCamera);
    return dotProduct > 0.1; // 0에 가까울수록 버튼이 경계에 있음, 0.1로 설정하여 미세하게 보이지 않도록 함
  };
  

  useFrame(({ camera }) => {
    if (globeRef.current) {
      globeRef.current.rotation.y += 0.001;

      const globePosition = new Vector3().setFromMatrixPosition(globeRef.current.matrixWorld);
      const cameraPosition = new Vector3().setFromMatrixPosition(camera.matrixWorld);

      const distanceToCamera = camera.position.distanceTo(globePosition);
      const scale = Math.max(1 / distanceToCamera, 0.5); // 거리에 따라 버튼 크기를 계산
      setButtonScale(scale * 5); // 버튼 크기 조정, 원하는 배율로 곱하기

      // 각 버튼의 위치를 구의 표면에 고정
      const alarmPos = new Vector3(3.5, 0, 0).applyQuaternion(globeRef.current.quaternion).add(globePosition);
      const mypagePos = new Vector3(-3.5, 1, 1).applyQuaternion(globeRef.current.quaternion).add(globePosition);
      const signinPos = new Vector3(2.5, 1, -2.5).applyQuaternion(globeRef.current.quaternion).add(globePosition);
      const signupPos = new Vector3(-2.5, -1, -2.5).applyQuaternion(globeRef.current.quaternion).add(globePosition);
      const sleepPos = new Vector3(1.5, 0, 1).applyQuaternion(globeRef.current.quaternion).add(globePosition);

      // 각 버튼이 카메라에서 보이는지 여부를 확인
      setAlarmVisible(isButtonVisible(alarmPos, cameraPosition, globePosition));
      setMypageVisible(isButtonVisible(mypagePos, cameraPosition, globePosition));
      setSigninVisible(isButtonVisible(signinPos, cameraPosition, globePosition));
      setSignupVisible(isButtonVisible(signupPos, cameraPosition, globePosition));
      setSleepVisible(isButtonVisible(signupPos, cameraPosition, globePosition));
    }
  });

  return (
    <group>
      <mesh ref={globeRef}>
        <sphereGeometry args={[3, 64, 64]} />
      </mesh>

      {userInfo && alarmVisible && (
        <Html position={[3.5, 0, 0]} style={{ transform: `scale(${buttonScale})` }}>
          <CheckpointButton onClick={() => window.location.href = '/alarmsetting'}>알람</CheckpointButton>
        </Html>
      )}
      {userInfo && mypageVisible && (
        <Html position={[-3.5, 1, 1]} style={{ transform: `scale(${buttonScale})` }}>
          <CheckpointButton onClick={() => window.location.href = '/mypage'}>마이 페이지</CheckpointButton>
        </Html>
      )}
      {userInfo && sleepVisible && (
        <Html position={[1.5, 0, 1]} style={{ transform: `scale(${buttonScale})` }}>
          <CheckpointButton onClick={() => window.location.href = '/sleep'}>수면 페이지</CheckpointButton>
        </Html>
      )}
      {!userInfo && signinVisible && (
        <Html position={[2.5, 1, -2.5]} style={{ transform: `scale(${buttonScale})` }}>
          <CheckpointButton onClick={() => window.location.href = '/signin'}>로그인</CheckpointButton>
        </Html>
      )}
      {!userInfo && signupVisible && (
        <Html position={[-2.5, -1, -2.5]} style={{ transform: `scale(${buttonScale})` }}>
          <CheckpointButton onClick={() => window.location.href = '/signup'}>회원가입</CheckpointButton>
        </Html>
      )}
    </group>
  );
};

const LoadingFallback = () => (
  <Html center>
    <LoadingText>Loading Earth texture...</LoadingText>
  </Html>
);

const ErrorFallback = ({ error }: { error: Error }) => (
  <Html center>
    <ErrorText>Error loading Earth texture: {error.message}</ErrorText>
  </Html>
);

const ObjectComponent = () => {

  const navigate = useNavigate();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalTitle, setModalTitle] = useState('');
  const [modalMessage, setModalMessage] = useState('');
  const [modalBtnText, setModalBtnText] = useState('');
  const [modalIcon, setModalIcon] = useState('warning');

  const openModal = useCallback(() => {
    setIsModalOpen(true);
  }, []);

  const closeModal = useCallback(() => {
    setIsModalOpen(false);
    navigate('/signin');
  }, [navigate]);

  const handleNavClick = useCallback(
    (path: string) => {
      const userInfo = false;
      if (!userInfo) {
        setModalTitle('Caution');
        setModalMessage('서비스를 사용하기 위해서는\n\n로그인을 해주세요.');
        setModalIcon('error_outline');
        openModal();
      } else {
        navigate(path);
      }
    },
    [navigate, openModal]
  );

  return (
    <CanvasContainer>
      <Canvas camera={{ position: [0, 0, 12], fov: 70 }}>
        <ambientLight intensity={0.5} />
        <directionalLight position={[1, 0, 1]} intensity={1} />
        <Suspense fallback={<LoadingFallback />}>
          <ErrorBoundary FallbackComponent={ErrorFallback}>
            <Globe handleNavClick={handleNavClick} />
          </ErrorBoundary>
        </Suspense>
        <OrbitControls enableZoom={true} />
      </Canvas>
      <Modal3Wrapper>
        <Modal3
          isOpen={isModalOpen}
          isClose={closeModal}
          header={modalTitle}
          icon="error_outline"
        >
          <MessageWrapper dangerouslySetInnerHTML={{ __html: modalMessage }} />
        </Modal3>
      </Modal3Wrapper>
    </CanvasContainer>
  );
};

export default ObjectComponent;


class ErrorBoundary extends React.Component<
  { children: React.ReactNode; FallbackComponent: React.ComponentType<{ error: Error }> },
  { hasError: boolean; error: Error | null }
> {
  constructor(props: { children: React.ReactNode; FallbackComponent: React.ComponentType<{ error: Error }> }) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error) {
    return { hasError: true, error };
  }

  render() {
    if (this.state.hasError) {
      return <this.props.FallbackComponent error={this.state.error!} />;
    }

    return this.props.children;
  }
}


const CanvasContainer = styled.div`
  width: 100%;
  height: 90vh;
  //  background: linear-gradient(135deg, var(--darkblue-color), var(--lightblue-color));
`;

const CheckpointButton = styled.button`
  background-color: rgba(255, 255, 255, 0.7);
  color: #333;
  padding: 10px 20px;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  white-space: nowrap;
  font-weight: bold;
  width: auto;
  text-align: center;
  transition: background-color 0.3s;

  &:hover {
    background-color: rgba(255, 255, 255, 0.9);
  }
`;

const LoadingText = styled.div`
  color: white;
  font-size: 24px;
`;

const ErrorText = styled.div`
  color: red;
  font-size: 24px;
`;

const MessageWrapper = styled.div`
  margin: 2rem 0;
  padding: 1rem 0;
  min-height: 5rem;
  text-align: center;
  white-space: pre-line;  // 줄바꿈을 처리
`;

const Modal3Wrapper = styled.div`
  z-index: 9999;
`;