import styled from 'styled-components';
import NavIcon from './NavIcon';
import {NavIconProps} from './NavIcon';

export interface NavBarProps {
  props: NavIconProps[];
}

export const SoleMainNavBarProps: NavIconProps[] = [
  {
    $bgColor: 'var(--darkblue-color)',
    $textColor: 'var(--white-color)',
    text: 'language',
    to: '/',
  },
];

const NavBar: React.FC<NavBarProps> = ({props}) => {
  return (
    <Container $num={props.length}>
      {props.map((prop, index) => {
        return (
          <NavIcon
            key={index}
            $bgColor={prop.$bgColor}
            $textColor={prop.$textColor}
            text={prop.text}
            to={prop.to}
            onClick={prop.onClick}
          />
        );
      })}
    </Container>
  );
};

const Container =
  styled.div <
  {$num: number} >
  `
  display: flex;
  width: 100%;
  height: 8rem;
  padding: 0;
  justify-content: ${props => (props.$num > 1 ? 'space-between' : 'center')};
  align-items: center;
  // background-color: red;
`;

export default NavBar;
