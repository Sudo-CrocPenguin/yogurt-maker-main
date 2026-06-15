import { Icon } from "./Icon";

interface NoticeProps {
  message: string;
  title?: string;
  tone?: "info" | "error" | "success";
}

export function Notice({ message, title, tone = "info" }: NoticeProps) {
  return (
    <div className={`notice notice-${tone}`} role={tone === "error" ? "alert" : "status"}>
      <Icon name={tone === "success" ? "check" : tone === "error" ? "alert" : "activity"} />
      <div>
        {title ? <strong>{title}</strong> : null}
        <p>{message}</p>
      </div>
    </div>
  );
}
