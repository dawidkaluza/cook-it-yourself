
export type SignInRequest = {
  email: string;
  password: string;
};

export type SignInResponse = {
  redirectUrl: string;
  external: boolean;
};

export type RedirectResponse = {
  redirectUrl?: string;
};