import {useNavigate} from 'react-router-dom';
import styled from 'styled-components';
export interface NavIconProps {
  $bgColor: string;
  $textColor: string;
  text: string;
  to?: string;
  onClick?: () => void;
}
const NavIcon = (props: NavIconProps) => {
  const navigate = useNavigate();

  const handleClick = () => {
    if (props.onClick) {
      props.onClick();
    } else if (props.to) {
      navigate(props.to);
    }
  };
  return (
    <Container
      $bgColor={props.$bgColor}
      $textColor={props.$textColor}
      onClick={handleClick}
    >
      <span className="material-icons">{props.text}</span>
    </Container>
  );
};

//prettier-ignore
const Container = styled.div<{$bgColor: string, $textColor: string}>`
    display: flex;
    background-color: ${props => props.$bgColor};
    color: ${props => props.$textColor};
    border-radius: 100%;
    padding: 0.5rem;
    cursor: pointer;
    span {
        font-size: 7.5rem;
    }
    
`;

export default NavIcon;
