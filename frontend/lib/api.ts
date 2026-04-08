import { Reminder, ReminderFilter, ReminderRequest } from "@/types/reminder";

const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  if (!res.ok) {
    let message = `API error ${res.status}: ${path}`;
    try {
      const body = await res.json();
      if (body?.message) message = body.message;
      else if (body?.errors?.[0]?.message) message = body.errors[0].message;
    } catch { /* json 파싱 실패 시 기본 메시지 사용 */ }
    throw new Error(message);
  }
  if (res.status === 204) return undefined as T;
  return res.json();
}

export function getReminders(filter?: ReminderFilter): Promise<Reminder[]> {
  const query = filter ? `?filter=${filter}` : "";
  return request(`/api/reminders${query}`);
}

export function createReminder(data: ReminderRequest): Promise<Reminder> {
  return request("/api/reminders", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export function updateReminder(id: number, data: ReminderRequest): Promise<Reminder> {
  return request(`/api/reminders/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export function toggleComplete(id: number): Promise<Reminder> {
  return request(`/api/reminders/${id}/complete`, { method: "PATCH" });
}

export function toggleFlag(id: number): Promise<Reminder> {
  return request(`/api/reminders/${id}/flag`, { method: "PATCH" });
}

export function deleteReminder(id: number): Promise<void> {
  return request(`/api/reminders/${id}`, { method: "DELETE" });
}

export interface ReminderCounts {
  today: number;
  scheduled: number;
  all: number;
  flagged: number;
}

export function getReminderCounts(): Promise<ReminderCounts> {
  return request("/api/reminders/counts");
}
