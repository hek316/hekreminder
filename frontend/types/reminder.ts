export type Priority = "NONE" | "LOW" | "MEDIUM" | "HIGH";

export interface Reminder {
  id: number;
  title: string;
  notes: string | null;
  dueDate: string | null; // ISO-8601
  completed: boolean;
  priority: Priority;
  flagged: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ReminderRequest {
  title: string;
  notes?: string | null;
  dueDate?: string | null;
  priority?: Priority;
  flagged?: boolean;
}

export type ReminderFilter = "today" | "scheduled" | "flagged" | "completed";
