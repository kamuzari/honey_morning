import React from 'react';
import styled from 'styled-components';

const Container = styled.div`
  display: flex;
  width: 100%;
  height: 100vh;
  color: var(--white-color);
  background-color: var(--darkblue-color);
  justify-content: center;

  * {
    border: 1px solid lime;
  }
`;
const ContentsContainer = styled.div`
  display: flex;
  flex-direction: column;
  width: 50%;
  align-items: center;
`;

const TimeContainer = styled.div`
  display: flex;
  width: 100%;
  height: 25rem;
`;

const ToggleContainer = styled.div`
  display: flex;
  width: 100%;
  height: 5rem;
  justify-content: center;
`;

const SettingContainer = styled.div`
  display: flex;
  width: 100%;
  height: 15rem;
  justify-content: center;
`;

const ButtonContainer = styled.div`
  display: flex;
  width: 100%;
  height: 5rem;
  justify-content: center;
`;

const AlarmSetting: React.FunctionComponent = () => {
  return (
    <Container>
      <ContentsContainer>
        <ToggleContainer>
          Whereas recognition of the inherent dignity
        </ToggleContainer>
        <TimeContainer>
          모든 인류 구성원의 천부의 존엄성과 동등하고 양도할 수 없는 권리를
          인정하는
        </TimeContainer>
        <SettingContainer></SettingContainer>
        <ButtonContainer></ButtonContainer>
      </ContentsContainer>
    </Container>
  );
};

export default AlarmSetting;
