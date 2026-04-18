"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { intranetRoutes } from "@/config/intranet-routes";

export default function ConversationThreadPlaceholderPage() {
  const params = useParams();
  const id = typeof params.conversationId === "string" ? params.conversationId : "";

  return (
    <div className="space-y-4">
      <div>
        <h2 className="text-xl font-semibold text-gray-900">Conversation</h2>
        <p className="mt-1 font-mono text-sm text-gray-500">{id || "—"}</p>
      </div>
      <p className="text-gray-600">
        Message thread (load messages, send, mark read) is implemented in{" "}
        <strong>Phase F6</strong>. For now, use the API or return to the list.
      </p>
      <Link href={intranetRoutes.home} className="text-sm text-primary underline">
        ← Back to conversations
      </Link>
    </div>
  );
}
