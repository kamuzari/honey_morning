import React from "react";
import styled from "styled-components";

interface ButtonProps {
  text: string | null;
  type?: "button" | "submit" | "reset";
  $bgColor?: string;
  $borderColor?: string;
  $textColor?: string;
  $disabled?: boolean;
  $form?: string;
  $padding? : number;
  onClick?:
    | (() => void)
    | ((e: React.FormEvent<HTMLButtonElement>) => void)
    | ((e: React.MouseEvent<HTMLButtonElement>) => void);
}

const GlobalBtn: React.FC<ButtonProps> = ({
  text,
  type = "button",
  $bgColor,
  $disabled,
  $borderColor,
  $textColor,
  $form,
  $padding,
  onClick,
}) => {
  return (
    <Button
      type={type}
      $bgColor={$bgColor}
      $borderColor={$borderColor}
      $textColor={$textColor}
      form={$form}
      onClick={onClick}
      disabled={$disabled}
      $padding={$padding}
    >
      {text}
    </Button>
  );
};

const Button = styled.button<{
  $bgColor?: string;
  $borderColor?: string;
  $textColor?: string;
  $padding?: number;
}>`
  font-family: "Noto Sans KR";
  font-size: 3rem;
  font-weight: 700;
  border-radius: 100px;
  border: 2px solid ${(props) => props.$borderColor || "#000000"};
  padding: ${(props) => "0 " + props.$padding + "rem 0 " + props.$padding + "rem" };
  background-color: ${(props) => props.$bgColor || "var(--yellow-color)"};
  transition: background-color 0.3s ease;
  color: ${(props) => props.$textColor || "#000000"};
  &:disabled {
    cursor: not-allowed;
  }
`;

export default GlobalBtn;
