"use client";

import { useCallback, useState } from "react";
import Link from "next/link";
import { Button, Text, Title } from "rizzui";
import { intranetRoutes } from "@/config/intranet-routes";
import { apiGetJson } from "@/lib/intranet-api/client";
import { setAccessToken, getAccessToken, clearAccessToken } from "@/lib/intranet-api/token";
import { getApiBaseUrl } from "@/lib/intranet-api/env";
import type { UserMe } from "@/lib/intranet-api/types";

export default function IntranetApiDevPage() {
  const [tokenInput, setTokenInput] = useState("");
  const [result, setResult] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const loadTokenFromStorage = useCallback(() => {
    setTokenInput(getAccessToken() ?? "");
  }, []);

  const saveToken = useCallback(() => {
    const t = tokenInput.trim();
    if (!t) {
      clearAccessToken();
      setResult("Token cleared from session storage.");
      return;
    }
    setAccessToken(t);
    setResult("Token saved to session storage (session only).");
  }, [tokenInput]);

  const fetchMe = useCallback(async () => {
    setError(null);
    setResult(null);
    setLoading(true);
    try {
      const me = await apiGetJson<UserMe>("/api/users/me");
      setResult(JSON.stringify(me, null, 2));
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e));
    } finally {
      setLoading(false);
    }
  }, []);

  return (
    <div className="mx-auto max-w-2xl space-y-6">
      <div>
        <Title as="h2" className="text-xl">
          API dev tools
        </Title>
        <Text className="mt-2 text-gray-600">
          Backend base URL:{" "}
          <code className="rounded bg-gray-100 px-1">
            {getApiBaseUrl() || "same-origin /api (Next proxies to backend)"}
          </code>{" "}
          — set <code className="rounded bg-gray-100 px-1">NEXT_PUBLIC_API_BASE_URL</code> only if
          the browser must call the API host directly.
        </Text>
      </div>

      <div className="space-y-2">
        <Text className="font-medium">Access token (JWT)</Text>
        <textarea
          className="focus:border-primary w-full rounded-md border border-gray-300 p-2 text-sm outline-none focus:ring-1 focus:ring-gray-400"
          rows={4}
          value={tokenInput}
          onChange={(e) => setTokenInput(e.target.value)}
          placeholder="Paste accessToken from POST /api/auth/login"
        />
        <div className="flex flex-wrap gap-2">
          <Button size="sm" variant="outline" onClick={loadTokenFromStorage}>
            Load from storage
          </Button>
          <Button size="sm" onClick={saveToken}>
            Save to session storage
          </Button>
          <Button size="sm" variant="flat" onClick={fetchMe} disabled={loading}>
            {loading ? "Loading…" : "GET /api/users/me"}
          </Button>
        </div>
      </div>

      {error && (
        <pre className="overflow-auto rounded-md bg-red-50 p-3 text-sm text-red-800">
          {error}
        </pre>
      )}
      {result && (
        <pre className="overflow-auto rounded-md bg-gray-50 p-3 text-sm">{result}</pre>
      )}

      <Link href={intranetRoutes.home} className="text-sm text-primary underline">
        ← Intranet home
      </Link>
    </div>
  );
}
