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
      {props.map(prop => {
        return (
          <NavIcon
            $bgColor={prop.$bgColor}
            $textColor={prop.$textColor}
            text={prop.text}
            to={prop.to}
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
  padding: 2rem 0 0 0;
  justify-content: ${props => (props.$num > 1 ? 'space-between' : 'center')};
  align-items: center;
`;

export default NavBar;
