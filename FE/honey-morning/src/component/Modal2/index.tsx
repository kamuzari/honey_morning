import React from 'react';
import styled from 'styled-components';

interface Modal2Props {
  isOpen: boolean;
  isClose: () => void;
  header: string;
  icon: string;
  children: React.ReactNode;
  btnText?: string;
  actions?: React.ReactNode;
}

const Modal2: React.FC<Modal2Props> = ({
  isOpen,
  isClose,
  header,
  icon,
  children,
  btnText = '확인',
  actions,
}) => {
  if (!isOpen) return null;

  const handleOverlayClick = (e: React.MouseEvent<HTMLDivElement>) => {
    if (e.target === e.currentTarget) {
      isClose();
    }
  };

  return (
    <ModalOverlay onClick={handleOverlayClick}>
      <ModalContainer>
        <ModalHeader>
          <ModalIcon className="material-icons">{icon}</ModalIcon>
          <ModalTitle>{header}</ModalTitle>
        </ModalHeader>
        <ModalBody>{children}</ModalBody>
        <ModalActions>
          {actions || <ModalButton onClick={isClose}>{btnText}</ModalButton>}
        </ModalActions>
      </ModalContainer>
    </ModalOverlay>
  );
};

export default Modal2;

const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 1000;
`;

const ModalContainer = styled.div`
  background-color: white;
  border-radius: 2rem;
  width: 45rem;
  min-height: 40rem;
  text-align: center;
  z-index: 1001;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
`;

const ModalHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
  color: white;
  height: 7rem;
  background-color: var(--red-color);
  border-radius: 2rem 2rem 0 0;
  position: relative;
`;

const ModalIcon = styled.span`
  position: absolute;
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  font-size: 4rem;
  font-weight: light;
`;

const ModalTitle = styled.h2`
  margin: 0;
  font-size: 3rem;
  font-weight: bold;
`;

const ModalBody = styled.div`
  font-size: 2.5rem;
  font-weight: bold;
  margin: 5.5rem 5.5rem 1rem 5.5rem;
  text-align: center;
  white-space: pre-line; /* 줄바꿈이 가능하도록 설정 */
`;

const ModalActions = styled.div`
  display: flex;
  justify-content: center; /* 버튼을 중앙으로 배치 */
  padding: 1rem 4rem;
`;

const ModalButton = styled.button`
  background-color: var(--yellow-color);
  border: none;
  font-size: 50px;
  border-radius: 20px;
  padding: 20px 50px;
  cursor: pointer;
`;

const ActionButton = styled.button<{ disabled?: boolean }>`
  background-color: var(--darkblue-color);
  color: white;
  font-size: 2.3rem;
  font-weight: bold;
  min-width: 15rem;
  min-height: 7rem;
  border: none;
  padding: 1rem;
  margin: 3rem;
  border-radius: 8px;
  cursor: ${({ disabled }) => (disabled ? 'not-allowed' : 'pointer')};
`;


const HighlightedText = styled.span`
  color: red; /* 특정 텍스트만 빨간색으로 변경 */
`;

const AlertText = styled.span`
  color: red;
  font-size: 2rem;
`;