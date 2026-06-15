import type { ReactNode } from "react";

interface PanelProps {
  actions?: ReactNode;
  children: ReactNode;
  eyebrow?: string;
  title?: string;
}

export function Panel({ actions, children, eyebrow, title }: PanelProps) {
  return (
    <section className="panel">
      {title || actions || eyebrow ? (
        <header className="panel-header">
          <div>
            {eyebrow ? <span className="panel-eyebrow">{eyebrow}</span> : null}
            {title ? <h2>{title}</h2> : null}
          </div>
          {actions ? <div className="panel-actions">{actions}</div> : null}
        </header>
      ) : null}
      {children}
    </section>
  );
}
