// import { useRef } from 'react';
// import { Canvas, useFrame } from '@react-three/fiber';
// import { Mesh } from 'three';
// import { OrbitControls, Html } from '@react-three/drei';
// import {Link} from 'react-router-dom';
// import styled from 'styled-components';


// const MainPage = () => {
//   return (
//     <>
//       <Container>
//         <div>
//           <StyledLink to="/sleep">수면페이지</StyledLink>
//         </div>
//         <div>
//           <StyledLink to="/signin">로그인페이지</StyledLink>
//         </div>
//         <div>
//           <StyledLink to="/signup">회원가입페이지</StyledLink>
//         </div>
//         <div>
//           <StyledLink to="/alarm">알람페이지</StyledLink>
//         </div>
//         <div>
//           <StyledLink to="/briefing">브리핑페이지</StyledLink>
//         </div>
//         <div>
//           <StyledLink to="/quizzie">퀴즈페이지</StyledLink>
//         </div>
//       </Container>
//     </>
//   );
// };

// export default MainPage;

// const Container = styled.div`
//   display: flex;
//   flex-direction: column;
//   justify-content: center;
//   align-items: center;
//   width: 100%;
//   height: 100%;
//   gap: 20px;
//   border: 1px solid lime;
//   font-size: 5rem;
//   color: white;
// `;

// const StyledLink = styled(Link)`
//   color: white;
//   text-decoration: none;
// `;
import React, { useRef } from 'react';
import { Canvas } from '@react-three/fiber';
import { OrbitControls, Html } from '@react-three/drei';
import { Mesh } from 'three';
import styled from 'styled-components';

const Globe = () => {
  const globeRef = useRef<Mesh>(null!);

  return (
    <mesh ref={globeRef}>
      <sphereGeometry args={[3, 32, 32]} /> {/* 구 모양 지구 */}
      <meshStandardMaterial color="blue" wireframe={false} />
      {/* 이곳에 HTML 체크포인트를 추가할 수 있음 */}
      <Html position={[2, 0, 0]}>
        <CheckpointButton onClick={() => window.location.href = '/alarm'}>알람</CheckpointButton>
      </Html>
      <Html position={[-2, 1, 1]}>
        <CheckpointButton onClick={() => window.location.href = '/mypage'}>마이 페이지</CheckpointButton>
      </Html>
    </mesh>
  );
};

const ObjectComponent = () => {
  return (
    <CanvasContainer>
      <Canvas>
        <ambientLight intensity={0.5} /> {/* 전역 조명 */}
        <directionalLight position={[10, 10, 5]} /> {/* 방향성 조명 */}
        <Globe />
        {/* 마우스 조작만 가능하게 설정 */}
        <OrbitControls enableZoom={true} />
      </Canvas>
    </CanvasContainer>
  );
};

export default ObjectComponent;

const CanvasContainer = styled.div`
  width: 100%;
  height: 100%;
`;

const CheckpointButton = styled.button`
  background-color: yellow;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
  border: none;
`;
