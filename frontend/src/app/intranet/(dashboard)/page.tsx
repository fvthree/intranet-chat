"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { intranetRoutes } from "@/config/intranet-routes";
import { clearAccessToken } from "@/lib/intranet-api/token";

export default function IntranetHomePage() {
  const router = useRouter();

  function logout() {
    clearAccessToken();
    router.replace(intranetRoutes.login);
  }

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <h2 className="text-xl font-semibold text-gray-900">Intranet chat</h2>
        <button
          type="button"
          onClick={logout}
          className="rounded-md border border-gray-300 px-3 py-1.5 text-sm font-medium text-gray-700 hover:bg-gray-50"
        >
          Log out
        </button>
      </div>
      <p className="text-gray-600">
        Phase F3: your name and username in the header and sidebar come from{" "}
        <code className="rounded bg-gray-100 px-1 text-xs">GET /api/users/me</code>. Use the dev page
        to debug API calls; conversation UI is a later phase.
      </p>
      <ul className="list-inside list-disc space-y-2 text-sm">
        <li>
          <Link href={intranetRoutes.settings} className="text-primary underline">
            Settings
          </Link>{" "}
          (placeholder)
        </li>
        <li>
          <Link href={intranetRoutes.dev} className="text-primary underline">
            API dev tools
          </Link>
        </li>
        <li>
          <Link href={intranetRoutes.login} className="text-primary underline">
            Login page
          </Link>{" "}
          (sends you back here if you are already signed in)
        </li>
        <li>
          <Link href="/" className="text-primary underline">
            Template home
          </Link>
        </li>
      </ul>
    </div>
  );
}
