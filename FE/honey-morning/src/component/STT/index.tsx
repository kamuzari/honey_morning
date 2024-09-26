import React, {useEffect, useState} from 'react';
import annyang from 'annyang';

interface SttProps {
  currentOptions: string[];
  answer: string;
  setAnswer: any;
}

const stt: React.FC<SttProps> = props => {
  const [currentOptions, setCurrentOptions] = useState(props.currentOptions);
  const optionCheck = (phrase: string, currentOptions: string[]) => {
    for (var option = 0; option < 4; option++) {
      for (var i = 0; i < phrase.length; i++) {
        if (currentOptions[option].substring(i, i + phrase.length) == phrase) {
          console.log('일치하는 것을 찾았습니다!');
          props.setAnswer(option);
        }
      }
    }
  };
  useEffect(() => {
    // annyang이 정의되어 있는지 확인
    if (annyang) {
      annyang.debug();
      annyang.setLanguage('ko');
      annyang.start();

      return () => {
        annyang.abort(); // 컴포넌트가 언마운트될 때 음성 인식 중단
      };
    }
  }, []);

  useEffect(() => {
    // 음성 인식 결과가 나올 때 실행
    setCurrentOptions(props.currentOptions);
    annyang.addCallback('result', (phrases: string[]) => {
      optionCheck(phrases[0].trim(), props.currentOptions);
    });
  }, [props.currentOptions]);

  return <></>;
};

export default stt;
