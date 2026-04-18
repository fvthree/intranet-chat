import Link from "next/link";
import { intranetRoutes } from "@/config/intranet-routes";

export default function IntranetHomePage() {
  return (
    <div className="space-y-4">
      <h2 className="text-xl font-semibold text-gray-900">Intranet chat</h2>
      <p className="text-gray-600">
        Phase F1: API client and placeholder routes. Use the dev page to paste a JWT and
        call <code className="rounded bg-gray-100 px-1">GET /api/users/me</code>.
      </p>
      <ul className="list-inside list-disc space-y-2 text-sm">
        <li>
          <Link href={intranetRoutes.dev} className="text-primary underline">
            API dev tools
          </Link>
        </li>
        <li>
          <Link href={intranetRoutes.login} className="text-primary underline">
            Login (placeholder for Phase F2)
          </Link>
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
