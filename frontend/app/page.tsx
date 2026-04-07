"use client";

import { useEffect, useState } from "react";
import { Reminder } from "@/types/reminder";
import { createReminder, deleteReminder, getReminders, toggleComplete, toggleFlag } from "@/lib/api";
import ReminderList from "@/components/ReminderList";
import AddReminderInput from "@/components/AddReminderInput";

export default function Home() {
  const [reminders, setReminders] = useState<Reminder[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getReminders()
      .then(setReminders)
      .finally(() => setLoading(false));
  }, []);

  async function handleAdd(title: string) {
    const created = await createReminder({ title });
    setReminders((prev) => [...prev, created]);
  }

  async function handleToggleComplete(id: number) {
    const updated = await toggleComplete(id);
    setReminders((prev) => prev.map((r) => (r.id === id ? updated : r)));
  }

  async function handleToggleFlag(id: number) {
    const updated = await toggleFlag(id);
    setReminders((prev) => prev.map((r) => (r.id === id ? updated : r)));
  }

  async function handleDelete(id: number) {
    await deleteReminder(id);
    setReminders((prev) => prev.filter((r) => r.id !== id));
  }

  const incomplete = reminders.filter((r) => !r.completed);
  const completed = reminders.filter((r) => r.completed);

  return (
    <main
      className="min-h-screen bg-white"
      style={{ fontFamily: "-apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif" }}
    >
      <div className="max-w-2xl mx-auto px-4 pt-12 pb-8">
        <h1 className="text-[28px] font-bold text-[#007AFF] mb-6">리마인더</h1>

        {loading ? (
          <p className="text-[#8E8E93] text-sm">불러오는 중...</p>
        ) : (
          <div className="bg-white rounded-xl border border-[#E5E5EA] overflow-hidden">
            <ReminderList
              reminders={incomplete}
              color="#007AFF"
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
    </main>
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
      <button
        onClick={() => setOpen(!open)}
        className="flex items-center gap-2 px-4 py-2 w-full text-left text-[#8E8E93] text-sm hover:bg-gray-50"
      >
        <span className={`transition-transform ${open ? "rotate-90" : ""}`}>▶</span>
        <span>완료됨 ({reminders.length})</span>
      </button>
      {open && (
        <ReminderList
          reminders={reminders}
          color="#8E8E93"
          onToggleComplete={onToggleComplete}
          onToggleFlag={onToggleFlag}
          onDelete={onDelete}
        />
      )}
    </div>
  );
}
