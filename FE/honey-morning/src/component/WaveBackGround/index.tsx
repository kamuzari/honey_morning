import React from 'react';
import styled, {keyframes} from 'styled-components';

// Define the keyframes for the drift animation
const drift = keyframes`
  from {
    transform: rotate(0deg);
  }
  to {
    transform:  rotate(360deg);
  }
`;

const moveLeft = keyframes`
  from {
    transform: translateX(0);
  }
  to {
    transform: translateX(-1000px);
  }
`;

// Styled components for the waves
const WaveOne = styled.div`
  opacity: 0.95;
  position: absolute;
  top: -330px;
  width: 3000px;
  height: 100%;
  transform-origin: 50% 48%;
  border-radius: 60%;
  background: var(--darkblue-color);
  animation: ${drift} 20000ms infinite linear;
  z-index: 1;
`;

const WaveTwo = styled(WaveOne)`
  background: #3773b7;
  opacity: 0.5;
  animation: ${drift} 16000ms infinite linear;
  z-index: 0;
`;

const WaveThree = styled(WaveOne)`
  background: #97a7e0;
  opacity: 0.3;
  animation: ${drift} 11000ms infinite linear;
  z-index: 0;
`;

// The WaveBackground component
const WaveBackground: React.FC = () => (
  <>
    <WaveOne />
    <WaveTwo />
    <WaveThree />
  </>
);

export default WaveBackground;
