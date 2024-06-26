import {redirect} from "next/navigation";
import {cookies} from "next/headers";

type ApiRequest = {
  endpoint: string;
  method?: string;
  body?: any;
  headers?: object;
  ignoreAuth?: boolean;
};

function fetchApi({ endpoint, method, body, headers, ignoreAuth } : ApiRequest): Promise<any> {
  return new Promise((resolve, reject) => {
    let baseUrl;

    const isClient = typeof window !== 'undefined';
    if (isClient) {
      baseUrl = process.env.NEXT_PUBLIC_API_GATEWAY_CLIENT_URL;
    } else {
      baseUrl = process.env.API_GATEWAY_SERVER_URL;
    }

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
              reject({
                unauthorized: true,
                response,
              });
            } else {
              redirect("/sign-in");
            }
            break;
          }

          case 403: {
            if (ignoreAuth) {
              reject({
                accessDenied: true,
                response,
              });
            } else {
              redirect("/sign-in");
            }
            break;
          }

          default: {
            reject({ response });
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
  return await fetchApi({
    ...request,
    headers: {
      ...headers,
      "Cookie": cookies().toString()
    }
  });
};

export async function fetchFromClient(request : ApiRequest) {
  return await fetchApi(request);
}
