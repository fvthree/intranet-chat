import { getApiBaseUrl } from "@/lib/intranet-api/env";
import { clearAccessToken, getAccessToken } from "@/lib/intranet-api/token";
import { intranetRoutes } from "@/config/intranet-routes";

/**
 * Low-level fetch to the backend. Attaches `Authorization: Bearer` when a token exists
 * in {@link sessionStorage} (set manually in Phase F1 dev tools; login flow in Phase F2).
 *
 * On **401** in the browser, clears the token and redirects to the login placeholder route.
 */
export async function apiFetch(
  path: string,
  init: RequestInit = {},
): Promise<Response> {
  const base = getApiBaseUrl();
  const url = path.startsWith("http") ? path : `${base}${path.startsWith("/") ? "" : "/"}${path}`;

  const headers = new Headers(init.headers);
  const token = getAccessToken();
  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }
  if (
    init.body != null &&
    !headers.has("Content-Type") &&
    !(init.body instanceof FormData)
  ) {
    headers.set("Content-Type", "application/json");
  }

  const res = await fetch(url, { ...init, headers });

  if (res.status === 401 && typeof window !== "undefined") {
    clearAccessToken();
    window.location.assign(intranetRoutes.login);
  }

  return res;
}

export async function apiGetJson<T>(path: string): Promise<T> {
  const res = await apiFetch(path, { method: "GET" });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `HTTP ${res.status}`);
  }
  return res.json() as Promise<T>;
}

export async function apiPostJson<T, B = unknown>(
  path: string,
  body: B,
): Promise<T> {
  const res = await apiFetch(path, {
    method: "POST",
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `HTTP ${res.status}`);
  }
  return res.json() as Promise<T>;
}
