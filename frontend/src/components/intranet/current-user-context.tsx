"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";
import type { UserMe } from "@/lib/intranet-api/types";
import { getCurrentUser } from "@/lib/intranet-api/users";
import { getAccessToken } from "@/lib/intranet-api/token";

type CurrentUserState = {
  user: UserMe | null;
  loading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
};

const CurrentUserContext = createContext<CurrentUserState | undefined>(undefined);

export function CurrentUserProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<UserMe | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const refetch = useCallback(async () => {
    if (!getAccessToken()) {
      setUser(null);
      setError(null);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const me = await getCurrentUser();
      setUser(me);
    } catch (e) {
      setUser(null);
      setError(e instanceof Error ? e.message : "Could not load profile");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void refetch();
  }, [refetch]);

  const value = useMemo(
    () => ({ user, loading, error, refetch }),
    [user, loading, error, refetch],
  );

  return (
    <CurrentUserContext.Provider value={value}>{children}</CurrentUserContext.Provider>
  );
}

export function useCurrentUser(): CurrentUserState {
  const ctx = useContext(CurrentUserContext);
  if (ctx === undefined) {
    throw new Error("useCurrentUser must be used within CurrentUserProvider");
  }
  return ctx;
}
