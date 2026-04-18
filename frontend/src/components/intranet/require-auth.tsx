"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { intranetRoutes } from "@/config/intranet-routes";
import { getAccessToken } from "@/lib/intranet-api/token";

type GateState = "checking" | "authed";

/**
 * Client-only guard: {@link sessionStorage} token is not available on the server.
 * Redirects to `/intranet/login` when unauthenticated.
 */
export function RequireAuth({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const [state, setState] = useState<GateState>("checking");

  useEffect(() => {
    if (!getAccessToken()) {
      router.replace(intranetRoutes.login);
      return;
    }
    setState("authed");
  }, [router]);

  if (state === "checking") {
    return (
      <div className="p-6 text-sm text-gray-500" aria-live="polite">
        Checking session…
      </div>
    );
  }

  return <>{children}</>;
}
