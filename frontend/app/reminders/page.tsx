"use client";

import { use, useEffect, useState } from "react";
import { Reminder, ReminderFilter } from "@/types/reminder";
import { createReminder, deleteReminder, getReminders, toggleComplete, toggleFlag } from "@/lib/api";
import { useRefreshCounts } from "@/lib/countsContext";
import ReminderList from "@/components/ReminderList";
import AddReminderInput from "@/components/AddReminderInput";

const FILTER_LABEL: Record<string, { title: string; color: string }> = {
  today:     { title: "오늘",   color: "#007AFF" },
  scheduled: { title: "예정",   color: "#FF3B30" },
  all:       { title: "전체",   color: "#000000" },
  flagged:   { title: "깃발",   color: "#FF9500" },
};

export default function RemindersPage({ searchParams }: { searchParams: Promise<{ filter?: string }> }) {
  const { filter } = use(searchParams);
  const meta = FILTER_LABEL[filter ?? "all"] ?? FILTER_LABEL["all"];

  const refreshCounts = useRefreshCounts();
  const [reminders, setReminders] = useState<Reminder[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    getReminders(filter as ReminderFilter)
      .then(setReminders)
      .finally(() => setLoading(false));
  }, [filter]);

  async function handleAdd(title: string) {
    try {
      const created = await createReminder({ title });
      setReminders((prev) => [...prev, created]);
      refreshCounts();
    } catch (e) {
      alert((e as Error).message);
    }
  }

  async function handleToggleComplete(id: number) {
    try {
      const updated = await toggleComplete(id);
      setReminders((prev) => prev.map((r) => (r.id === id ? updated : r)));
      refreshCounts();
    } catch (e) {
      alert((e as Error).message);
    }
  }

  async function handleToggleFlag(id: number) {
    try {
      const updated = await toggleFlag(id);
      setReminders((prev) => prev.map((r) => (r.id === id ? updated : r)));
      refreshCounts();
    } catch (e) {
      alert((e as Error).message);
    }
  }

  async function handleDelete(id: number) {
    try {
      await deleteReminder(id);
      setReminders((prev) => prev.filter((r) => r.id !== id));
      refreshCounts();
    } catch (e) {
      alert((e as Error).message);
    }
  }

  const incomplete = reminders.filter((r) => !r.completed);
  const completed = reminders.filter((r) => r.completed);

  return (
    <div className="max-w-2xl mx-auto px-6 pt-10 pb-8">
      <h1 className="text-[28px] font-bold mb-6" style={{ color: meta.color }}>
        {meta.title}
      </h1>

      {loading ? (
        <p className="text-[#8E8E93] text-sm">불러오는 중...</p>
      ) : (
        <div className="bg-white rounded-xl border border-[#E5E5EA] overflow-hidden">
          <ReminderList
            reminders={incomplete}
            color={meta.color}
            onToggleComplete={handleToggleComplete}
            onToggleFlag={handleToggleFlag}
            onDelete={handleDelete}
          />
          <div className="border-t border-[#E5E5EA]">
            <AddReminderInput onAdd={handleAdd} />
          </div>
          {completed.length > 0 && (
            <CompletedSection
              reminders={completed}
              onToggleComplete={handleToggleComplete}
              onToggleFlag={handleToggleFlag}
              onDelete={handleDelete}
            />
          )}
        </div>
      )}
    </div>
  );
}

function CompletedSection({ reminders, onToggleComplete, onToggleFlag, onDelete }: {
  reminders: Reminder[];
  onToggleComplete: (id: number) => void;
  onToggleFlag: (id: number) => void;
  onDelete: (id: number) => void;
}) {
  const [open, setOpen] = useState(false);
  return (
    <div className="border-t border-[#E5E5EA]">
      <button onClick={() => setOpen(!open)} className="flex items-center gap-2 px-4 py-2 w-full text-left text-[#8E8E93] text-sm hover:bg-gray-50">
        <span className={`transition-transform ${open ? "rotate-90" : ""}`}>▶</span>
        <span>완료됨 ({reminders.length})</span>
      </button>
      {open && (
        <ReminderList reminders={reminders} color="#8E8E93" onToggleComplete={onToggleComplete} onToggleFlag={onToggleFlag} onDelete={onDelete} />
      )}
    </div>
  );
}
