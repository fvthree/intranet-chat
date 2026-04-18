import HydrogenLayout from "@/layouts/hydrogen/layout";
import { RequireAuth } from "@/components/intranet/require-auth";
import { CurrentUserProvider } from "@/components/intranet/current-user-context";

export default function IntranetDashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <CurrentUserProvider>
      <HydrogenLayout intranetChrome>
        <RequireAuth>{children}</RequireAuth>
      </HydrogenLayout>
    </CurrentUserProvider>
  );
}
