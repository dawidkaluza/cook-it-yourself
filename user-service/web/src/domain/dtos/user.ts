
export type SignInRequest = {
  email: string;
  password: string;
};

export type SignInResponse = {
  redirectUrl: string;
  external: boolean;
};

export type SignUpRequest = {
  email: string;
  name: string;
  password: string;
};

export type SignUpResponse = {
  id: number;
  email: string;
  name: string;
};

export type RedirectResponse = {
  redirectUrl?: string;
};