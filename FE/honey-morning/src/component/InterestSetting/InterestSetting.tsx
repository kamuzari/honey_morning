import styled from 'styled-components';
import HashTag from '@/component/MyPage/HashTag';
import {categoryList} from '../../pages/MyPage';
import {useInterestStore} from '@/store/InterestStore';
import {useState} from 'react';

export const HashTagSelect = () => {
  const {selectedCategory, toggleCategory} = useInterestStore();
  return (
    <HashTagContainer>
      {categoryList.map((item, index) => {
        return (
          <HashTag
            text={item}
            key={index}
            type="NEWS"
            selected={selectedCategory.includes(item) ? true : false}
            onClick={() => {
              toggleCategory(item);
            }}
          ></HashTag>
        );
      })}
    </HashTagContainer>
  );
};

export const CustomHashTagSelect = () => {
  const {
    selectedCategory,
    selectedCustomCategory,
    interestInput,
    setInterestInput,
    deleteCustomCategory,
  } = useInterestStore();

  return (
    <CustomHashTagContainer>
      {selectedCustomCategory.map((item, index) => {
        return (
          <>
            <CustomHashTagBox key={index}>
              <HashTag
                text={item}
                type="CUSTOM"
              ></HashTag>
              <span
                className="material-icons"
                onClick={() => {
                  deleteCustomCategory(item);
                }}
              >
                close
              </span>
            </CustomHashTagBox>
          </>
        );
      })}
      {selectedCategory.length + selectedCustomCategory.length < 3 &&
      selectedCustomCategory.length < 2 ? (
        <input
          placeholder="관심사를 입력하세요."
          value={interestInput}
          onChange={event => {
            setInterestInput(event.target.value);
          }}
        />
      ) : (
        []
      )}
    </CustomHashTagContainer>
  );
};
const HashTagContainer = styled.div`
  display: flex;
  width: 90%;
  height: 12rem;
  box-sizing: border-box;
  overflow-x: scroll;
  overflow-y: hidden;
  padding: 3rem 0 3rem 0;
  &::-webkit-scrollbar {
    display: none;
  }
`;

const CustomHashTagContainer = styled.div`
  display: flex;
  flex-direction: column;
  width: 90%;
  font-size: 5rem;
  padding: 1rem 0 2rem 0;
  height: 15rem;
  justify-content: space-between;
  input {
    display: flex;
    background-color: var(--yellow-color);
    width: 50rem;
    height: 8rem;
    border: none;
    border-radius: 25rem;
    font-size: 4rem;
    font-weight: bold;
    padding: 0 0 0 3rem;
  }
`;

const CustomHashTagBox = styled.div`
  display: flex;
  width: 70%;
  .material-icons {
    display: flex;
    align-items: center;
    font-size: 3rem;
    font-weight: bold;
    color: var(--red-color);
  }
`;
