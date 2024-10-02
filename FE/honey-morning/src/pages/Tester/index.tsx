import React from 'react';
import {useQueryClient} from '@tanstack/react-query';
import styled from 'styled-components';

// interface Quiz {
//   id: number;
//   question: string;
//   answer: number;
//   option1: string;
//   option2: string;
//   option3: string;
//   option4: string;
//   quizUrl: string;
// }

// interface AlarmStartResponse {
//   morningCallUrl: string;
//   quizzes: Quiz[];
//   briefingContent: string;
//   briefingContentUrl: string;
// }

const TestPage = () => {
  // const queryClient = useQueryClient();
  // const alarmStartData =
  //   queryClient.getQueryData < AlarmStartResponse > ['alarmStartData'];
  return (
    <div>
      <h2>저장된 알람 데이터</h2>
      {/* <pre>{JSON.stringify(alarmStartData, null, 2)}</pre> */}
    </div>
  );
};

export default TestPage;
