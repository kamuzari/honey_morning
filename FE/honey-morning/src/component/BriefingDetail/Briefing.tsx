import styled from 'styled-components';
import {Content, SmallTitle} from '@/pages/MyPage';
import {Container, HelpSpan, HelpContainer} from './Summary';
import {useState} from 'react';
import GlobalBtn from '../GlobalBtn';
const Briefing = () => {
  const shortData =
    ' 안녕하세요, 9월 3일 꿀 모닝 브리핑을 전해드립니다. 오늘의 미세먼지는 최악이네요. 마스크 꼭 챙기시길 바랍니다.오늘의 첫번째 뉴스는 경제 뉴스에요. 미국이 또 금리를 올려서 사람들을 힘들게 하고 있군요. 아 유럽에 사는 고양이 뉴스도 있어요. 유럽에만 사는 고양이 품종이 있다는 걸 알안녕하세요, 9월 3일 꿀 모닝 브리핑을 전해드립니다. 오늘의 미세먼지는 최악이네요. 마스크 꼭 챙기시길 바랍니다.오늘의 첫번째 뉴스는 경제 뉴스에요. 미국이 또 금리를 올려서 사람들을 힘들게 하고 있군요. 아 유럽에 사는 고양이 뉴스도 있어요. 유럽에만 사는 고양이 품종이 있다는 걸 알';
  const LongData =
    ' 안녕하세요, 9월 3일 꿀 모닝 브리핑을 전해드립니다. 오늘의 미세먼지는 최악이네요. 마스크 꼭 챙기시길 바랍니다.오늘의 첫번째 뉴스는 경제 뉴스에요. 미국이 또 금리를 올려서 사람들을 힘들게 하고 있군요. 아 유럽에 사는 고양이 뉴스도 있어요. 유럽에만 사는 고양이 품종이 있다는 걸 알 안녕하세요, 9월 3일 꿀 모닝 브리핑을 전해드립니다. 오늘의 미세먼지는 최악이네요. 마스크 꼭 챙기시길 바랍니다.오늘의 첫번째 뉴스는 경제 뉴스에요. 미국이 또 금리를 올려서 사람들을 힘들게 하고 있군요. 아 유럽에 사는 고양이 뉴스도 있어요. 유럽에만 사는 고양이 품종이 있다는 걸 알 안녕하세요, 9월 3일 꿀 모닝 브리핑을 전해드립니다. 오늘의 미세먼지는 최악이네요. 마스크 꼭 챙기시길 바랍니다.오늘의 첫번째 뉴스는 경제 뉴스에요. 미국이 또 금리를 올려서 사람들을 힘들게 하고 있군요. 아 유럽에 사는 고양이 뉴스도 있어요. 유럽에만 사는 고양이 품종이 있다는 걸 알';
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
        <GlobalBtn text="전체 듣기" $padding={4}></GlobalBtn>
        <LongText>{LongData}</LongText>
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
  line-height: 3.7rem;
`;

const LongText = styled.div`
  width: 90%;
  display: flex;
  overflow: scroll;
  text-overflow: ellipsis;
  height: 60rem;
  font-weight: 500;
  font-size: 3.2rem;
  line-height: 3.7rem;
  margin-top: 3rem;
`;

export default Briefing;
