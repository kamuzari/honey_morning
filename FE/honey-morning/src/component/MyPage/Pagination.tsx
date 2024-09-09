import styled from 'styled-components';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: React.Dispatch<React.SetStateAction<number>>;
}

const Pagination = (props: PaginationProps) => {
  const pageNumbers = [];
  const currentPage = props.currentPage;
  const totalPages = props.totalPages;
  const onPageChange = props.onPageChange;

  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i);
  }

  return (
    <PageContainer>
      <button onClick={() => onPageChange(currentPage - 1)}>
        <span className="material-icons">chevron_left</span>
      </button>
      {pageNumbers.map(number => (
        <button onClick={() => onPageChange(number)}>
          <span className={number == currentPage ? 'selected' : ''}>
            {number}
          </span>
        </button>
      ))}
      <button onClick={() => onPageChange(currentPage + 1)}>
        <span className="material-icons">chevron_right</span>
      </button>
    </PageContainer>
  );
};

const PageContainer = styled.ul`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 4rem;
  margin-top: 2rem;
  button {
    border: none;
    background-color: transparent;
    padding: 0;

    span {
      display: flex;
      font-size: 4rem;
      justify-content: center;
      align-items: center;
      width: 3rem;

      color: var(--lightblue-color);
      font-weight: bold;
    }
    .material-icons {
      font-size: 5rem;
    }
    .selected {
      color: var(--darkblue-color);
    }
  }
`;
export default Pagination;
