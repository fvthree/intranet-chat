"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { intranetRoutes } from "@/config/intranet-routes";
import { login } from "@/lib/intranet-api/auth";
import { getAccessToken, setAccessToken } from "@/lib/intranet-api/token";

export function LoginForm() {
  const router = useRouter();
  const [username, setUsername] = useState("demo");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (getAccessToken()) {
      router.replace(intranetRoutes.home);
    }
  }, [router]);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const res = await login({ username: username.trim(), password });
      setAccessToken(res.accessToken);
      router.replace(intranetRoutes.home);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Sign in failed");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-semibold text-gray-900">Sign in</h2>
        <p className="mt-1 text-sm text-gray-600">
          Use your intranet account. Local seed users: <code className="text-xs">demo</code> /{" "}
          <code className="text-xs">alice</code> with password{" "}
          <code className="text-xs">password</code>.
        </p>
      </div>

      <form onSubmit={onSubmit} className="space-y-4">
        <div>
          <label htmlFor="username" className="mb-1 block text-sm font-medium text-gray-700">
            Username
          </label>
          <input
            id="username"
            name="username"
            autoComplete="username"
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm outline-none focus:border-primary focus:ring-1 focus:ring-gray-400"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>
        <div>
          <label htmlFor="password" className="mb-1 block text-sm font-medium text-gray-700">
            Password
          </label>
          <input
            id="password"
            name="password"
            type="password"
            autoComplete="current-password"
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm outline-none focus:border-primary focus:ring-1 focus:ring-gray-400"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        {error && (
          <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-800" role="alert">
            {error}
          </p>
        )}

        <button
          type="submit"
          disabled={loading}
          className="bg-primary hover:bg-primary-dark w-full rounded-md px-4 py-2 text-sm font-medium text-primary-foreground disabled:opacity-60"
        >
          {loading ? "Signing in…" : "Sign in"}
        </button>
      </form>

      <p className="text-sm text-gray-600">
        After signing in, use <strong>API dev tools</strong> from the intranet home to call{" "}
        <code className="rounded bg-gray-100 px-1">GET /api/users/me</code> or paste a token.
      </p>
    </div>
  );
}
