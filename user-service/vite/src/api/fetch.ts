import {settings} from "../settings/settings.ts";
import {ApiError} from "./ApiError.ts";
import Cookies from "universal-cookie";

type ApiRequest = {
  url: string,
  method?: string;
  body?: any;
  headers?: HeadersInit;
}

export const fetchApi = async <T>(request: ApiRequest): Promise<T> => {
  const fullUrl = request.url.startsWith("/") ? (settings.userServiceUrl + request.url) : request.url;
  const response = await fetch(fullUrl, {
    method: request.method,
    body: request.body ? JSON.stringify(request.body) : undefined,
    credentials: settings.userServiceUrl ? "include" : "same-origin",
    headers: {
      "Content-Type": "application/json",
      "Accept": "application/json",
      "X-XSRF-TOKEN": new Cookies().get("XSRF-TOKEN"),
      ...request.headers
    }
  });

  if (!response.ok) {
    throw new ApiError(response);
  }

  return await response.json();
};