"use client";

import { Text } from "rizzui";
import { useCurrentUser } from "@/components/intranet/current-user-context";

/** Secondary line in the header: signed-in user from `GET /api/users/me`. */
export default function IntranetHeaderStrip() {
  const { user, loading, error } = useCurrentUser();

  if (loading) {
    return (
      <div className="hidden min-w-0 flex-1 px-2 md:block">
        <Text className="truncate text-sm text-gray-500">Loading profile…</Text>
      </div>
    );
  }

  if (error) {
    return (
      <div className="hidden min-w-0 flex-1 px-2 md:block">
        <Text className="truncate text-sm text-red-700" title={error}>
          {error}
        </Text>
      </div>
    );
  }

  if (!user) {
    return null;
  }

  return (
    <div className="hidden min-w-0 flex-1 px-2 lg:block">
      <p className="truncate text-sm text-gray-600">
        <span className="font-medium text-gray-900">{user.displayName}</span>
        <span className="text-gray-400"> · </span>
        <span className="text-gray-500">@{user.username}</span>
        {user.department ? (
          <>
            <span className="text-gray-400"> · </span>
            <span className="text-gray-500">{user.department}</span>
          </>
        ) : null}
      </p>
    </div>
  );
}
