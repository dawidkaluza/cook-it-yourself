import axios from "axios";

const axiosInstance = axios.create({
  baseURL: process.env.USER_SERVICE_BASEURL,
  headers: {
    "Accept": "application/json",
    "Content-Type": "application/json"
  }
});

const buildSuccessResponse = (response) => {
  return {
    success: true,
    response
  };
};

const buildErrorResponse = ({ message = "", fields = {} }) => {
  return {
    success: false,
    response: { message, fields }
  };
};

const validateSignInFields = (fields, errors) => {
  let result = true;

  for (const key in fields) {
    if (!fields[key]) {
      errors[key] = errors[key] ?? "Field must not be empty.";
      result = false;
    }
  }

  return result;
};

const signIn = (fields) => {
  const errors = {};
  const validationResult = validateSignInFields(fields, errors);
  if (!validationResult) {
    return Promise.resolve(
      buildErrorResponse({ fields: errors })
    );
  }

  const { username, password, csrfToken } = fields;
  return axiosInstance
    .post(
      "/sign-in",
      `username=${username}&password=${password}`,
      {
        withCredentials: true,
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'X-XSRF-TOKEN': csrfToken
        }
      }
    ).then(response => {
      switch (response.status) {
        case 200: {
          const body = response.data;
          return buildSuccessResponse({
            redirectUrl: body.redirectUrl
          });
        }

        default: {
          return buildErrorResponse({ message: "Unable to process the request. Try again later."});
        }
      }
    }).catch(error => {
      if (error.response) {
        const response = error.response;
        switch (response.status) {
          case 401:
          case 403: {
            return buildErrorResponse({ message: "Invalid e-mail or password."});
          }

          default: {
            return buildErrorResponse({ message: "Unable to process the request. Try again later." });
          }
        }
      }

      return buildErrorResponse({ message: "Unable to process the request. Try again later." });
    });
};

const userService = {
  signIn
};

export { userService };