import Link from "next/link";
import { intranetRoutes } from "@/config/intranet-routes";

export default function Home() {
  return (
    <div className="space-y-3">
      <h1 className="text-xl font-semibold text-gray-900">
        Isomorphic starter template
      </h1>
      <p className="text-gray-600">
        Intranet chat UI:{" "}
        <Link href={intranetRoutes.home} className="text-primary underline">
          /intranet
        </Link>
      </p>
    </div>
  );
}
