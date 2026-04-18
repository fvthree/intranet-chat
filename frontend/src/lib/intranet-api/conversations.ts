import { apiGetJson } from "@/lib/intranet-api/client";
import type { ConversationListItem } from "@/lib/intranet-api/types";

/** `GET /api/conversations` — ordered by `updatedAt` descending (server-side). */
export function listConversations(): Promise<ConversationListItem[]> {
  return apiGetJson<ConversationListItem[]>("/api/conversations");
}
