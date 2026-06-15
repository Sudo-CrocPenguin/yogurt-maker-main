type IconName =
  | "activity"
  | "alert"
  | "batch"
  | "check"
  | "close"
  | "edit"
  | "plus"
  | "recipe"
  | "refresh"
  | "search"
  | "temperature";

interface IconProps {
  name: IconName;
  size?: number;
}

const paths: Record<IconName, string> = {
  activity: "M4 12h3l2-7 4 14 2-7h5",
  alert: "M12 3 2.8 20h18.4L12 3Zm0 6v5m0 3h.01",
  batch: "M6 3h12v4l-2 2v10a2 2 0 0 1-2 2H10a2 2 0 0 1-2-2V9L6 7V3Zm2 0h8M9 14h6",
  check: "m4 12 5 5L20 6",
  close: "M6 6l12 12M18 6 6 18",
  edit: "M4 20h4L19 9l-4-4L4 16v4Zm10-14 4 4",
  plus: "M12 5v14M5 12h14",
  recipe: "M7 3h8l4 4v14H7a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2Zm8 0v5h5M8 12h8M8 16h8",
  refresh: "M20 6v5h-5M4 18v-5h5M18 11a6 6 0 0 0-10-4L4 11m2 2a6 6 0 0 0 10 4l4-4",
  search: "M10.5 18a7.5 7.5 0 1 1 0-15 7.5 7.5 0 0 1 0 15Zm5.3-2.2L21 21",
  temperature: "M14 14.8V5a2 2 0 0 0-4 0v9.8a4 4 0 1 0 4 0Z"
};

export function Icon({ name, size = 18 }: IconProps) {
  return (
    <svg
      aria-hidden="true"
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      focusable="false"
    >
      <path d={paths[name]} />
    </svg>
  );
}
