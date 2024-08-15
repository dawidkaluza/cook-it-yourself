
type SignInRequest = {
  email: string;
  password: string;
  csrfToken: string;
};

type SignInApiResponse = {
  redirectUrl?: string;
};

type SignInResponse = {
  redirectUrl: string;
  external: boolean;
};

class InvalidCredentialsError extends Error {
  constructor(message: string) {
    super(message);
    this.name = "InvalidCredentialsError";
  }
}

const authorize = (redirectUrl: string): Promise<SignInApiResponse> => {
  // TODO implement
  throw new Error("Not implemented.");
};


// TODO should return type be wrapped as a Promise when its async already?
const handleRedirect = async (redirectUrl?: string): Promise<SignInResponse> => {
  if (!redirectUrl) {
    return Promise.resolve({
      redirectUrl: "",
      external: false,
    });
  }

  const appUrl = import.meta.env.BASE_URL;
  if (redirectUrl.startsWith(appUrl)) {
    return Promise.resolve({
      redirectUrl,
      external: false,
    });
  }


  let userServiceUrl: string | undefined = import.meta.env.VITE_USER_SERVICE_URL;
  if (!userServiceUrl) {
    const publicPath: string | undefined = import.meta.env.VITE_PUBLIC_PATH;
    if (publicPath) {
      userServiceUrl = appUrl.slice(0, appUrl.length - publicPath.length); // TODO this might be failing then public path has slash at the begiining and/or and
    } else {
      userServiceUrl = appUrl;
    }
  }

  if (redirectUrl.startsWith(userServiceUrl)) {
    const authResponse = await authorize(redirectUrl);
    return handleRedirect(authResponse.redirectUrl);
  }

  return Promise.resolve({
    redirectUrl: redirectUrl,
    external: true,
  });
};

export const signIn = async (request: SignInRequest) => {
  if (!request.email || !request.password) {
    throw new InvalidCredentialsError("Invalid username or password");
  }

  if (!request.csrfToken) {
    throw new Error("Missing CSRF token");
  }

  const requestData = new URLSearchParams();
  requestData.append("username", request.email);
  requestData.append("password", request.password);

  const userServiceUrl = import.meta.env.VITE_USER_SERVICE_URL ?? "";
  const response = await fetch(userServiceUrl + "/sign-in", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
      "X-XSRF-TOKEN": request.csrfToken
    },
    body: requestData,
  });

  if (!response.ok) {
    switch (response.status) {
      case 401:
      case 403: {
        throw new InvalidCredentialsError("Invalid username or password");
      }

      default: {
        throw new Error("Unexpected error");
      }
    }
  }

  const body: SignInApiResponse = await response.json();
  return handleRedirect(body.redirectUrl);
};