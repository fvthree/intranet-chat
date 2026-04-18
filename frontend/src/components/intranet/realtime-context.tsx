"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
} from "react";
import { buildWebSocketUrl, getWebSocketOrigin } from "@/lib/intranet-api/env";
import { parseRealtimeEvent } from "@/lib/intranet-api/realtime-events";
import type { RealtimeServerEvent } from "@/lib/intranet-api/realtime-events";
import { getAccessToken } from "@/lib/intranet-api/token";

export type RealtimeConnectionStatus = "connecting" | "open" | "closed";

type Listener = (event: RealtimeServerEvent) => void;

export type RealtimeLastDisconnect = {
  code: number;
  reason: string;
};

type RealtimeContextValue = {
  status: RealtimeConnectionStatus;
  /** Last close event from the socket (for troubleshooting). */
  lastDisconnect: RealtimeLastDisconnect | null;
  /** Where the client connects (no token); should match your Spring server :8080. */
  socketUrl: string;
  subscribe: (listener: Listener) => () => void;
};

const RealtimeContext = createContext<RealtimeContextValue | null>(null);

const MAX_BACKOFF_MS = 30_000;

export function RealtimeProvider({ children }: { children: React.ReactNode }) {
  const [status, setStatus] = useState<RealtimeConnectionStatus>("connecting");
  const [lastDisconnect, setLastDisconnect] = useState<RealtimeLastDisconnect | null>(null);
  const [socketUrl, setSocketUrl] = useState(`${getWebSocketOrigin()}/ws`);
  const listenersRef = useRef(new Set<Listener>());
  const wsRef = useRef<WebSocket | null>(null);
  const reconnectTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const attemptRef = useRef(0);
  const stoppedRef = useRef(false);

  const subscribe = useCallback((listener: Listener) => {
    listenersRef.current.add(listener);
    return () => {
      listenersRef.current.delete(listener);
    };
  }, []);

  useEffect(() => {
    setSocketUrl(`${getWebSocketOrigin()}/ws`);
  }, []);

  useEffect(() => {
    stoppedRef.current = false;

    function notify(event: RealtimeServerEvent) {
      listenersRef.current.forEach((fn) => {
        try {
          fn(event);
        } catch {
          /* ignore subscriber errors */
        }
      });
    }

    function clearReconnectTimer() {
      if (reconnectTimerRef.current != null) {
        clearTimeout(reconnectTimerRef.current);
        reconnectTimerRef.current = null;
      }
    }

    function scheduleReconnect() {
      clearReconnectTimer();
      const attempt = attemptRef.current;
      const delay = Math.min(MAX_BACKOFF_MS, 1000 * Math.pow(2, attempt));
      reconnectTimerRef.current = setTimeout(() => {
        reconnectTimerRef.current = null;
        connect();
      }, delay);
    }

    function connect() {
      clearReconnectTimer();
      const token = getAccessToken();
      if (!token || stoppedRef.current) {
        setStatus("closed");
        setLastDisconnect({ code: 0, reason: "No access token in session" });
        return;
      }

      try {
        const url = buildWebSocketUrl(token);
        setStatus("connecting");
        const ws = new WebSocket(url);
        wsRef.current = ws;

        ws.onopen = () => {
          if (stoppedRef.current) return;
          attemptRef.current = 0;
          setLastDisconnect(null);
          setStatus("open");
        };

        ws.onmessage = (ev) => {
          if (typeof ev.data !== "string") return;
          const parsed = parseRealtimeEvent(ev.data);
          if (parsed) notify(parsed);
        };

        ws.onerror = () => {
          /* onclose handles reconnect */
        };

        ws.onclose = (ev) => {
          wsRef.current = null;
          if (stoppedRef.current) return;
          setLastDisconnect({
            code: ev.code,
            reason: ev.reason || "",
          });
          setStatus("closed");
          attemptRef.current += 1;
          if (getAccessToken()) {
            scheduleReconnect();
          }
        };
      } catch {
        setStatus("closed");
        attemptRef.current += 1;
        scheduleReconnect();
      }
    }

    connect();

    return () => {
      stoppedRef.current = true;
      clearReconnectTimer();
      attemptRef.current = 0;
      const w = wsRef.current;
      wsRef.current = null;
      if (w && (w.readyState === WebSocket.OPEN || w.readyState === WebSocket.CONNECTING)) {
        w.close();
      }
      setStatus("closed");
    };
  }, []);

  const value = useMemo(
    () => ({
      status,
      lastDisconnect,
      socketUrl,
      subscribe,
    }),
    [status, lastDisconnect, socketUrl, subscribe],
  );

  return <RealtimeContext.Provider value={value}>{children}</RealtimeContext.Provider>;
}

export function useRealtime(): RealtimeContextValue {
  const ctx = useContext(RealtimeContext);
  if (!ctx) {
    throw new Error("useRealtime must be used within RealtimeProvider");
  }
  return ctx;
}
