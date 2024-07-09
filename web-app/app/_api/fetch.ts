import {redirect} from "next/navigation";
import {cookies} from "next/headers";

type ApiRequest = {
  endpoint: string;
  method?: string;
  body?: any;
  headers?: object;
  ignoreAuth?: boolean;
};

type InternalApiRequest = ApiRequest & { baseUrl: string };

export class ApiError extends Error {
  private _response : Response
  constructor(response : Response) {
    super("Client error " + response.status);
    this._response = response;
  }

  get response(): Response {
    return this._response;
  }

  set response(value: Response) {
    this._response = value;
  }
}

function fetchApi({ baseUrl, endpoint, method, body, headers, ignoreAuth } : InternalApiRequest): Promise<any> {
  return new Promise((resolve, reject) => {
    fetch(
      baseUrl + endpoint,
      {
        method: method ?? "GET",
        body: body ? JSON.stringify(body) : undefined,
        headers: {
          "Accept": "application/json",
          "Content-Type": "application/json",
          ...headers,
        },
      }
    ).then(response => {
      if (response.ok) {
        if (!response.body) {
          resolve(null);
        } else {
          response.json()
            .then(data => {
              resolve(data)
            });
        }
      } else {
        switch (response.status) {
          case 401: {
            if (ignoreAuth) {
              reject(new ApiError(response));
            } else {
              redirect("/sign-in");
            }
            break;
          }

          case 403: {
            if (ignoreAuth) {
              reject(new ApiError(response));
            } else {
              redirect("/sign-in");
            }
            break;
          }

          default: {
            reject(new ApiError(response) );
            break;
          }
        }
      }
    }).catch(error => {
      reject(error);
    });
  });
}

export async function fetchFromServer(request : ApiRequest) {
  const { headers } = request;
  const baseUrl = process.env.API_GATEWAY_SERVER_URL;
  if (!baseUrl) {
    throw new Error("API_GATEWAY_SERVER_URL env var not defined");
  }

  return await fetchApi({
    ...request,
    baseUrl,
    headers: {
      ...headers,
      "Cookie": cookies().toString()
    }
  });
}

export async function fetchFromClient(request : ApiRequest) {
  const baseUrl = process.env.NEXT_PUBLIC_API_GATEWAY_CLIENT_URL;
  if (!baseUrl) {
    throw new Error("NEXT_PUBLIC_API_GATEWAY_CLIENT_URL env var not defined");
  }

  return await fetchApi({
    ...request,
    baseUrl,
  });
}
