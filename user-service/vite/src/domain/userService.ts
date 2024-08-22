import {settings} from "../settings/settings.ts";
import {fetchApi} from "../api/fetch.ts";
import {ApiError} from "../api/ApiError.ts";
import {RedirectResponse, SignInRequest, SignInResponse, SignUpRequest, SignUpResponse} from "./dtos/user.ts";
import {InvalidCredentialsError, InvalidFieldsError} from "./errors/user.tsx";

const validate = (request: Record<string, any>) => {
  const errorFields: Record<string, string> = {};
  for (const key in request) {
    if (!request[key]) {
      errorFields[key] = "Field must not be empty.";
    }
  }
  return errorFields;
}

const isEmpty = (record: Record<string, string>) => {
  return !Object.keys(record).length;
}

const authorize = async (redirectUrl: string): Promise<RedirectResponse> => {
  return await fetchApi({
    endpoint: redirectUrl,
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

  let userServiceUrl = settings.userServiceUrl || new URL(appUrl).origin;
  if (redirectUrl.startsWith(userServiceUrl)) {
    const redirectEndpoint = redirectUrl.slice(userServiceUrl.length);
    const authResponse = await authorize(redirectEndpoint);
    return handleRedirect(authResponse.redirectUrl);
  }

  return {
    redirectUrl: redirectUrl,
    external: true,
  }
};

const signIn = async (request: SignInRequest) => {
  const errorFields = validate(request);
  if (!isEmpty(errorFields)) {
    throw new InvalidCredentialsError("Invalid email or password.");
  }

  const requestData = new URLSearchParams();
  requestData.append("username", request.email);
  requestData.append("password", request.password);

  try {
    const body = await fetchApi<RedirectResponse>({
      endpoint: "/sign-in",
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: requestData
    });
    return handleRedirect(body.redirectUrl);
  } catch (error) {
    if (error instanceof ApiError) {
      switch (error.status) {
        case 401:
        case 403: {
          throw new InvalidCredentialsError("Invalid email or password.");
        }
      }
    }

    throw error;
  }
};

const signUp = async (request: SignUpRequest) => {
  const errorFields = validate(request);
  if (!isEmpty(errorFields)) {
    throw new InvalidFieldsError(errorFields);
  }

  try {
    return await fetchApi<SignUpResponse>({
      endpoint: "/user/sign-up",
      method: "POST",
      body: JSON.stringify(request)
    });
  } catch(error) {
    if (error instanceof ApiError) {
      switch(error.status) {
        case 409: {
          throw new InvalidFieldsError({
            email: "Email already exists"
          });
        }

        case 422: {
          const errorFields: Record<string, string> = {};
          const response: { fields: Array<{ name: string, message: string}> } = error.body;
          for (const field of response.fields) {
            errorFields[field.name] = field.message;
          }

          if (!isEmpty(errorFields)) {
            throw new InvalidFieldsError(errorFields);
          }
        }
      }
    }

    throw error;
  }
};

export const userService = { signIn, signUp };