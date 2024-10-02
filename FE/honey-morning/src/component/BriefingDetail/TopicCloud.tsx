import React, {useState} from 'react';
import WordCloud from 'react-wordcloud';
import ItemsCarousel from 'react-items-carousel';
import {Topic} from './Summary';
import styled from 'styled-components';
import {Bar} from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import {title} from 'process';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
);

interface TopicCloudProps {
  topics: Topic[];
}

const topicOptions: any = {
  rotations: 2,
  rotationAngles: [-90, 0],
  scale: 'sqrt',
  spiral: 'archimedean',
  fontSizes: [20, 100],
};

const barOptions: any = {
  indexAxis: 'y',
  scales: {
    x: {
      grid: {
        display: false,
      },
      ticks: {
        display: false,
      },
    },
    y: {
      grid: {
        display: false,
      },
      ticks: {
        font: {
          size: 40,
          weight: 1000,
        },
      },
    },
  },
  plugins: {
    legend: {
      display: false,
    },
    tooltip: {
      enabled: true,
      bodyFont: {
        size: 20,
      },
      titleFont: {
        size: 20,
      },
      callbacks: {
        label: function (context) {
          return `${context.dataset.label}: ${context.raw.toFixed(4)}`;
        },
      },
    },
  },
};

const TopicCloud: React.FC<TopicCloudProps> = ({topics}) => {
  const [activeItemIndex, setActiveItemIndex] = useState(0);
  const chevronWidth = 40;

  // 상위 5개의 키워드 추출
  const topKeywords = topics[activeItemIndex]?.topic_words
    .sort((a, b) => b.weight - a.weight)
    .slice(0, 5);

  // 막대그래프 데이터 설정
  const data = {
    labels: topKeywords?.map(word => word.word),
    datasets: [
      {
        label: '키워드 비중',
        data: topKeywords?.map(word => word.weight),
        backgroundColor: [
          '#FFCA1A',
          '#FFD94D',
          '#FFE880',
          '#FFF099',
          '#FFF59D',
        ],
      },
    ],
  };

  // 동그라미로 인덱스 표시
  const indicators = topics.map((_, index) => (
    <Indicator
      key={index}
      active={index === activeItemIndex}
    />
  ));

  return (
    <Container>
      <ItemsCarousel
        requestToChangeActive={setActiveItemIndex}
        activeItemIndex={activeItemIndex}
        numberOfCards={1}
        gutter={20}
        leftChevron={<StyledButton>{'<'}</StyledButton>}
        rightChevron={<StyledButton>{'>'}</StyledButton>}
        outsideChevron
        chevronWidth={chevronWidth}
      >
        {topics.map(topic => (
          <WordCloudContainer key={topic.topic_id}>
            <WordCloud
              words={topic.topic_words.map(word => ({
                text: word.word,
                value: Number(word.weight.toFixed(4)),
              }))}
              options={topicOptions}
            />
          </WordCloudContainer>
        ))}
      </ItemsCarousel>

      {topKeywords && (
        <BarContainer>
          <h3>상위 5개 키워드</h3>
          <Bar
            data={data}
            options={barOptions}
          />
        </BarContainer>
      )}
      <IndicatorContainer>{indicators}</IndicatorContainer>
    </Container>
  );
};

const Container = styled.div`
  border: 1px solid #ccc;
  justify-content: center;
  align-items: center;
  padding: 5rem;
`;

const WordCloudContainer = styled.div`
  background-color: #f8f9fa;
  background-size: cover;
  background-position: center;
  padding: 2rem;
`;

const StyledButton = styled.button`
  background-color: white;
  color: var(--darkblue-color);
  border: none;
  border-radius: 30%;
  font-size: 2.5rem;
  weight: 500,
  cursor: pointer;
`;
const BarContainer = styled.div`
  width: 100%;
  margin-top: 5rem;
  h3 {
    font-size: 3rem;
    font-weight: 500;
    text-align: center;
  }
`;

const IndicatorContainer = styled.div`
  display: flex;
  justify-content: center;
  margin-top: 1rem;
`;

const Indicator =
  styled.div <
  {active: boolean} >
  `
  width: 24px;
  height: 24px;
  margin: 0 12px;
  background-color: ${props => (props.active ? '#25387C' : '#ddd')};
  border-radius: 50%;
`;
export default TopicCloud;
