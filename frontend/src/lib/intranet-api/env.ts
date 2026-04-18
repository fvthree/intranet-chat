/**
 * Base URL for the Spring backend (no trailing slash).
 * Set `NEXT_PUBLIC_API_BASE_URL` in `.env.local`, e.g. `http://localhost:8080`.
 */
export function getApiBaseUrl(): string {
  const raw = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
  return raw.replace(/\/$/, "");
}
