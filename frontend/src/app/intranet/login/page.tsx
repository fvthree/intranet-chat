import Link from "next/link";
import { intranetRoutes } from "@/config/intranet-routes";

export default function IntranetLoginPlaceholderPage() {
  return (
    <div className="max-w-md space-y-4">
      <h2 className="text-xl font-semibold text-gray-900">Sign in</h2>
      <p className="text-gray-600">
        Login UI and <code className="rounded bg-gray-100 px-1">POST /api/auth/login</code>{" "}
        wiring will be added in <strong>Phase F2</strong>. For now, obtain a token from the
        backend (e.g. curl or Swagger) and paste it on the{" "}
        <Link href={intranetRoutes.dev} className="text-primary underline">
          API dev tools
        </Link>{" "}
        page.
      </p>
      <Link
        href={intranetRoutes.home}
        className="inline-block text-sm text-primary underline"
      >
        ← Back to intranet home
      </Link>
    </div>
  );
}
