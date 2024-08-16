import {settings} from "../settings/settings.ts";

export type SignInRequest = {
  email: string;
  password: string;
  csrfToken: string;
};

export type SignInResponse = {
  redirectUrl: string;
  external: boolean;
};

export type ApiRedirectResponse = {
  redirectUrl?: string;
};

export class InvalidCredentialsError extends Error {
  constructor(message: string) {
    super(message);
    this.name = "InvalidCredentialsError";
  }
}

export class ApiError extends Error {
  private _response: Response;

  constructor(response: Response) {
    super("API error, http status " + response.status);
    this.name = "ApiError";
    this._response = response;
  }

  get response(): Response {
    return this._response;
  }

  set response(value: Response) {
    this._response = value;
  }
}

const authorize = async (redirectUrl: string): Promise<ApiRedirectResponse> => {
  const response = await fetch(redirectUrl, {
    headers: {
      "Accept": "application/json",
    },
    credentials: "include"
  });

  if (!response.ok) {
    throw new ApiError(response);
  }

  return await response.json();
};


const handleRedirect = async (redirectUrl?: string): Promise<SignInResponse> => {
  if (!redirectUrl) {
    return {
      redirectUrl: "",
      external: false,
    }
  }

  const appUrl = import.meta.env.BASE_URL;
  if (redirectUrl.startsWith(appUrl)) {
    return {
      redirectUrl,
      external: false,
    }
  }


  let userServiceUrl = settings.userServiceUrl;
  if (!userServiceUrl) {
    const publicPath = settings.publicPath;
    userServiceUrl = publicPath ? new URL(appUrl).href : appUrl;
  }

  if (redirectUrl.startsWith(userServiceUrl)) {
    const authResponse = await authorize(redirectUrl);
    return handleRedirect(authResponse.redirectUrl);
  }

  return {
    redirectUrl: redirectUrl,
    external: true,
  }
};

const signIn = async (request: SignInRequest) => {
  if (!request.email || !request.password) {
    throw new InvalidCredentialsError("Invalid username or password");
  }

  if (!request.csrfToken) {
    throw new Error("Missing CSRF token");
  }

  const requestData = new URLSearchParams();
  requestData.append("username", request.email);
  requestData.append("password", request.password);

  const response = await fetch(settings.userServiceUrl + "/sign-in", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
      "Accept": "application/json",
      "X-XSRF-TOKEN": request.csrfToken
    },
    body: requestData,
    credentials: "include",
  });

  if (!response.ok) {
    switch (response.status) {
      case 401:
      case 403: {
        throw new InvalidCredentialsError("Invalid username or password");
      }

      default: {
        throw new ApiError(response);
      }
    }
  }

  const body: ApiRedirectResponse = await response.json();
  return handleRedirect(body.redirectUrl);
};

export const userService = { signIn };