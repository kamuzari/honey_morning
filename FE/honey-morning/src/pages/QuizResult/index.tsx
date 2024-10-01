import styled from 'styled-components';
import {useLocation} from 'react-router-dom';
import {instance} from '@/api/axios';
import React, { useState, useEffect } from 'react';
import {saveAlarmResult} from '@/api/alarmApi/index'


// Typescript
interface AlarmResultData {
  count: number,
  isAttending: number
}

const ShowQuizResult: React.FC = () => {



  const location = useLocation();

  const {correctCount} = location.state || {correctCount: 0};  
  // 상태 변수 선언
  const [alarmCategoryInfo, setAlarmCategoryInfo] = useState(null);
  const [alarmResultInfo, setAlarmResultInfo] = useState(null);
  const [streakInfo, setStreakInfo] = useState(null);


  
  useEffect(() => {
    const fetchStreak = async () => {
      try {
        const data = await fetchStreakData();
        setStreakInfo(data);
      } catch (error) {
        console.error(`[Error] data: ${error}`);
      }
    };

    fetchStreak(); // 데이터 fetch 호출
  }, []);

    useEffect(() => {
      const fetchCategory = async () => {
        try {
          const data = await fetchCategoryData();
          setAlarmCategoryInfo(data);
        } catch (error) {
          console.error(`[Error] data: ${error}`);
        }
      };
  
      fetchCategory(); // 데이터 fetch 호출
    }, []);

  useEffect(() => {
    const fetchResult = async () => {
      try {
        const data = await fetchResultData();
        setAlarmResultInfo(data);
      } catch (error) {
        console.error(`[Error] data: ${error}`);
      }
    };

    fetchResult(); // 데이터 fetch 호출
  }, []);

  const fetchStreakData = async () => {
    const {data} = await instance.get(`/api/alarms/result/streak`);
    return data;
  };
  const fetchCategoryData = async () => {
    const {data} = await instance.get(`/api/alarms/category`);
    return data;
  };
  const fetchResultData = async () => {
    const {data} = await instance.get(`/api/alarms/result`);
    return data;
  };

  return (
    <TopContainer>
      <CharacterArea>캐릭터</CharacterArea>
      <div className="MessageArea">
        {correctCount > 0 ? 'Misson Completed!' : (
  <>
    {'Streak Broken!'}<br />
    {'Keep Trying!'}
  </>
)}
      </div>
      <StatsArea>
        <CategoryArea>
          <div className="Category">카테고리</div>
          <div className="CategoryName">
            {alarmCategoryInfo ? (
              alarmCategoryInfo.map((category, index) => (
                <div key={index}>
                  <p className="material-icons">category</p>
                  <p>{category.word}</p>
                </div>
              ))
            ) : (
              <p>카테고리 정보 없음</p>
            )}
          </div>
        </CategoryArea>
        <CorrectAnswersArea>
          <div className="Answer">정답 수</div>
          <div className="AnswerCount">
            <p className="material-icons">check</p>
            <p>{correctCount} / 2 </p>
          </div>
        </CorrectAnswersArea>
        <StreakArea>
          <div className="Streak">스트릭</div>
          <div className="StreakDays">
            <p className="material-icons">event</p>
            <p>{streakInfo}</p>
          </div>
        </StreakArea>
      </StatsArea>
      <ToStreakButton>스트릭 확인하기</ToStreakButton>
    </TopContainer>
  );
};

const TopContainer = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 100%;

  .MessageArea {
    display: flex;
    justify-content: center;
    margin: 150px;
    font-size: 70px;
    font-weight: 500;
    text-align: center;
    color: var(--darkblue-color);

    line-height: 120%;
  }
`;

const CharacterArea = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 200px auto 100px;

  border: black solid 1px;
  height: 800px;
  width: 500px;
  font-size: 100px;
`;

const StatsArea = styled.div`
  display: flex;
  justify-content: center;
  font-size: 50px;
  width: 100%;
  gap: 30px;
`;

const CategoryArea = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 350px;
  width: 300px;
  border-radius: 30px;
  background-color: var(--yellow-color);

  .Category {
    color: white;
    display: flex;
    justify-content: center;
    padding-bottom: 10px;
  }

  .CategoryName {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    gap: 15px;

    background-color: white;
    color: var(--yellow-color);
    border-radius: 20px;
    width: 250px;
    height: 250px;
    font-size: 30px
  }

    .CategoryName div {
    display: flex;
    align-items: center;
    justify-content: between;

    gap: 15px;
  }

  .material-icons {
    font-size: 50px;
  }
`;

const CorrectAnswersArea = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 350px;
  width: 300px;
  border-radius: 30px;
  background-color: var(--green-color);

  .Answer {
    color: white;
    display: flex;
    justify-content: center;
    padding-bottom: 10px;
  }
  .AnswerCount {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 15px;

    background-color: white;
    color: var(--green-color);
    border-radius: 20px;
    width: 250px;
    height: 250px;
  }
  .material-icons {
    font-size: 50px;
  }
`;
const StreakArea = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 350px;
  width: 300px;
  border-radius: 30px;
  background-color: var(--mediumblue-color);

  .Streak {
    color: white;
    display: flex;
    justify-content: center;
    padding-bottom: 10px;
  }
  .StreakDays {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 15px;

    background-color: white;
    color: var(--mediumblue-color);
    border-radius: 20px;
    width: 250px;
    height: 250px;
  }

  .material-icons {
    font-size: 50px;
  }
`;

const ToStreakButton = styled.button`
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 150px auto;
  width: 950px;
  height: 150px;
  background-color: var(--darkblue-color);
  border-radius: 30px;
  color: white;
  font-size: 60px;
`;

export default ShowQuizResult;
