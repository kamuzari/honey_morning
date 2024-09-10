import {create} from 'zustand';
import {categoryList} from '@/pages/MyPage';

interface InterestState {
  selectedCategory: string[];
  selectedCustomCategory: string[];
  interestInput: string;
  toggleCategory: (input: string) => void;
  addCustomCategory: () => void;
  deleteCustomCategory: (input: string) => void;
  setInterestInput: (input: string) => void;
}

export const useInterestStore =
  create <
  InterestState >
  (set => ({
    selectedCategory: [],
    selectedCustomCategory: [],
    interestInput: '',
    toggleCategory: input =>
      set(state => {
        const {selectedCategory, selectedCustomCategory, interestInput} = state;
        const isSelected = selectedCategory?.includes(input);

        if (selectedCategory.length <= 1 && isSelected) {
          alert('최소 한 개 이상의 뉴스 카테고리를 선택해야 합니다.');
          return {};
        }
        if (
          (selectedCategory.length + selectedCustomCategory.length >= 3 &&
            !isSelected) ||
          (interestInput.trim().length > 0 && selectedCategory.length == 2)
        ) {
          alert('최대 세 개의 카테고리만 선택할 수 있습니다.');
          return {};
        }

        return {
          selectedCategory: isSelected
            ? selectedCategory.filter(item => item !== input) // 카테고리 제거
            : [...selectedCategory, input], // 카테고리 추가
        };
      }),
    addCustomCategory: () =>
      set(state => {
        const {selectedCustomCategory, interestInput} = state;
        if (interestInput.length >= 1 && interestInput.trim().length >= 1) {
          return {
            selectedCustomCategory: [...selectedCustomCategory, interestInput],
            interestInput: '',
          };
        } else {
          return {};
        }
      }),
    deleteCustomCategory: input =>
      set(state => {
        const {selectedCustomCategory} = state;
        return {
          selectedCustomCategory: selectedCustomCategory.filter(
            item => item !== input,
          ),
        };
      }),
    setInterestInput: input => set({interestInput: input}),
  }));
