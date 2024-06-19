function fetchApi(endpoint: string, method?: string, body?: any, headers?: object): Promise<any> {
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
            .then(data => resolve(JSON.parse(data)));
        }
      } else {
        switch (response.status) {
          case 401: {
            console.error("Unauthorized");
            reject({
              unauthorized: true,
              response,
            });
            break;
          }

          case 403: {
            console.error("Access denied");
            reject({
              accessDenied: true,
              response,
            });
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

export { fetchApi };