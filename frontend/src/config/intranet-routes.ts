/** Routes for the intranet chat UI (Phase F1+). */
export const intranetRoutes = {
  home: "/intranet",
  /** Sign-in page is the app root (`/`). `/intranet/login` redirects there. */
  login: "/",
  dev: "/intranet/dev",
  settings: "/intranet/settings",
} as const;
