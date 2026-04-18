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
