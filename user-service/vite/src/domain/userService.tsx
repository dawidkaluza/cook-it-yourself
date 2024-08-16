import {settings} from "../settings/settings.ts";
import {fetchApi} from "../api/fetch.ts";
import {ApiError} from "../api/ApiError.ts";
import {RedirectResponse, SignInRequest, SignInResponse} from "./dtos/user.ts";
import {InvalidCredentialsError} from "./errors/user.tsx";

const authorize = async (redirectUrl: string): Promise<RedirectResponse> => {
  return await fetchApi({
    url: redirectUrl,
  });
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
    throw new InvalidCredentialsError("Invalid username or password.");
  }

  const requestData = new URLSearchParams();
  requestData.append("username", request.email);
  requestData.append("password", request.password);

  try {
    const body = await fetchApi<RedirectResponse>({
      url: "/sign-in",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: requestData.toString()
    });
    return handleRedirect(body.redirectUrl);
  } catch (error) {
    if (error instanceof ApiError) {
      const response = error.response;
      switch (response.status) {
        case 401:
        case 403: {
          throw new InvalidCredentialsError("Invalid username or password.");
        }
      }
    }

    throw error;
  }
};

export const userService = { signIn };