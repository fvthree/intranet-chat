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

function isLoopbackHostname(hostname: string): boolean {
  return hostname === "localhost" || hostname === "127.0.0.1" || hostname === "[::1]";
}

/**
 * Origin for WebSocket (`ws:` / `wss:`), no path.
 *
 * - Optional `NEXT_PUBLIC_WS_ORIGIN` — explicit override.
 * - If `NEXT_PUBLIC_API_BASE_URL` is set — derive `ws(s)://` + host/port from that URL.
 * - Else on **loopback** (`localhost` / `127.0.0.1` / `[::1]`) — `ws(s)://127.0.0.1:8080`
 *   (always IPv4: on Windows, `localhost` can resolve to `::1` while the JVM listens on IPv4 only,
 *   so the handshake never hits Spring and you see 1005 with no server logs).
 * - Else (e.g. LAN IP) — same host/port as the page so `/ws` can be proxied by Next.
 * - SSR fallback: `ws://127.0.0.1:8080`.
 */
export function getWebSocketOrigin(): string {
  const explicit = process.env.NEXT_PUBLIC_WS_ORIGIN?.trim();
  if (explicit) {
    return explicit.replace(/\/$/, "");
  }

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

  if (typeof window !== "undefined") {
    const scheme = window.location.protocol === "https:" ? "wss" : "ws";
    const host = window.location.hostname;
    if (isLoopbackHostname(host)) {
      return `${scheme}://127.0.0.1:8080`;
    }
    return `${scheme}://${window.location.host}`;
  }
  return "ws://127.0.0.1:8080";
}

/** Full `ws://host/ws?token=...` URL for the realtime connection. */
export function buildWebSocketUrl(accessToken: string): string {
  const base = getWebSocketOrigin();
  const q = new URLSearchParams({ token: accessToken });
  return `${base}/ws?${q.toString()}`;
}
