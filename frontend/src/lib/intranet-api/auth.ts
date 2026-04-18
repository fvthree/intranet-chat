import { postJsonUnauthenticated } from "@/lib/intranet-api/client";
import type { LoginResponse } from "@/lib/intranet-api/types";

export type LoginRequestBody = {
  username: string;
  password: string;
};

export async function login(body: LoginRequestBody): Promise<LoginResponse> {
  return postJsonUnauthenticated<LoginResponse, LoginRequestBody>(
    "/api/auth/login",
    body,
  );
}
