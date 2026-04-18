"use client";

import { useParams } from "next/navigation";
import { ConversationThread } from "@/components/intranet/conversation-thread";

export default function ConversationThreadPage() {
  const params = useParams();
  const id = typeof params.conversationId === "string" ? params.conversationId : "";

  if (!id) {
    return <p className="text-sm text-gray-500">Invalid conversation.</p>;
  }

  return <ConversationThread conversationId={id} />;
}
