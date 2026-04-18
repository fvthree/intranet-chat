import Link from "next/link";
import { intranetRoutes } from "@/config/intranet-routes";

export const metadata = {
  title: "Settings · Intranet chat",
};

export default function IntranetSettingsPage() {
  return (
    <div className="space-y-4">
      <h2 className="text-xl font-semibold text-gray-900">Settings</h2>
      <p className="text-gray-600">
        Placeholder for account and notification preferences. Profile details come from{" "}
        <code className="rounded bg-gray-100 px-1 text-sm">GET /api/users/me</code> (see header and
        sidebar).
      </p>
      <Link href={intranetRoutes.home} className="text-sm text-primary underline">
        ← Intranet home
      </Link>
    </div>
  );
}
