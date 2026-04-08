"use client";

import { Suspense } from "react";
import SmartListCard, { SmartCard } from "./SmartListCard";

export default function SmartListGrid({ counts }: { counts: { today: number; scheduled: number; all: number; flagged: number } }) {
  const cards: SmartCard[] = [
    { key: "today",     label: "오늘",  icon: "📅", color: "#007AFF", count: counts.today },
    { key: "scheduled", label: "예정",  icon: "🗓", color: "#FF3B30", count: counts.scheduled },
    { key: "all",       label: "전체",  icon: "≡",  color: "#000000", count: counts.all },
    { key: "flagged",   label: "깃발",  icon: "🚩", color: "#FF9500", count: counts.flagged },
  ];

  return (
    <Suspense>
      <div className="grid grid-cols-2 gap-2 px-3 pt-3">
        {cards.map((card) => (
          <SmartListCard key={card.key} card={card} />
        ))}
      </div>
    </Suspense>
  );
}
