import { apiGetJson, apiPostJson } from "@/lib/intranet-api/client";
import type { MarkReadResponse, MessagePageResponse, MessageResponse } from "@/lib/intranet-api/types";

const MAX_PAGE_SIZE = 200;

export function listMessages(
  conversationId: string,
  options: { page: number; size?: number },
): Promise<MessagePageResponse> {
  const size = Math.min(options.size ?? 50, MAX_PAGE_SIZE);
  const qs = new URLSearchParams({
    page: String(options.page),
    size: String(size),
  });
  return apiGetJson<MessagePageResponse>(
    `/api/conversations/${encodeURIComponent(conversationId)}/messages?${qs}`,
  );
}

export function sendMessage(
  conversationId: string,
  body: { content: string },
): Promise<MessageResponse> {
  return apiPostJson<MessageResponse, { content: string }>(
    `/api/conversations/${encodeURIComponent(conversationId)}/messages`,
    body,
  );
}

/** Mark read through latest message (`{}`) or a specific `messageId`. */
export function markConversationRead(
  conversationId: string,
  body: Record<string, unknown> = {},
): Promise<MarkReadResponse> {
  return apiPostJson<MarkReadResponse, Record<string, unknown>>(
    `/api/conversations/${encodeURIComponent(conversationId)}/read`,
    body,
  );
}
