import {Content, SmallTitle} from '@/pages/MyPage';
import styled from 'styled-components';
import HashTag from '../MyPage/HashTag';
import TopicCloud from './TopicCloud';
import {useState} from 'react';

const CATEGORY = [
  '정치',
  '경제',
  '사회',
  '생활/문화',
  'IT/과학',
  '세계',
  '연예',
  '스포츠',
];

const customCategory = ['유럽에 사는 고양이'];

export interface Topic {
  topic_id: number;
  topic_words: Word[];
}

export interface Word {
  word: string;
  weight: number;
}

interface SummaryDto {
  topicModelResponses: Topic[];
  categories: string[];
}

const Summary = ({summaryDto}: {summaryDto: SummaryDto}) => {
  const [isHelpOpen, setIsHelpOpen] = useState(false);
  const categories = summaryDto.categories || [];

  const basicCategory = categories.filter((category: string) =>
    CATEGORY.includes(category),
  );
  const customCategory = categories.filter(
    (category: string) => !CATEGORY.includes(category),
  );

  return (
    <Container>
      <Content>
        <div className="smallTitleContainer">
          <SmallTitle>카테고리</SmallTitle>
        </div>

        <div className="hashTagContainer">
          {basicCategory.map((item: string) => {
            return (
              <HashTag
                key={item}
                text={item}
                type="NEWS"
              ></HashTag>
            );
          })}
        </div>

        <div className="hashTagContainer">
          {customCategory.map((item: string) => {
            return (
              <HashTag
                key={item}
                text={item}
                type="CUSTOM"
              ></HashTag>
            );
          })}
        </div>
      </Content>
      <Content>
        <div className="smallTitleContainer">
          <SmallTitle>토픽 클라우드</SmallTitle>
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
              <span>AI / 빅데이터 기술로 생성한 워드 클라우드 기술입니다.</span>
            </div>
            <div className="notification">
              <span>워드 클라우드란?</span>
              <span>
                각 토픽별 키워드 비중에 따라 눈에 잘 보이게 그려드려요!
              </span>
            </div>
          </HelpContainer>
        ) : (
          []
        )}
        <WordCloudContainer>
          <TopicCloud topics={summaryDto.topicModelResponses} />
        </WordCloudContainer>
      </Content>
    </Container>
  );
};
export const Container = styled.div`
  display: flex;
  width: 100%;
  flex-direction: column;
  align-items: center;
`;

export const HelpSpan = styled.span`
  display: flex;
  margin-left: 1.2rem;
  font-size: 4rem;
`;

const WordCloudContainer = styled.div`
  width: 100%;
  height: 100%;
  // display: flex;
  justify-content: center;
  backgroud-color: black;
`;

export const HelpContainer = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
  padding: 0 0 0 4rem;

  .notification {
    display: inline-flex;
    flex-direction: column;
    background-color: var(--lightblue-color);
    border-radius: 20px;
    box-sizing: border-box;
    padding: 1rem;
    margin-bottom: 2rem;

    span {
      font-size: 2.3rem;
      color: var(--darkblue-color);
      padding: 0.7rem 0 0.7rem 0;
    }

    span:nth-child(1) {
      font-weight: 900;
    }
  }

  .notification:nth-child(1) {
    width: 90%;
    text-align: center;
  }

  .notification:nth-child(2) {
    width: 90%;
    font-weight: 500;
  }
`;

export default Summary;
