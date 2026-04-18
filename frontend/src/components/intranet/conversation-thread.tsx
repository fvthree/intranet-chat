"use client";

import Link from "next/link";
import { useCallback, useEffect, useLayoutEffect, useRef, useState } from "react";
import { intranetRoutes } from "@/config/intranet-routes";
import { INTRANET_SEED_USERS } from "@/config/intranet-seed-users";
import { useCurrentUser } from "@/components/intranet/current-user-context";
import { getConversation } from "@/lib/intranet-api/conversations";
import { listMessages, markConversationRead, sendMessage } from "@/lib/intranet-api/messages";
import type { ConversationResponse, MessageResponse } from "@/lib/intranet-api/types";
import { cn } from "@/utils/class-names";

const PAGE_SIZE = 50;
const MAX_CONTENT = 10_000;

function sortByTimeAsc(a: MessageResponse, b: MessageResponse): number {
  const ta = new Date(a.createdAt).getTime();
  const tb = new Date(b.createdAt).getTime();
  if (ta !== tb) return ta - tb;
  return a.id.localeCompare(b.id);
}

function senderLabel(senderId: string, myId: string | undefined): string {
  if (myId && senderId === myId) return "You";
  const seed = INTRANET_SEED_USERS.find((u) => u.id === senderId);
  if (seed) return seed.label;
  return `${senderId.slice(0, 8)}…`;
}

function threadTitle(conv: ConversationResponse | null): string {
  if (!conv) return "Conversation";
  if (conv.type === "CHANNEL") return conv.name?.trim() || "Channel";
  if (conv.type === "DIRECT") return "Direct message";
  return conv.name?.trim() || "Conversation";
}

function formatMsgTime(iso: string): string {
  try {
    return new Date(iso).toLocaleString(undefined, {
      month: "short",
      day: "numeric",
      hour: "numeric",
      minute: "2-digit",
    });
  } catch {
    return "";
  }
}

type Props = { conversationId: string };

