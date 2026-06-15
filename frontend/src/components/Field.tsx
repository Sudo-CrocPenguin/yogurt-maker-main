import type { InputHTMLAttributes, ReactNode, SelectHTMLAttributes, TextareaHTMLAttributes } from "react";

interface FieldShellProps {
  children: ReactNode;
  hint?: string;
  label: string;
}

function FieldShell({ children, hint, label }: FieldShellProps) {
  return (
    <label className="field">
      <span>{label}</span>
      {children}
      {hint ? <small>{hint}</small> : null}
    </label>
  );
}

interface InputFieldProps extends InputHTMLAttributes<HTMLInputElement> {
  hint?: string;
  label: string;
}

export function InputField({ hint, label, ...props }: InputFieldProps) {
  return (
    <FieldShell hint={hint} label={label}>
      <input {...props} />
    </FieldShell>
  );
}

interface TextAreaFieldProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  hint?: string;
  label: string;
}

export function TextAreaField({ hint, label, ...props }: TextAreaFieldProps) {
  return (
    <FieldShell hint={hint} label={label}>
      <textarea {...props} />
    </FieldShell>
  );
}

interface SelectFieldProps extends SelectHTMLAttributes<HTMLSelectElement> {
  hint?: string;
  label: string;
}

export function SelectField({ children, hint, label, ...props }: SelectFieldProps) {
  return (
    <FieldShell hint={hint} label={label}>
      <select {...props}>{children}</select>
    </FieldShell>
  );
}
