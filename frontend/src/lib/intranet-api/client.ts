import { getApiBaseUrl } from "@/lib/intranet-api/env";
import { clearAccessToken, getAccessToken } from "@/lib/intranet-api/token";
import { intranetRoutes } from "@/config/intranet-routes";

/**
 * Low-level fetch to the backend. Attaches `Authorization: Bearer` when a token exists
 * in {@link sessionStorage}.
 *
 * On **401** in the browser, clears the token and redirects to the login route (session expired).
 * Use {@link postJsonUnauthenticated} for `POST /api/auth/login` so failed logins do not trigger that redirect.
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

  let res: Response;
  try {
    res = await fetch(url, { ...init, headers });
  } catch (e) {
    throw mapNetworkError(e);
  }

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
    throw new Error(formatApiError(text, res.status));
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
    throw new Error(formatApiError(text, res.status));
  }
  return res.json() as Promise<T>;
}

/**
 * POST JSON without a bearer token and without 401→redirect (for `POST /api/auth/login`).
 */
export async function postJsonUnauthenticated<T, B = unknown>(
  path: string,
  body: B,
): Promise<T> {
  const base = getApiBaseUrl();
  const url = `${base}${path.startsWith("/") ? path : `/${path}`}`;
  let res: Response;
  try {
    res = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
  } catch (e) {
    throw mapNetworkError(e);
  }
  if (!res.ok) {
    const text = await res.text();
    throw new Error(formatApiError(text, res.status));
  }
  return res.json() as Promise<T>;
}

function mapNetworkError(e: unknown): Error {
  if (!(e instanceof TypeError)) {
    return e instanceof Error ? e : new Error(String(e));
  }
  const msg = e.message.toLowerCase();
  if (msg.includes("fetch") || msg.includes("network")) {
    return new Error(
      "Cannot reach the API (network error). Start the Spring backend (default port 8080), ensure Postgres/Redis if required, and restart `npm run dev`. If you set NEXT_PUBLIC_API_BASE_URL, it must match where the API listens.",
    );
  }
  return e;
}

function formatApiError(text: string, status: number): string {
  if (!text) {
    return `Request failed (${status})`;
  }
  try {
    const j = JSON.parse(text) as {
      message?: string;
      error?: string;
      detail?: string;
    };
    return j.detail ?? j.message ?? j.error ?? text;
  } catch {
    return text.length > 200 ? `${text.slice(0, 200)}…` : text;
  }
}
