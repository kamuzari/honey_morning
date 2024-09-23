import styled from 'styled-components';

const MainPage = () => {
  return (
    <>
      <Container>
        <div>Object</div>
      </Container>
    </>
  );
};

const Head = styled.div``;

const Container = styled.div`
  display: flex;
  align-items: center;
  width: 100%;
  height: 100%;
  border: 1px solid lime;
  font-size: 5rem;
  color: white;
`;

export default MainPage;
