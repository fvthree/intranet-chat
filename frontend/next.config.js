const withBundleAnalyzer = require("@next/bundle-analyzer")({
  enabled: process.env.ANALYZE === "true",
});

/** @type {import('next').NextConfig} */
const nextConfig = {
  /**
   * Proxy API calls through the Next dev server so the browser stays same-origin
   * (http://localhost:3000 → /api/...), avoiding CORS and fixing "Failed to fetch"
   * when the UI is opened via a host the backend does not allow (e.g. LAN IP).
   *
   * Target is server-side only; override with BACKEND_ORIGIN if the API is not on 8080.
   */
  async rewrites() {
    const backend = process.env.BACKEND_ORIGIN ?? "http://127.0.0.1:8080";
    return [
      { source: "/api/:path*", destination: `${backend}/api/:path*` },
      // WebSocket upgrade proxied to Spring so the browser can use same-origin ws://localhost:3000/ws
      // (avoids cross-port issues and close code 1005 in many dev setups).
      { source: "/ws", destination: `${backend.replace(/\/$/, "")}/ws` },
    ];
  },
};

module.exports = withBundleAnalyzer(nextConfig);
