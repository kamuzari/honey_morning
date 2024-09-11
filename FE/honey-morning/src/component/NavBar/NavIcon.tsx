import {useNavigate} from 'react-router-dom';
import styled from 'styled-components';
export interface NavIconProps {
  $bgColor: string;
  $textColor: string;
  text: string;
  to: string;
}
const NavIcon = (props: NavIconProps) => {
  const navigate = useNavigate();
  return (
    <Container
      $bgColor={props.$bgColor}
      $textColor={props.$textColor}
      onClick={() => {
        navigate(props.to);
      }}
    >
      <span className="material-icons">{props.text}</span>
    </Container>
  );
};

const Container =
  styled.div <
  {$bgColor: string, $textColor: string} >
  `
    display: flex;
    background-color: ${props => props.$bgColor};
    color: ${props => props.$textColor};
    border-radius: 100%;
    padding: 0.5rem;
    span {
        font-size: 7.5rem;
    }
    
`;

export default NavIcon;
