"use client";

import { useEffect, useState } from "react";
import { getRemindercounts, ReminderCounts } from "@/lib/api";
import SmartListGrid from "./SmartListGrid";

export default function Sidebar() {
  const [counts, setCounts] = useState<ReminderCounts>({ today: 0, scheduled: 0, all: 0, flagged: 0 });

  useEffect(() => {
    getRemindercounts().then(setCounts).catch(() => {});
  }, []);

  return (
    <aside
      className="w-[260px] flex-shrink-0 h-screen sticky top-0 overflow-y-auto"
      style={{ backgroundColor: "#F2F2F7" }}
    >
      <SmartListGrid counts={counts} />

      {/* 나의 목록 — Phase 3에서 연동 */}
      <div className="mt-4 px-3">
        <p className="text-[11px] text-[#8E8E93] font-semibold mb-1 px-1">나의 목록</p>
      </div>
    </aside>
  );
}
