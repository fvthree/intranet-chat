"use client";

import { Text } from "rizzui";
import { useCurrentUser } from "@/components/intranet/current-user-context";

export default function IntranetSidebarProfile() {
  const { user, loading, error } = useCurrentUser();

  return (
    <div className="border-t border-gray-100 px-6 py-4 2xl:px-8">
      {loading && (
        <Text className="text-xs text-gray-500">Loading profile…</Text>
      )}
      {error && !loading && (
        <Text className="text-xs text-red-700">{error}</Text>
      )}
      {user && !loading && (
        <div className="min-w-0">
          <p className="truncate text-sm font-medium text-gray-900">{user.displayName}</p>
          <p className="truncate text-xs text-gray-500">@{user.username}</p>
        </div>
      )}
    </div>
  );
}
