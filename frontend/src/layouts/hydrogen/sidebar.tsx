"use client";

import { cn } from "@/utils/class-names";
import IntranetSidebarProfile from "@/components/intranet/intranet-sidebar-profile";

export default function Sidebar({
  className,
  intranetChrome,
}: {
  className?: string;
  intranetChrome?: boolean;
}) {
  return (
    <aside
      className={cn(
        "fixed bottom-0 start-0 z-50 flex h-full w-[270px] flex-col border-e-2 border-gray-100 bg-white 2xl:w-72 dark:bg-gray-100/50",
        className
      )}
    >
      <div className="min-h-0 flex-1" aria-hidden="true" />
      {intranetChrome ? <IntranetSidebarProfile /> : null}
    </aside>
  );
}
