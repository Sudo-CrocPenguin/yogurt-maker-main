import type { ButtonHTMLAttributes, ReactNode } from "react";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  icon?: ReactNode;
  variant?: "primary" | "secondary" | "danger" | "ghost";
}

export function Button({ children, className = "", icon, type = "button", variant = "secondary", ...props }: ButtonProps) {
  return (
    <button className={`button button-${variant} ${className}`} type={type} {...props}>
      {icon}
      <span>{children}</span>
    </button>
  );
}
