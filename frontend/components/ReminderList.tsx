"use client";

import { Reminder } from "@/types/reminder";
import ReminderItem from "./ReminderItem";

interface Props {
  reminders: Reminder[];
  color?: string;
  onToggleComplete: (id: number) => void;
  onToggleFlag: (id: number) => void;
  onDelete: (id: number) => void;
}

export default function ReminderList({ reminders, color, onToggleComplete, onToggleFlag, onDelete }: Props) {
  if (reminders.length === 0) {
    return <p className="text-[#8E8E93] text-sm px-4 py-6 text-center">리마인더가 없습니다</p>;
  }

  return (
    <div className="divide-y divide-[#E5E5EA]">
      {reminders.map((reminder) => (
        <ReminderItem
          key={reminder.id}
          reminder={reminder}
          color={color}
          onToggleComplete={onToggleComplete}
          onToggleFlag={onToggleFlag}
          onDelete={onDelete}
        />
      ))}
    </div>
  );
}
