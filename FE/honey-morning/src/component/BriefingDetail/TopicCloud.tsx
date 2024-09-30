import styled from 'styled-components';
import WordCloud from 'react-wordcloud';
import Slider from 'react-slick';
import {Topic} from './Summary';

// interface TopicResponseDto {
//   data: Topic[];
// }

// interface Topic {
//   topic_id: number;
//   topic_words: TopicWord[];
// }

// interface TopicWord{
//   word: string
//   weight: number
// }

const dummyData = {
  data: [
    {
      topic_id: 0,
      topic_words: [
        {
          word: '대통령',
          weight: 0.008147770538926125,
        },
        {
          word: '의원',
          weight: 0.006182737648487091,
        },
        {
          word: '대표',
          weight: 0.004943727049976587,
        },
        {
          word: '여사',
          weight: 0.004349950235337019,
        },
        {
          word: '국민',
          weight: 0.004345982801169157,
        },
        {
          word: '가구',
          weight: 0.00402170792222023,
        },
        {
          word: '대한',
          weight: 0.003747190348803997,
        },
        {
          word: '때문',
          weight: 0.0037150620482861996,
        },
        {
          word: '대해',
          weight: 0.0036698291078209877,
        },
        {
          word: '지금',
          weight: 0.0036570716183632612,
        },
        {
          word: '김건희',
          weight: 0.0035129401367157698,
        },
        {
          word: '민주당',
          weight: 0.003440628293901682,
        },
        {
          word: '미국',
          weight: 0.0032284909393638372,
        },
        {
          word: '서울',
          weight: 0.0031695105135440826,
        },
        {
          word: '금리',
          weight: 0.003022134769707918,
        },
        {
          word: '얘기',
          weight: 0.00301304180175066,
        },
        {
          word: '투자',
          weight: 0.002991551300510764,
        },
        {
          word: '문제',
          weight: 0.002940006786957383,
        },
        {
          word: '공천',
          weight: 0.0028753727674484253,
        },
        {
          word: '시장',
          weight: 0.002867048140615225,
        },
        {
          word: '억원',
          weight: 0.002846940653398633,
        },
        {
          word: '상황',
          weight: 0.0028138132765889168,
        },
        {
          word: '상승',
          weight: 0.002811474958434701,
        },
        {
          word: '이후',
          weight: 0.002632055664435029,
        },
        {
          word: '인하',
          weight: 0.0025765313766896725,
        },
        {
          word: '한국',
          weight: 0.002463108627125621,
        },
        {
          word: '사업',
          weight: 0.0024483301676809788,
        },
        {
          word: '체코',
          weight: 0.002443555509671569,
        },
        {
          word: '거래',
          weight: 0.00238050683401525,
        },
        {
          word: '관련',
          weight: 0.0023693754337728024,
        },
      ],
    },
    {
      topic_id: 1,
      topic_words: [
        {
          word: '대통령',
          weight: 0.0065469942055642605,
        },
        {
          word: '시장',
          weight: 0.005787363275885582,
        },
        {
          word: '금리',
          weight: 0.005361592397093773,
        },
        {
          word: '투자',
          weight: 0.004681284539401531,
        },
        {
          word: '미국',
          weight: 0.0043942625634372234,
        },
        {
          word: '반도체',
          weight: 0.004321189131587744,
        },
        {
          word: '상승',
          weight: 0.004076436161994934,
        },
        {
          word: '주가',
          weight: 0.004032230470329523,
        },
        {
          word: '거래',
          weight: 0.0033832029439508915,
        },
        {
          word: '하이닉스',
          weight: 0.0033784164115786552,
        },
        {
          word: '증권',
          weight: 0.0032964316196739674,
        },
        {
          word: '대표',
          weight: 0.0032733615953475237,
        },
        {
          word: '억원',
          weight: 0.0031134553719311953,
        },
        {
          word: '전망',
          weight: 0.0030314000323414803,
        },
        {
          word: '수출',
          weight: 0.0030274116434156895,
        },
        {
          word: '만원',
          weight: 0.002984290476888418,
        },
        {
          word: '사업',
          weight: 0.002963544335216284,
        },
        {
          word: '지수',
          weight: 0.002923353109508753,
        },
        {
          word: '기준',
          weight: 0.0028646690770983696,
        },
        {
          word: '대한',
          weight: 0.0027574426494538784,
        },
        {
          word: '서울',
          weight: 0.0027564833872020245,
        },
        {
          word: '관련',
          weight: 0.0027346378192305565,
        },
        {
          word: '한국',
          weight: 0.002703332109376788,
        },
        {
          word: '대해',
          weight: 0.0026860213838517666,
        },
        {
          word: '체코',
          weight: 0.0026731914840638638,
        },
        {
          word: '국민',
          weight: 0.002670441521331668,
        },
        {
          word: '증시',
          weight: 0.0026166525203734636,
        },
        {
          word: '인하',
          weight: 0.0025636348873376846,
        },
        {
          word: '공급',
          weight: 0.0025545868556946516,
        },
        {
          word: '가능성',
          weight: 0.0025261160917580128,
        },
      ],
    },
  ],
};

interface TopicCloudProps {
  topics: Topic[];
}

const TopicCloud: React.FC<TopicCloudProps> = ({topics}) => {
  console.log(dummyData.data.length);
  console.log('topics: ');
  console.log(topics);

  function repeatTopicCloud() {
    var topicCloud = [];
    for (var i = 0; i < dummyData.data.length; i++) {
      const words = dummyData.data[i].topic_words.map(({word, weight}) => ({
        text: word,
        value: weight,
      }));
      topicCloud.push(
        <WordCloud
          words={words}
          options={{rotations: 2, rotationAngles: [-90, 0]}}
          size={[600, 600]}
        />,
      );
    }
    return topicCloud;
  }

  var settings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 1,
    slidesToScroll: 1,
  };

  return (
    <Container>
      <Slider {...settings}>{repeatTopicCloud()}</Slider>
    </Container>
  );
};

const Container = styled.div`
  display: flex;
  width: 100%;
  height: 50rem;
  border: 1px solid lime;
`;

export default TopicCloud;
