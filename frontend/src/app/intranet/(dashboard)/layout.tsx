import HydrogenLayout from "@/layouts/hydrogen/layout";
import { RequireAuth } from "@/components/intranet/require-auth";

export default function IntranetDashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <HydrogenLayout>
      <RequireAuth>{children}</RequireAuth>
    </HydrogenLayout>
  );
}
