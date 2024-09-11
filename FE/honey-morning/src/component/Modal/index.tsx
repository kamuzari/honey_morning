import styled from 'styled-components';

interface ModalProps {
  isOpen: boolean;
  isClose: () => void;
  title?: string;
  message?: string;
  btnText?: string;
}

const ToLocate = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
`;

const Container = styled.div`
  display: flex;
  flex-direction: column;

  width: 600px;
  height: 1000px;
  padding: 50px;

  border: solid 8px var(--yellow-color);
  background-color: white;
  border-radius: 30px;
  box-shadow: 10px 5px 5px gray;

  .TitleArea {
    margin-bottom: 50px;
    font-size: 100px;
  }

  .MessageArea {
    margin-bottom: 50px;
    font-size: 50px;
  }

  .ButtonArea {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    margin: 10px;
    margin-top: auto;
  }
`;

const ModalButton = styled.button`
  text: white;
  background-color: var(--yellow-color);
  border: none;
  font-size: 50px;

  border-radius: 20px;
  padding: 20px 50px;
`;

const Modal: React.FC<ModalProps> = ({
  isOpen,
  isClose,
  title,
  message,
  btnText = '확인',
}) => {
  if (!isOpen) return null;
  return (
    <ToLocate>
      <Container>
        <div className="TitleArea">{title}</div>
        <div className="MessageArea">{message}</div>
        <div className="ButtonArea">
          <ModalButton onClick={isClose}>{btnText}</ModalButton>
        </div>
      </Container>
    </ToLocate>
  );
};

export default Modal;
