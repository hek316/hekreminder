"use client";

import { createContext, useCallback, useContext, useEffect, useState } from "react";
import { getReminderCounts, ReminderCounts } from "@/lib/api";

interface CountsContextValue {
  counts: ReminderCounts;
  refreshCounts: () => void;
}

const CountsContext = createContext<CountsContextValue>({
  counts: { today: 0, scheduled: 0, all: 0, flagged: 0 },
  refreshCounts: () => {},
});

export function CountsProvider({ children }: { children: React.ReactNode }) {
  const [counts, setCounts] = useState<ReminderCounts>({ today: 0, scheduled: 0, all: 0, flagged: 0 });

  const refreshCounts = useCallback(() => {
    getReminderCounts().then(setCounts).catch(() => {});
  }, []);

  useEffect(() => {
    refreshCounts();
  }, [refreshCounts]);

  return (
    <CountsContext.Provider value={{ counts, refreshCounts }}>
      {children}
    </CountsContext.Provider>
  );
}

export const useCounts = () => useContext(CountsContext);
export const useRefreshCounts = () => useContext(CountsContext).refreshCounts;
