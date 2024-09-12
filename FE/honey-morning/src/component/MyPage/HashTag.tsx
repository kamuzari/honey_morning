import styled from 'styled-components';

interface HashTagProps {
  text: string;
  type: 'NEWS' | 'CUSTOM';
  selected?: boolean;
  onClick?:()=>void;
}

const HashTagContainer =
  styled.div <
  {$type: string, $selected?: boolean} >
  `
  display: inline-flex;
  max-width: 40rem;
  border-radius: 5rem;
  border: 3px solid black;
  justify-content: center;
  align-items: center;
  white-space: nowrap;
  height: 6rem;
  padding: 0 1.5rem 0 1.5rem;
  margin: 0 2rem 0 0;
  background-color: ${props =>
    props.$type == 'NEWS' ? (props.$selected ? 'var(--darkblue-color)' : 'var(--white-color)') : 'var(--yellow-color)'};
  span {
    font-size: 3rem;
    font-weight: bold;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
    display: inline-block;
    color: ${props => props.$selected ? "var(--white-color)" : "var(--black-color)"}
  }
`;

const HashTag: React.FC<HashTagProps> = props => {
  return (
    <HashTagContainer $type={props.type} $selected={props.selected} onClick={props.onClick}>
      <span># {props.text}</span>
    </HashTagContainer>
  );
};

export default HashTag;
