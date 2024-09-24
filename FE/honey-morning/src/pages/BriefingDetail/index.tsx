import {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';
import styled from 'styled-components';
import {Container, Content, Title, WhiteContainer} from '../MyPage';
import Briefing from '@/component/BriefingDetail/Briefing';
import Summary from '@/component/BriefingDetail/Summary';
import Quiz from '@/component/BriefingDetail/Quiz';
import NavBar, {SoleMainNavBarProps} from '@/component/NavBar/NavBar';
import {getBrief} from "@/api/briefingApi";

function toLocalDate(localDateTime: string): string {
    return new Date(localDateTime).toISOString().split('T')[0];
}

const navType = ['요약', '브리핑', '퀴즈'];
const BriefingDetail = () => {
    const {brief_id} = useParams();
    const [nav, setNav] = useState('요약');
    const [briefingData, setBriefingData] = useState<any>(null);  // 전체 데이터
    const [loading, setLoading] = useState(true);  // 로딩 상태
    useEffect(() => {
        const getBriefingDetail = async () => {
            try {
                const response = await getBrief(brief_id);
                setBriefingData(response);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching briefing data:', error);
            }
        };

        getBriefingDetail();
    }, [brief_id]);

    if (loading) {
        return <div>Loading...</div>;  // 로딩 상태일 때 보여줄 UI
    }
    return (
        <Container>
            <WhiteContainer>
                <Content>
                    <div className="titleContainer">
                        <Title>{toLocalDate(briefingData.createdAt)}의 꿀 모닝</Title>
                    </div>
                </Content>
                <BriefingContainer>
                    <NavSection>
                        {navType.map(item => {
                            return (
                                <NavBarContent
                                    className={item == nav ? 'selected' : ''}
                                    onClick={() => {
                                        setNav(item);
                                    }}
                                >
                                    {item}
                                </NavBarContent>
                            );
                        })}
                    </NavSection>
                    <BriefingContent>
                        {nav == '요약' ? (
                            <Summary summaryDto={briefingData.summaryDto}/>  // 요약 데이터 전달
                        ) : nav == '브리핑' ? (
                            <Briefing briefDto={briefingData.briefDto}/>  // 브리핑 데이터 전달
                        ) : (
                            <Quiz quizDto={briefingData.quizDto}/>  // 퀴즈 데이터 전달
                        )}
                    </BriefingContent>
                </BriefingContainer>
                <NavBar props={SoleMainNavBarProps}/>
            </WhiteContainer>
        </Container>
    );
};

const BriefingContainer = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;

    height: 87%;

    * {
        // border: 1px solid red;
    }
`;

const NavSection = styled.div`
    display: flex;
    width: 95%;
    margin: 3rem 0 0 0;

    .selected {
        background-color: var(--darkblue-color);
        color: white;
    }
`;

const NavBarContent = styled.button`
    display: flex;
    width: 10rem;
    height: 5rem;
    margin-left: 1.5rem;
    justify-content: center;
    align-items: center;
    font-size: 3rem;
    background-color: var(--lightblue-color);
    border-radius: 15px 15px 0 0;
    font-weight: bold;
    border: none;
`;

const BriefingContent = styled.div`
    //   * {
    //     border: 1px solid red;
    //   }
    display: flex;
    width: 100%;
    justify-content: center;
    border-top: 2px solid black;
    border-radius: 15px 15px 0 0;
    padding: 2rem 0 0 0;
    margin: 0;
`;

export default BriefingDetail;
