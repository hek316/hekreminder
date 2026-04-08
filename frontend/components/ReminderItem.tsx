"use client";

import { Reminder } from "@/types/reminder";

interface Props {
  reminder: Reminder;
  color?: string;
  onToggleComplete: (id: number) => void;
  onToggleFlag: (id: number) => void;
  onDelete: (id: number) => void;
}

const PRIORITY_LABEL: Record<string, { text: string; className: string }> = {
  HIGH:   { text: "!!!", className: "text-red-500" },
  MEDIUM: { text: "!!",  className: "text-orange-500" },
  LOW:    { text: "!",   className: "text-blue-500" },
};

function formatDueDate(iso: string): string {
  const date = new Date(iso);
  const today = new Date();
  const tomorrow = new Date();
  tomorrow.setDate(today.getDate() + 1);

  if (date.toDateString() === today.toDateString()) return "오늘";
  if (date.toDateString() === tomorrow.toDateString()) return "내일";

  return date.toLocaleDateString("ko-KR", { month: "long", day: "numeric" });
}

function isOverdue(iso: string): boolean {
  return new Date(iso) < new Date();
}

export default function ReminderItem({ reminder, color = "#007AFF", onToggleComplete, onToggleFlag, onDelete }: Props) {
  const priority = PRIORITY_LABEL[reminder.priority];

  return (
    <div
      className="flex items-start gap-3 px-4 py-2 group hover:bg-gray-50 rounded-lg"
      onContextMenu={(e) => {
        e.preventDefault();
        if (window.confirm(`"${reminder.title}"을(를) 삭제할까요?`)) {
          onDelete(reminder.id);
        }
      }}
    >
      {/* 원형 체크박스 */}
      <button
        onClick={() => onToggleComplete(reminder.id)}
        className="mt-0.5 flex-shrink-0 w-5 h-5 rounded-full border-2 flex items-center justify-center transition-all duration-300"
        style={{
          borderColor: color,
          backgroundColor: reminder.completed ? color : "transparent",
        }}
        aria-label="완료 토글"
      >
        {reminder.completed && (
          <svg className="w-3 h-3 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={3}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
          </svg>
        )}
      </button>

      {/* 우선순위 */}
      {priority && (
        <span className={`mt-0.5 text-xs font-bold flex-shrink-0 ${priority.className}`}>
          {priority.text}
        </span>
      )}

      {/* 제목 + 메모 */}
      <div className="flex-1 min-w-0">
        <p
          className="text-[15px] text-[#1C1C1E] leading-snug"
          style={{ textDecoration: reminder.completed ? "line-through" : "none", color: reminder.completed ? "#8E8E93" : "#1C1C1E" }}
        >
          {reminder.title}
        </p>
        {reminder.notes && (
          <p className="text-[13px] text-[#8E8E93] truncate">{reminder.notes}</p>
        )}
      </div>

      {/* 마감일 + 깃발 */}
      <div className="flex items-center gap-1.5 flex-shrink-0 mt-0.5">
        {reminder.dueDate && (
          <span className={`text-[13px] ${isOverdue(reminder.dueDate) && !reminder.completed ? "text-red-500" : "text-[#8E8E93]"}`}>
            {formatDueDate(reminder.dueDate)}
          </span>
        )}
        <button
          onClick={() => onToggleFlag(reminder.id)}
          className="opacity-0 group-hover:opacity-100 transition-opacity"
          aria-label="깃발 토글"
        >
          <span className={`text-sm ${reminder.flagged ? "text-orange-500" : "text-[#8E8E93]"}`}>🚩</span>
        </button>
      </div>
    </div>
  );
}
