/**
 * Base URL for the Spring backend (no trailing slash).
 *
 * - **Default (empty):** same-origin `/api/...` — Next.js rewrites proxy to the backend in dev
 *   (see `next.config.js`), so the browser does not call `:8080` directly and CORS is avoided.
 * - **Set explicitly** for production or when you want the browser to talk to the API host
 *   directly, e.g. `NEXT_PUBLIC_API_BASE_URL=https://api.example.com`.
 */
export function getApiBaseUrl(): string {
  const raw = process.env.NEXT_PUBLIC_API_BASE_URL?.trim();
  if (!raw) {
    return "";
  }
  return raw.replace(/\/$/, "");
}

/**
 * Origin for WebSocket (`ws:` / `wss:`), no path. Browsers cannot use the HTTP `/api` proxy for WS.
 *
 * When `NEXT_PUBLIC_API_BASE_URL` is unset, prefer `NEXT_PUBLIC_WS_ORIGIN`, otherwise (in the
 * browser) use the **same hostname as the page** on port 8080 — e.g. `localhost:3000` →
 * `ws://localhost:8080`. A fixed `ws://127.0.0.1:8080` breaks some setups where the UI is opened
 * as `localhost` and the socket must match.
 */
export function getWebSocketOrigin(): string {
  const api = process.env.NEXT_PUBLIC_API_BASE_URL?.trim();
  if (api) {
    try {
      const u = new URL(api);
      const scheme = u.protocol === "https:" ? "wss" : "ws";
      return `${scheme}://${u.host}`;
    } catch {
      /* fall through */
    }
  }
  const explicit = process.env.NEXT_PUBLIC_WS_ORIGIN?.trim();
  if (explicit) {
    return explicit.replace(/\/$/, "");
  }
  if (typeof window !== "undefined") {
    return `ws://${window.location.hostname}:8080`;
  }
  return "ws://127.0.0.1:8080";
}

/** Full `ws://host/ws?token=...` URL for the realtime connection. */
export function buildWebSocketUrl(accessToken: string): string {
  const base = getWebSocketOrigin();
  const q = new URLSearchParams({ token: accessToken });
  return `${base}/ws?${q.toString()}`;
}
