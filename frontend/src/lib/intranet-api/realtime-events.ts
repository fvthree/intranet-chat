import type { MessageResponse } from "@/lib/intranet-api/types";

export type RealtimeMessageNewEvent = {
  type: "MESSAGE_NEW";
  conversationId: string;
  message: MessageResponse;
};

export type RealtimePresenceEvent = {
  type: "PRESENCE";
  userId: string;
  status: string;
};

export type RealtimeServerEvent = RealtimeMessageNewEvent | RealtimePresenceEvent;

export function parseRealtimeEvent(raw: string): RealtimeServerEvent | null {
  try {
    const j = JSON.parse(raw) as { type?: string };
    if (j.type === "MESSAGE_NEW") {
      const e = j as RealtimeMessageNewEvent;
      const mid = e.message?.id;
      const conv = e.conversationId;
      if (conv && mid != null && String(mid).length > 0) return e;
    }
    if (j.type === "PRESENCE") {
      const e = j as RealtimePresenceEvent;
      if (e.userId && e.status) return e;
    }
  } catch {
    /* ignore non-JSON */
  }
  return null;
}
