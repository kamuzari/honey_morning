import React from 'react';
import WordCloud from 'react-wordcloud';
import Slider from 'react-slick';
import {Topic} from './Summary'; // Topic 타입을 Summary 파일에서 가져옴
import styled from 'styled-components';

interface TopicCloudProps {
  topics: Topic[];
}

const options: any = {
  rotations: 2,
  rotationAngles: [-90, 0],
  scale: 'sqrt',
  spiral: 'archimedean',
  fontSizes: [20, 100],
};

// 캐러셀 슬라이더 설정
const sliderSettings = {
  dots: true,
  infinite: true,
  speed: 500,
  vertical: false, // 가로 슬라이드를 위해 vertical을 false로 설정
  slidesToShow: 1,
  slidesToScroll: 1,
  arrows: true, // 이전/다음 버튼 활성화
};

const TopicCloud: React.FC<TopicCloudProps> = ({topics}) => {
  return (
    <Container>
      <Slider {...sliderSettings}>
        <div>
          {topics.map(topic => (
            <div key={topic.topic_id}>
              {/* 각 topic에 대한 WordCloud 생성 */}
              <WordCloud
                words={topic.topic_words.map(word => ({
                  text: word.word,
                  value: word.weight,
                }))}
                options={options}
              />
            </div>
          ))}
        </div>
      </Slider>
    </Container>
  );
};

const Container = styled.div`
  width: 40rem;
  height: 40rem;
  background-color: #f0f0f0;
  border: 1px solid #ccc;
  padding: 1rem;
  display: flex;
  justify-content: center;
  align-items: center;

  .slick-slide {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .slick-dots {
    bottom: -2rem; // dots(슬라이드 표시) 위치 조정
  }
`;

export default TopicCloud;
