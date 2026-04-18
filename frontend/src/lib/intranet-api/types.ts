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

/** `GET /api/conversations/{id}/messages` page. */
export type MessagePageResponse = {
  messages: MessageResponse[];
  page: number;
  size: number;
  totalElements: number;
  hasNext: boolean;
};

/** One message from list/send responses. */
export type MessageResponse = {
  id: string;
  conversationId: string;
  senderId: string;
  content: string;
  createdAt: string;
};

/** `POST /api/conversations/{id}/read` response. */
export type MarkReadResponse = {
  lastReadMessageId: string | null;
  lastReadAt: string | null;
};
