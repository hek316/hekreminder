import { Reminder, ReminderFilter, ReminderRequest } from "@/types/reminder";

const BASE_URL = "http://localhost:8080";

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  if (!res.ok) throw new Error(`API error ${res.status}: ${path}`);
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
