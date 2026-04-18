import { redirect } from "next/navigation";

/** Canonical login is at `/` (root). */
export default function IntranetLoginRedirectPage() {
  redirect("/");
}
