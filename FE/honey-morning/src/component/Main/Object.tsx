import React, { useRef, useState, useEffect, Suspense } from 'react';
import { Canvas, useFrame, useLoader } from '@react-three/fiber';
import { OrbitControls, Html } from '@react-three/drei';
import { Vector3, Mesh, TextureLoader, SphereGeometry, MeshPhongMaterial } from 'three';
import styled from 'styled-components';

const Globe = () => {
  const globeRef = useRef<Mesh>(null!);
  const [alarmVisible, setAlarmVisible] = useState(true);
  const [mypageVisible, setMypageVisible] = useState(true);
  const [signinVisible, setSigninVisible] = useState(true);
  const [signupVisible, setSignupVisible] = useState(true);

  const colorMap = useLoader(TextureLoader, './images/test2.png');

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
    const directionToButton = buttonPosition.clone().sub(globePosition).normalize();
    const directionToCamera = cameraPosition.clone().sub(globePosition).normalize();
    return directionToButton.dot(directionToCamera) > 0;
  };

  useFrame(({ camera }) => {
    if (globeRef.current) {
      globeRef.current.rotation.y += 0.001;

      const globePosition = new Vector3().setFromMatrixPosition(globeRef.current.matrixWorld);
      const cameraPosition = new Vector3().setFromMatrixPosition(camera.matrixWorld);

      // 각 버튼의 위치를 구의 표면에 고정
      const alarmPos = new Vector3(3.5, 0, 0).applyQuaternion(globeRef.current.quaternion).add(globePosition);
      const mypagePos = new Vector3(-3.5, 1, 1).applyQuaternion(globeRef.current.quaternion).add(globePosition);
      const signinPos = new Vector3(2.5, 1, -2.5).applyQuaternion(globeRef.current.quaternion).add(globePosition);
      const signupPos = new Vector3(-2.5, -1, -2.5).applyQuaternion(globeRef.current.quaternion).add(globePosition);

      // 각 버튼이 카메라에서 보이는지 여부를 확인
      setAlarmVisible(isButtonVisible(alarmPos, cameraPosition, globePosition));
      setMypageVisible(isButtonVisible(mypagePos, cameraPosition, globePosition));
      setSigninVisible(isButtonVisible(signinPos, cameraPosition, globePosition));
      setSignupVisible(isButtonVisible(signupPos, cameraPosition, globePosition));
    }
  });

  return (
    <group>
      <mesh ref={globeRef}>
        <sphereGeometry args={[3, 64, 64]} />
      </mesh>

      {alarmVisible && (
        <Html position={[3.5, 0, 0]}>
          <CheckpointButton onClick={() => window.location.href = '/alarm'}>알람</CheckpointButton>
        </Html>
      )}
      {mypageVisible && (
        <Html position={[-3.5, 1, 1]}>
          <CheckpointButton onClick={() => window.location.href = '/mypage'}>마이 페이지</CheckpointButton>
        </Html>
      )}
      {signinVisible && (
        <Html position={[2.5, 1, -2.5]}>
          <CheckpointButton onClick={() => window.location.href = '/signin'}>로그인</CheckpointButton>
        </Html>
      )}
      {signupVisible && (
        <Html position={[-2.5, -1, -2.5]}>
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
  return (
    <CanvasContainer>
      <Canvas camera={{ position: [0, 0, 10], fov: 45 }}>
        <ambientLight intensity={0.5} />
        <directionalLight position={[1, 0, 1]} intensity={1} />
        <Suspense fallback={<LoadingFallback />}>
          <ErrorBoundary FallbackComponent={ErrorFallback}>
            <Globe />
          </ErrorBoundary>
        </Suspense>
        <OrbitControls enableZoom={true} />
      </Canvas>
    </CanvasContainer>
  );
};

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

export default ObjectComponent;

const CanvasContainer = styled.div`
  width: 100%;
  height: 100vh;
`;

const CheckpointButton = styled.button`
  background-color: rgba(255, 255, 255, 0.7);
  color: #333;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
  border: none;
  font-weight: bold;
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