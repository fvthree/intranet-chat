"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { Avatar, Button, Popover, Text, Title } from "rizzui";
import { intranetRoutes } from "@/config/intranet-routes";
import { useCurrentUser } from "@/components/intranet/current-user-context";
import { clearAccessToken } from "@/lib/intranet-api/token";
import cn from "@/utils/class-names";

function DropdownBody() {
  const router = useRouter();
  const { user, loading, error } = useCurrentUser();

  function signOut() {
    clearAccessToken();
    router.replace(intranetRoutes.login);
  }

  if (loading) {
    return (
      <div className="w-64 px-6 py-6 text-sm text-gray-500">Loading profile…</div>
    );
  }

  if (error || !user) {
    return (
      <div className="w-64 px-6 py-6 text-sm text-red-700">
        {error ?? "Not signed in"}
      </div>
    );
  }

  return (
    <div className="w-64 text-left rtl:text-right">
      <div className="flex items-center border-b border-gray-300 px-6 pb-5 pt-6">
        <Avatar name={user.displayName} className="shrink-0" />
        <div className="ms-3 min-w-0">
          <Title as="h6" className="truncate font-semibold">
            {user.displayName}
          </Title>
          <Text className="truncate text-gray-600">{user.email}</Text>
          <Text className="truncate text-xs text-gray-500">@{user.username}</Text>
        </div>
      </div>
      <div className="grid px-3.5 py-3.5 font-medium text-gray-700">
        <Link
          href={intranetRoutes.settings}
          className="group my-0.5 flex items-center rounded-md px-2.5 py-2 hover:bg-gray-100 focus:outline-none hover:dark:bg-gray-50/50"
        >
          Settings
        </Link>
        <Link
          href={intranetRoutes.dev}
          className="group my-0.5 flex items-center rounded-md px-2.5 py-2 hover:bg-gray-100 focus:outline-none hover:dark:bg-gray-50/50"
        >
          API dev tools
        </Link>
      </div>
      <div className="border-t border-gray-300 px-6 pb-6 pt-5">
        <Button
          type="button"
          className="h-auto w-full justify-start p-0 font-medium text-gray-700 outline-none focus-within:text-gray-600 hover:text-gray-900 focus-visible:ring-0"
          variant="text"
          onClick={signOut}
        >
          Sign out
        </Button>
      </div>
    </div>
  );
}

export default function IntranetProfileMenu() {
  const [isOpen, setIsOpen] = useState(false);
  const pathname = usePathname();
  const { user, loading } = useCurrentUser();

  useEffect(() => {
    setIsOpen(false);
  }, [pathname]);

  const label = loading ? "…" : user?.displayName ?? "?";

  return (
    <Popover
      isOpen={isOpen}
      setIsOpen={setIsOpen}
      shadow="sm"
      placement="bottom-end"
    >
      <Popover.Trigger>
        <button
          type="button"
          aria-label="Account menu"
          className={cn(
            "flex max-w-[12rem] shrink-0 items-center gap-2 rounded-full outline-none focus-visible:ring-[1.5px] focus-visible:ring-gray-400 focus-visible:ring-offset-2 active:translate-y-px sm:max-w-xs",
          )}
        >
          <Avatar
            name={user?.displayName ?? "User"}
            className="!h-9 w-9 shrink-0 sm:!h-10 sm:!w-10"
          />
          <span className="hidden min-w-0 truncate text-left text-sm font-medium text-gray-700 md:inline">
            {label}
          </span>
        </button>
      </Popover.Trigger>

      <Popover.Content className="z-[9999] p-0 dark:bg-gray-100 [&>svg]:dark:fill-gray-100">
        <DropdownBody />
      </Popover.Content>
    </Popover>
  );
}
