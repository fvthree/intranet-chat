/** `GET /api/users/me` response (Spring/Jackson camelCase). */
export type UserMe = {
  id: string;
  employeeId: string;
  username: string;
  displayName: string;
  email: string;
  department: string;
  role: string;
  active: boolean;
};
