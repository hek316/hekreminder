import type { Metadata } from "next";
import "./globals.css";
import Sidebar from "@/components/Sidebar";

export const metadata: Metadata = {
  title: "HekReminder",
  description: "Apple Reminder Web Version",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ko" className="h-full antialiased">
      <body
        className="min-h-full flex"
        style={{ fontFamily: "-apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif" }}
      >
        <Sidebar />
        <main className="flex-1 bg-white min-h-screen overflow-y-auto">
          {children}
        </main>
      </body>
    </html>
  );
}
