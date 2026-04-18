import { apiGetJson } from "@/lib/intranet-api/client";
import type { UserMe } from "@/lib/intranet-api/types";

/** `GET /api/users/me` — requires a bearer token. */
export function getCurrentUser(): Promise<UserMe> {
  return apiGetJson<UserMe>("/api/users/me");
}
