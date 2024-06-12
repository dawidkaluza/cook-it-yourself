import {cookies} from "next/headers";

function fetchApi(endpoint: string, method?: string, body?: any): Promise<any> {
  return new Promise((resolve, reject) => {
    let baseUrl;
    let headers = {
      "Accept": "application/json",
      "Content-Type": "application/json",
    };

    const isClient = typeof window !== 'undefined';
    if (isClient) {
      baseUrl = process.env.API_GATEWAY_CLIENT_URL;
    } else {
      baseUrl = process.env.API_GATEWAY_SERVER_URL;
      headers["Cookie"] = cookies().toString();
      console.log("Cookies as string: ", headers["Cookie"]);
    }

    fetch(
      baseUrl + endpoint,
      {
        method: method ?? "GET",
        body: body ? JSON.stringify(body) : undefined,
        headers,
      }
    ).then(response => {
      if (response.ok) {
        if (!response.body) {
          resolve(undefined);
        } else {
          response.json()
            .then(data => resolve(JSON.parse(data)));
        }
      } else {
        switch (response.status) {
          case 401: {
            console.error("Unauthorized");
            reject(response);
            break;
          }

          case 403: {
            console.error("Access denied");
            reject(response);
            break;
          }

          default: {
            reject(response);
            break;
          }
        }
      }
    }).catch(error => {
      reject(error);
    });
  });
}

export { fetchApi };