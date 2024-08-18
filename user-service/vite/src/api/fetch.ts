import {settings} from "../settings/settings.ts";
import {ApiError} from "./ApiError.ts";
import Cookies from "universal-cookie";

type ApiRequest = {
  endpoint: string,
  method?: string;
  body?: BodyInit;
  headers?: HeadersInit;
}

export const fetchApi = async <T>(request: ApiRequest): Promise<T> => {
  const apiHeaders: HeadersInit = {
    "Content-Type": "application/json",
    "Accept": "application/json"
  };
  const xsrfToken = new Cookies().get("XSRF-TOKEN");
  if (xsrfToken) {
    apiHeaders["X-XSRF-TOKEN"] = xsrfToken;
  }

  const response = await fetch(settings.userServiceUrl + request.endpoint, {
    method: request.method,
    body: request.body,
    credentials: settings.userServiceUrl ? "include" : "same-origin",
    headers: {
      ...apiHeaders,
      ...request.headers
    }
  });

  if (!response.ok) {
    throw new ApiError(response);
  }

  return await response.json();
};