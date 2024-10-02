import React, {useEffect, useState} from 'react';
import annyang from 'annyang';
import styled from 'styled-components';

interface SttProps {
  currentOptions: string[];
  answer: string;
  setAnswer: any;
}

const stt: React.FC<SttProps> = props => {
  const [currentOptions, setCurrentOptions] = useState(props.currentOptions);
  const [test, setTest] = useState(null);
  // 마이크 요청 함수
  const requestMicrophoneAccess = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({audio: true});
      console.log('마이크 권한 허용');
    } catch (error) {
      console.error('마이크 권한 거절:', error);
    }
  };

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
      // 마이크 권한 요청
      requestMicrophoneAccess();

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
      setTest(phrases[0].trim());
    });
  }, [props.currentOptions]);

  return (
    <>
      <Test>{test}</Test>
    </>
  );
};

const Test = styled.div`
  font-size: 10rem;
  color: blue;
`;

export default stt;
