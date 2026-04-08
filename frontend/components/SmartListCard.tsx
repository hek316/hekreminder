"use client";

import { useRouter, useSearchParams } from "next/navigation";

export interface SmartCard {
  key: string;
  label: string;
  icon: string;
  color: string;
  count: number;
}

export default function SmartListCard({ card }: { card: SmartCard }) {
  const router = useRouter();
  const searchParams = useSearchParams();
  const active = searchParams.get("filter") === card.key;

  function handleClick() {
    router.push(`/reminders?filter=${card.key}`);
  }

  return (
    <button
      onClick={handleClick}
      className="rounded-xl p-3 flex flex-col justify-between h-20 w-full text-left transition-all duration-150"
      style={{
        backgroundColor: card.color,
        filter: active ? "brightness(0.92)" : undefined,
        transform: active ? "scale(0.97)" : undefined,
      }}
      onMouseEnter={(e) => { if (!active) (e.currentTarget as HTMLElement).style.filter = "brightness(1.08)"; }}
      onMouseLeave={(e) => { (e.currentTarget as HTMLElement).style.filter = active ? "brightness(0.92)" : ""; }}
    >
      <span className="text-[28px] font-bold text-white leading-none">{card.count}</span>
      <div className="flex items-center gap-1">
        <span className="text-base">{card.icon}</span>
        <span className="text-[13px] text-white font-medium">{card.label}</span>
      </div>
    </button>
  );
}
