import styled from 'styled-components';
import {Content, SmallTitle} from '@/pages/MyPage';
import {Container} from './Summary';
import Streak from '../Streak/Streak';

interface QuizResponseDto {
    question: string;
    option1: string;
    option2: string;
    option3: string;
    option4: string;
    selectedOption: number;
    answerNumber: number;
}

const Quiz = ({quizDto}: { quizDto: QuizResponseDto[] }) => {
    return (
        <Container>
            <Content>
                <div className="smallTitleContainer">
                    <SmallTitle>퀴즈 결과</SmallTitle>
                </div>
                <StreakContainer>
                    <Streak/>
                </StreakContainer>
            </Content>
            <Content>
                <div className="smallTitleContainer">
                    <SmallTitle>문제</SmallTitle>
                </div>
                <ProblemContainer>
                    {quizDto.map((quiz, index) => (
                        <Problem key={index}>
                            <span>Q. {quiz.question}</span>
                            <div className="optionContainer">
                                {[quiz.option1, quiz.option2, quiz.option3, quiz.option4].map((opt, optIndex) => (
                                    <Option
                                        key={optIndex}
                                        $index={optIndex + 1}
                                        $pick={quiz.selectedOption}
                                        $answer={quiz.answerNumber}
                                    >
                                        <div className="check"/>
                                        {opt}
                                    </Option>
                                ))}
                            </div>
                        </Problem>
                    ))}
                </ProblemContainer>
            </Content>
        </Container>
    );
};

const StreakContainer = styled.div`
    display: flex;
    width: 100%;
    height: 50rem;
    justify-content: center;
    align-items: center;
    border: 1px solid lime;
`;

const ProblemContainer = styled.div`
    display: flex;
    flex-direction: column;
    width: 90%;
    // border: 1px solid red;
    // * {
    //   border: 1px solid red;
    // }
`;

const Problem = styled.div`
    display: flex;
    width: 100%;
    height: 20rem;
    margin: 0 0 4rem 0;
    flex-direction: column;

    span {
        font-size: 3.4rem;
        font-weight: bold;
        margin: 0 0 4rem 0;
    }

    .optionContainer {
        display: flex;
        justify-content: space-between;
    }
`;

const Option =
    styled.div <
        { $index: number, $pick: number, $answer: number }>
        `
            display: flex;
            justify-content: space-around;
            font-weight: bold;
            font-size: 3rem;
            width: 12rem;
            height: 8rem;
            align-items: center;
            margin: 0.2rem;
            border-radius: 15px;
            background-color: ${props =>
                    props.$index == props.$pick
                            ? 'var(--darkblue-color)'
                            : 'var(--lightblue-color)'};
            color: ${props => (props.$index == props.$pick ? 'var(--white-color)' : [])};

            .check {
                display: flex;
                border-radius: 15px;
                background-color: ${props =>
                        props.$index == props.$answer
                                ? 'var(--green-color)'
                                : props.$pick == props.$index && props.$pick != props.$answer
                                        ? 'var(--red-color)'
                                        : 'var(--white-color)'};
                width: 3rem;
                height: 3rem;
            }
        `;
export default Quiz;
