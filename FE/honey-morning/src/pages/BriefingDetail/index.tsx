import {useState} from 'react';
import styled from 'styled-components';
import {Title, Container, WhiteContainer, Content} from '../MyPage';
import Briefing from '@/component/BriefingDetail/Briefing';
import Summary from '@/component/BriefingDetail/Summary';
import Quiz from '@/component/BriefingDetail/Quiz';
import {SoleMainNavBarProps} from '@/component/NavBar/NavBar';
import NavBar from '@/component/NavBar/NavBar';

interface BriefingDetailData {
  date: string;
}

const data: BriefingDetailData = {
  date: '2024년 9월 3일',
};

const navType = ['요약', '브리핑', '퀴즈'];

const BriefingDetail = () => {
  const [nav, setNav] = useState('퀴즈');
  return (
    <Container>
      <WhiteContainer>
        <Content>
          <div className="titleContainer">
            <Title>{data.date}의 꿀 모닝</Title>
          </div>
        </Content>
        <BriefingContainer>
          <NavSection>
            {navType.map(item => {
              return (
                <NavBarContent
                  className={item == nav ? 'selected' : ''}
                  onClick={() => {
                    setNav(item);
                  }}
                >
                  {item}
                </NavBarContent>
              );
            })}
          </NavSection>
          <BriefingContent>
            {nav == '요약' ? (
              <Summary />
            ) : nav == '브리핑' ? (
              <Briefing />
            ) : (
              <Quiz />
            )}
          </BriefingContent>
        </BriefingContainer>
        <NavBar props={SoleMainNavBarProps} />
      </WhiteContainer>
    </Container>
  );
};

const BriefingContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;

  height: 87%;
  * {
    // border: 1px solid red;
  }
`;

const NavSection = styled.div`
  display: flex;
  width: 95%;
  margin: 3rem 0 0 0;
  .selected {
    background-color: var(--darkblue-color);
    color: white;
  }
`;

const NavBarContent = styled.button`
  display: flex;
  width: 10rem;
  height: 5rem;
  margin-left: 1.5rem;
  justify-content: center;
  align-items: center;
  font-size: 3rem;
  background-color: var(--lightblue-color);
  border-radius: 15px 15px 0 0;
  font-weight: bold;
  border: none;
`;

const BriefingContent = styled.div`
  //   * {
  //     border: 1px solid red;
  //   }
  display: flex;
  width: 100%;
  justify-content: center;
  border-top: 2px solid black;
  border-radius: 15px 15px 0 0;
  padding: 2rem 0 0 0;
  margin: 0;
`;

export default BriefingDetail;
