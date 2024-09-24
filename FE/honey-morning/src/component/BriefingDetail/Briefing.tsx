import styled from 'styled-components';
import {Content, SmallTitle} from '@/pages/MyPage';
import {Container, HelpSpan, HelpContainer} from './Summary';
import {useState} from 'react';
import GlobalBtn from '../GlobalBtn';
const Briefing = ({briefDto}: {briefDto: any}) => {


  const shortData = briefDto.summary
  const LongData =briefDto.content
  const [isHelpOpen, setIsHelpOpen] = useState(false);
  return (
    <Container>
      <Content>
        <div className="smallTitleContainer">
          <SmallTitle>
            <HoneySpan>꿀</HoneySpan> 모닝 브리핑
          </SmallTitle>
          <HelpSpan
            className="material-icons"
            onClick={() => {
              setIsHelpOpen(!isHelpOpen);
            }}
          >
            help
          </HelpSpan>
        </div>
        {isHelpOpen ? (
          <HelpContainer>
            <div className="notification">
              <span>해당 날짜에 들었던 알람 브리핑입니다.</span>
            </div>
          </HelpContainer>
        ) : (
          []
        )}
        <ShortText>{shortData}</ShortText>
      </Content>
      <Content>
        <div className="smallTitleContainer">
          <SmallTitle>
            <HoneySpan>꿀</HoneySpan> 모닝 전체 브리핑
          </SmallTitle>
        </div>
        <GlobalBtn
          text="전체 듣기"
          $padding={4}
        ></GlobalBtn>
        <LongText $isHelpOpen={isHelpOpen}>{LongData}</LongText>
      </Content>
    </Container>
  );
};

const HoneySpan = styled.span`
  color: var(--yellow-color);
`;

const ShortText = styled.div`
  display: flex;
  font-size: 3.2rem;
  width: 90%;
  height: 30rem;
  overflow: scroll;
  text-overflow: ellipsis;
  font-weight: 500;
  line-height: 5rem;
`;

const LongText =
  styled.div <
  {$isHelpOpen: boolean} >
  `
  width: 90%;
  display: flex;
  overflow: scroll;
  text-overflow: ellipsis;
  height: ${props => (props.$isHelpOpen ? '50rem' : '55rem')};
  font-weight: 500;
  font-size: 3.2rem;
  line-height: 5rem;
  margin-top: 3rem;
`;

export default Briefing;
