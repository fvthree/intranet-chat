import { apiGetJson, apiPostJson } from "@/lib/intranet-api/client";
import type { ConversationListItem, ConversationResponse } from "@/lib/intranet-api/types";

/** `GET /api/conversations` — ordered by `updatedAt` descending (server-side). */
export function listConversations(): Promise<ConversationListItem[]> {
  return apiGetJson<ConversationListItem[]>("/api/conversations");
}

/** Open or create a 1:1 conversation (idempotent for the same pair). */
export function createDirectConversation(body: {
  otherUserId: string;
}): Promise<ConversationResponse> {
  return apiPostJson<ConversationResponse, { otherUserId: string }>(
    "/api/conversations/direct",
    body,
  );
}

/** Create a channel; creator becomes the first member. */
export function createChannel(body: { name: string }): Promise<ConversationResponse> {
  return apiPostJson<ConversationResponse, { name: string }>(
    "/api/conversations/channels",
    body,
  );
}

/** `GET /api/conversations/{id}` — participant-only. */
export function getConversation(conversationId: string): Promise<ConversationResponse> {
  return apiGetJson<ConversationResponse>(`/api/conversations/${encodeURIComponent(conversationId)}`);
}
