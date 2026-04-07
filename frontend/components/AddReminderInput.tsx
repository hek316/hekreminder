"use client";

import { useState } from "react";

interface Props {
  onAdd: (title: string) => void;
}

export default function AddReminderInput({ onAdd }: Props) {
  const [active, setActive] = useState(false);
  const [value, setValue] = useState("");

  function handleKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
    if (e.key === "Enter" && value.trim()) {
      onAdd(value.trim());
      setValue("");
      setActive(false);
    }
    if (e.key === "Escape") {
      setValue("");
      setActive(false);
    }
  }

  if (!active) {
    return (
      <button
        onClick={() => setActive(true)}
        className="flex items-center gap-2 px-4 py-2 text-[#007AFF] text-[15px] w-full text-left hover:bg-gray-50 rounded-lg transition-colors"
      >
        <span className="text-xl font-light leading-none">+</span>
        <span>새로운 리마인더</span>
      </button>
    );
  }

  return (
    <div className="flex items-center gap-3 px-4 py-2">
      <div className="w-5 h-5 rounded-full border-2 border-[#007AFF] flex-shrink-0" />
      <input
        autoFocus
        type="text"
        value={value}
        onChange={(e) => setValue(e.target.value)}
        onKeyDown={handleKeyDown}
        onBlur={() => { setValue(""); setActive(false); }}
        placeholder="새로운 리마인더"
        className="flex-1 text-[15px] text-[#1C1C1E] outline-none bg-transparent placeholder-[#8E8E93]"
      />
    </div>
  );
}
