"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { intranetRoutes } from "@/config/intranet-routes";
import { ConversationList } from "@/components/intranet/conversation-list";
import { CreateConversationForms } from "@/components/intranet/create-conversation-forms";

export default function IntranetHomePage() {
  const router = useRouter();
  const [listRefresh, setListRefresh] = useState(0);

  function handleCreated(conversationId: string) {
    setListRefresh((n) => n + 1);
    router.push(intranetRoutes.conversation(conversationId));
  }

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <h2 className="text-xl font-semibold text-gray-900">Conversations</h2>
      </div>
      <p className="text-sm text-gray-600">
        From <code className="rounded bg-gray-100 px-1 text-xs">GET /api/conversations</code> — newest
        activity first. Open a row for the thread (message UI in Phase F6).
      </p>
      <CreateConversationForms onCreated={handleCreated} />
      <ConversationList refreshToken={listRefresh} />
      <footer className="flex flex-wrap gap-x-3 gap-y-1 border-t border-gray-100 pt-4 text-sm text-gray-500">
        <Link href={intranetRoutes.dev} className="text-primary underline">
          API dev tools
        </Link>
        <Link href={intranetRoutes.settings} className="text-primary underline">
          Settings
        </Link>
      </footer>
    </div>
  );
}
