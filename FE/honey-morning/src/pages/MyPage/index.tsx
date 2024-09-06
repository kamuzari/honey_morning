import styled from 'styled-components';
import HashTag from '@/component/AlarmSetting/HashTag';

const categoryList = [
  '정치',
  '경제',
  '사회',
  '생활/문화',
  '세계',
  '기술/IT',
  '연예',
  '스포츠',
];

const MyPage: React.FC = () => {
  return (
    <Container>
      <WhiteContainer>
        <Content>
          <div className="titleContainer">
            <Title>내 관심사</Title>
          </div>
          <div className="hashTagContainer">
            {categoryList.map((item) => <HashTag text={item} type='NEWS')}
          </div>
          <div className="smallTitleContainer">
            <SmallTitle>나만의 관심사</SmallTitle>
          </div>
          <div className="hashTagContainer">
            <HashTag text="유럽에 사는 고양이" type="CUSTOM" />
            <HashTag text="유럽에 사는 고양이" type="CUSTOM" />
            <HashTag text="유럽에 사는 고양이" type="CUSTOM" />
            <HashTag text="유럽에 사는 고양이" type="CUSTOM" />
            <HashTag text="유럽에 사는 고양이" type="CUSTOM" />
            <HashTag text="유럽에 사는 고양이" type="CUSTOM" />
          </div>
        </Content>
      </WhiteContainer>
    </Container>
  );
};

const Container = styled.div`
  display: flex;
  width: 100%;
  height: 100vh;
  background-color: var(--darkblue-color);
  justify-content: center;
  padding: 3rem;
  box-sizing: border-box;
  * {
    // border: 1px solid lime;
  }
`;

const WhiteContainer = styled.div`
  display: flex;
  width: 100%;
  flex-direction: column;
  background-color: white;
  border-radius: 3rem;
  padding: 5rem 0 0 0;
`;

const Content = styled.div`
  displya: flex;
  flex-direction: column;
  width: 100%;
  .titleContainer {
    display: flex;
    width: 100%;
  }

  .hashTagContainer {
    display: flex;
    width: 100%;
    box-sizing: border-box;
    padding: 3rem 0 3rem 4rem;

    overflow-x: auto;
    &::-webkit-scrollbar {
      display: none;
    }
  }

  .smallTitleContainer {
    display: flex;
    margin: 2rem;
    padding: 0 0 0 3rem;
  }
`;
const Title = styled.div`
  display: flex;
  width: 20rem;
  height: 6rem;
  border-radius: 0 10rem 10rem 0;
  background-color: var(--yellow-color);
  font-weight: bold;
  font-size: 4rem;
  padding: 0 0 0 5rem;
  align-items: center;
`;

const SmallTitle = styled.div`
  color: var(--darkblue-color);
  font-size: 3.5rem;
  font-weight: bold;
`;

export default MyPage;
