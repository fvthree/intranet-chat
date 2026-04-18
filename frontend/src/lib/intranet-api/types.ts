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

/** `POST /api/auth/login` response. */
export type LoginResponse = {
  accessToken: string;
  tokenType: string;
  expiresInSeconds: number;
};

/** Embedded in `GET /api/conversations` rows. */
export type LastMessagePreview = {
  messageId: string;
  contentPreview: string;
  senderId: string;
  createdAt: string;
};

/** One row from `GET /api/conversations` (Jackson camelCase). */
export type ConversationListItem = {
  id: string;
  type: string;
  name: string | null;
  createdBy: string;
  createdAt: string;
  updatedAt: string;
  unreadCount: number;
  lastMessage: LastMessagePreview | null;
};

/** `POST /api/conversations/direct` and `POST /api/conversations/channels` response. */
export type ConversationResponse = {
  id: string;
  type: string;
  name: string | null;
  createdBy: string;
  createdAt: string;
  updatedAt: string;
};
