import { LoginForm } from "@/app/intranet/login/login-form";

export const metadata = {
  title: "Sign in — Intranet chat",
};

export default function Home() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50 px-4 py-12">
      <div className="w-full max-w-md rounded-lg border border-gray-200 bg-white p-8 shadow-sm">
        <LoginForm />
      </div>
    </div>
  );
}