export function ConversationThread({ conversationId }: Props) {
  const { user } = useCurrentUser();
  const [conv, setConv] = useState<ConversationResponse | null>(null);
  const [convError, setConvError] = useState<string | null>(null);

  const [messages, setMessages] = useState<MessageResponse[]>([]);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [loadingOlder, setLoadingOlder] = useState(false);
  const [nextOlderPage, setNextOlderPage] = useState<number | null>(null);
  const [hasOlder, setHasOlder] = useState(false);

  const [draft, setDraft] = useState("");
  const [sendError, setSendError] = useState<string | null>(null);
  const [sending, setSending] = useState(false);

  const scrollRef = useRef<HTMLDivElement>(null);
  const bottomRef = useRef<HTMLDivElement>(null);
  const shouldStickToBottom = useRef(true);

  const loadInitial = useCallback(async () => {
    setLoading(true);
    setLoadError(null);
    setMessages([]);
    setNextOlderPage(null);
    setHasOlder(false);
    try {
      const first = await listMessages(conversationId, { page: 0, size: PAGE_SIZE });
      const total = first.totalElements;
      if (total === 0) {
        setMessages([]);
        setHasOlder(false);
        return;
      }
      if (total <= PAGE_SIZE) {
        setMessages([...first.messages].sort(sortByTimeAsc));
        setHasOlder(false);
        shouldStickToBottom.current = true;
        return;
      }
      const lastPage = Math.floor((total - 1) / PAGE_SIZE);
      const last = await listMessages(conversationId, { page: lastPage, size: PAGE_SIZE });
      setMessages([...last.messages].sort(sortByTimeAsc));
      setNextOlderPage(lastPage > 0 ? lastPage - 1 : null);
      setHasOlder(lastPage > 0);
      shouldStickToBottom.current = true;
    } catch (e) {
      setLoadError(e instanceof Error ? e.message : "Failed to load messages");
    } finally {
      setLoading(false);
    }
  }, [conversationId]);

  useEffect(() => {
    void loadInitial();
  }, [loadInitial]);

  useEffect(() => {
    let cancelled = false;
    setConvError(null);
    void getConversation(conversationId)
      .then((c) => {
        if (!cancelled) setConv(c);
      })
      .catch((e: unknown) => {
        if (!cancelled) {
          setConvError(e instanceof Error ? e.message : "Could not load conversation");
        }
      });
    return () => {
      cancelled = true;
    };
  }, [conversationId]);

  useEffect(() => {
    if (loading || messages.length === 0) return;
    void markConversationRead(conversationId, {}).catch(() => {
      /* ignore mark-read failures */
    });
  }, [conversationId, loading, messages.length]);

  useLayoutEffect(() => {
    if (!shouldStickToBottom.current || !bottomRef.current) return;
    bottomRef.current.scrollIntoView({ block: "end" });
  }, [messages]);

  async function loadOlder() {
    if (nextOlderPage === null || loadingOlder) return;
    setLoadingOlder(true);
    const el = scrollRef.current;
    const prevHeight = el?.scrollHeight ?? 0;
    try {
      const page = await listMessages(conversationId, { page: nextOlderPage, size: PAGE_SIZE });
      setMessages((prev) => {
        const merged = [...page.messages, ...prev];
        const seen = new Set<string>();
        const deduped = merged.filter((m) => {
          if (seen.has(m.id)) return false;
          seen.add(m.id);
          return true;
        });
        return deduped.sort(sortByTimeAsc);
      });
      const loadedPage = nextOlderPage;
      const newNext = loadedPage > 0 ? loadedPage - 1 : null;
      setNextOlderPage(newNext);
      setHasOlder(newNext !== null);
      shouldStickToBottom.current = false;
      requestAnimationFrame(() => {
        if (!el) return;
        const nextHeight = el.scrollHeight;
        el.scrollTop = nextHeight - prevHeight;
      });
    } catch (e) {
      setLoadError(e instanceof Error ? e.message : "Failed to load older messages");
    } finally {
      setLoadingOlder(false);
    }
  }

  async function onSend(e: React.FormEvent) {
    e.preventDefault();
    setSendError(null);
    const text = draft.trim();
    if (!text) {
      setSendError("Message cannot be empty.");
      return;
    }
    if (text.length > MAX_CONTENT) {
      setSendError(`Message must be at most ${MAX_CONTENT} characters.`);
      return;
    }
    setSending(true);
    try {
      const created = await sendMessage(conversationId, { content: text });
      setDraft("");
      setMessages((prev) => {
        const next = [...prev, created];
        return next.sort(sortByTimeAsc);
      });
      shouldStickToBottom.current = true;
      void markConversationRead(conversationId, {}).catch(() => {});
    } catch (err) {
      setSendError(err instanceof Error ? err.message : "Send failed");
    } finally {
      setSending(false);
    }
  }

  return (
    <div className="flex min-h-[60vh] flex-col gap-3">
      <div className="flex flex-wrap items-start justify-between gap-2 border-b border-gray-100 pb-3">
        <div>
          <h2 className="text-xl font-semibold text-gray-900">{threadTitle(conv)}</h2>
          {convError ? (
            <p className="mt-1 text-sm text-amber-800">{convError}</p>
          ) : (
            <p className="mt-1 font-mono text-xs text-gray-500">{conversationId}</p>
          )}
        </div>
        <Link
          href={intranetRoutes.home}
          className="shrink-0 rounded-md border border-gray-300 px-3 py-1.5 text-sm text-gray-700 hover:bg-gray-50"
        >
          ← Conversations
        </Link>
      </div>

      <div
        ref={scrollRef}
        className="flex min-h-[280px] flex-1 flex-col overflow-y-auto rounded-lg border border-gray-200 bg-gray-50/80 p-3"
      >
        {loading ? (
          <p className="m-auto text-sm text-gray-500">Loading messages…</p>
        ) : loadError ? (
          <div className="m-auto text-center">
            <p className="text-sm text-red-800">{loadError}</p>
            <button
              type="button"
              onClick={() => void loadInitial()}
              className="mt-3 rounded-md border border-gray-300 bg-white px-3 py-1.5 text-sm"
            >
              Retry
            </button>
          </div>
        ) : (
          <>
            {hasOlder && nextOlderPage !== null ? (
              <div className="mb-3 flex justify-center">
                <button
                  type="button"
                  onClick={() => void loadOlder()}
                  disabled={loadingOlder}
                  className="rounded-full border border-gray-300 bg-white px-4 py-1.5 text-xs font-medium text-gray-700 hover:bg-gray-100 disabled:opacity-60"
                >
                  {loadingOlder ? "Loading…" : "Load older messages"}
                </button>
              </div>
            ) : null}
            {messages.length === 0 ? (
              <p className="m-auto text-center text-sm text-gray-500">No messages yet. Say hello below.</p>
            ) : (
              <ul className="space-y-3">
                {messages.map((m) => {
                  const mine = user?.id === m.senderId;
                  return (
                    <li
                      key={m.id}
                      className={cn(
                        "flex flex-col rounded-lg border px-3 py-2 text-sm shadow-sm",
                        mine
                          ? "ms-4 border-primary/30 bg-primary/10"
                          : "me-4 border-gray-200 bg-white",
                      )}
                    >
                      <div className="flex flex-wrap items-baseline justify-between gap-2 text-xs text-gray-500">
                        <span className="font-medium text-gray-700">
                          {senderLabel(m.senderId, user?.id)}
                        </span>
                        <time dateTime={m.createdAt}>{formatMsgTime(m.createdAt)}</time>
                      </div>
                      <p className="mt-1 whitespace-pre-wrap break-words text-gray-900">{m.content}</p>
                    </li>
                  );
                })}
              </ul>
            )}
            <div ref={bottomRef} />
          </>
        )}
      </div>

      <form onSubmit={(e) => void onSend(e)} className="rounded-lg border border-gray-200 bg-white p-3 shadow-sm">
        <label htmlFor="thread-composer" className="sr-only">
          Message
        </label>
        <textarea
          id="thread-composer"
          rows={3}
          maxLength={MAX_CONTENT}
          placeholder="Write a message…"
          className="w-full resize-y rounded-md border border-gray-300 px-3 py-2 text-sm outline-none focus:border-primary focus:ring-1 focus:ring-gray-400"
          value={draft}
          onChange={(e) => setDraft(e.target.value)}
          disabled={sending || loading}
        />
        <div className="mt-2 flex flex-wrap items-center justify-between gap-2">
          <span className="text-xs text-gray-400">
            {draft.trim().length}/{MAX_CONTENT}
          </span>
          <button
            type="submit"
            disabled={sending || loading}
            className="bg-primary hover:bg-primary-dark rounded-md px-4 py-2 text-sm font-medium text-primary-foreground disabled:opacity-60"
          >
            {sending ? "Sending…" : "Send"}
          </button>
        </div>
        {sendError ? (
          <p className="mt-2 text-sm text-red-800" role="alert">
            {sendError}
          </p>
        ) : null}
      </form>
    </div>
  );
}
