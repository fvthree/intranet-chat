"use client";

import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import { intranetRoutes } from "@/config/intranet-routes";
import { listConversations } from "@/lib/intranet-api/conversations";
import type { ConversationListItem } from "@/lib/intranet-api/types";
import { cn } from "@/utils/class-names";

function conversationTitle(row: ConversationListItem): string {
  if (row.type === "CHANNEL") {
    return row.name?.trim() || "Channel";
  }
  if (row.type === "DIRECT") {
    return "Direct message";
  }
  return row.name?.trim() || row.type;
}

function formatWhen(iso: string | undefined): string {
  if (!iso) return "";
  try {
    const d = new Date(iso);
    return d.toLocaleString(undefined, {
      month: "short",
      day: "numeric",
      hour: "numeric",
      minute: "2-digit",
    });
  } catch {
    return "";
  }
}

export function ConversationList() {
  const [items, setItems] = useState<ConversationListItem[] | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const list = await listConversations();
      setItems(list);
    } catch (e) {
      setItems(null);
      setError(e instanceof Error ? e.message : "Failed to load conversations");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  if (loading) {
    return (
      <div className="rounded-lg border border-gray-200 bg-white p-8 text-center text-sm text-gray-500">
        Loading conversations…
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-800">
        <p>{error}</p>
        <button
          type="button"
          onClick={() => void load()}
          className="mt-3 rounded-md border border-red-300 bg-white px-3 py-1.5 text-sm font-medium text-red-900 hover:bg-red-100"
        >
          Retry
        </button>
      </div>
    );
  }

  if (!items || items.length === 0) {
    return (
      <div className="rounded-lg border border-dashed border-gray-300 bg-gray-50/80 p-8 text-center text-sm text-gray-600">
        <p className="font-medium text-gray-800">No conversations yet</p>
        <p className="mt-2">
          Channels and DMs will show here once they exist. You can create new ones in a later phase
          from this app.
        </p>
      </div>
    );
  }

  return (
    <ul className="divide-y divide-gray-200 overflow-hidden rounded-lg border border-gray-200 bg-white shadow-sm">
      {items.map((row) => {
        const title = conversationTitle(row);
        const preview = row.lastMessage?.contentPreview;
        const when = formatWhen(row.lastMessage?.createdAt ?? row.updatedAt);
        const unread = row.unreadCount > 0;

        return (
          <li key={row.id}>
            <Link
              href={intranetRoutes.conversation(row.id)}
              className={cn(
                "flex gap-3 px-4 py-3 transition-colors hover:bg-gray-50 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary",
                unread && "bg-primary/5",
              )}
            >
              <div className="flex min-w-0 flex-1 flex-col gap-0.5">
                <div className="flex flex-wrap items-baseline justify-between gap-2">
                  <span className="truncate font-medium text-gray-900">{title}</span>
                  {when ? (
                    <span className="shrink-0 text-xs text-gray-500">{when}</span>
                  ) : null}
                </div>
                <div className="flex items-center gap-2">
                  <span className="text-xs font-medium uppercase tracking-wide text-gray-400">
                    {row.type === "CHANNEL" ? "Channel" : row.type === "DIRECT" ? "DM" : row.type}
                  </span>
                  {unread ? (
                    <span className="inline-flex min-h-[1.25rem] min-w-[1.25rem] items-center justify-center rounded-full bg-primary px-1.5 text-xs font-semibold text-primary-foreground">
                      {row.unreadCount > 99 ? "99+" : row.unreadCount}
                    </span>
                  ) : null}
                </div>
                {preview ? (
                  <p className="truncate text-sm text-gray-600">{preview}</p>
                ) : (
                  <p className="text-sm italic text-gray-400">No messages yet</p>
                )}
              </div>
            </Link>
          </li>
        );
      })}
    </ul>
  );
}
