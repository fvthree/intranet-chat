import HydrogenLayout from "@/layouts/hydrogen/layout";
import { RequireAuth } from "@/components/intranet/require-auth";
import { CurrentUserProvider } from "@/components/intranet/current-user-context";
import { RealtimeProvider } from "@/components/intranet/realtime-context";

export default function IntranetDashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <CurrentUserProvider>
      <HydrogenLayout intranetChrome>
        <RequireAuth>
          <RealtimeProvider>{children}</RealtimeProvider>
        </RequireAuth>
      </HydrogenLayout>
    </CurrentUserProvider>
  );
}
