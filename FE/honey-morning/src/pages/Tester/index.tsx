import React, {useState} from 'react';
import Modal2 from '@/component/Modal2';
import styled from 'styled-components';

const TestPage = () => {
  const [isOpen, setIsOpen] = useState(true);

  const handleClose = () => {
    setIsOpen(false);
  };

  return (
    <div>
      <Modal2
        isOpen={isOpen}
        isClose={handleClose}
        header="수면 경고"
        icon="warning"
        actions={
          <>
            <ActionButton onClick={() => alert('네 클릭됨')}>네</ActionButton>
            <ActionButton onClick={() => alert('아니오 클릭됨')}>
              아니오
            </ActionButton>
          </>
        }
      >
        체류시간 확인용: 5{'\n\n'}
        정말 수면을 종료하시겠습니까?{'\n\n'}
        수면 시간이 <HighlightedText>2시간</HighlightedText> 이하입니다.{'\n\n'}
        지금 수면을 종료하면 브리핑이 생성되지 않고, 기록이 남지 않습니다.
        {'\n\n\n'}
        계속하시겠습니까?
      </Modal2>
    </div>
  );
};

export default TestPage;

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