import { redirect } from "next/navigation";

export default function Home() {
  redirect("/reminders?filter=all");
}
