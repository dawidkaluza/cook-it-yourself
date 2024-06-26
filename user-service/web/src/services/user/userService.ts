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

const handleRedirect = (redirectUrlAsString) => {
  if (!redirectUrlAsString) {
    return buildSuccessResponse({
      redirectUrl: "",
      external: false
    });
  }

  const redirectUrl = new URL(redirectUrlAsString);
  const webAppBaseUrl = new URL(window.location.origin + process.env.PUBLIC_PATH);
  if (redirectUrl.href.startsWith(webAppBaseUrl.href)) {
    return buildSuccessResponse({
      redirectUrl: redirectUrl.pathname + redirectUrl.search,
      external: false
    });
  }

  const userServiceBaseUrl = new URL(process.env.USER_SERVICE_BASEURL || window.location.origin);
  if (redirectUrl.href.startsWith(userServiceBaseUrl.href)) {
    return authorize(redirectUrl.href)
      .then(result => {
        if (!result.success) {
          return result;
        }

        return handleRedirect(result.response.redirectUrl);
      });
  }


  return buildSuccessResponse({
    redirectUrl: redirectUrl.href,
    external: true
  });
}

const validateSignUpFields = (fields, errors) => {
  let result = true;

  for (const key in fields) {
    if (!fields[key]) {
      errors[key] = errors[key] ?? "Field must not be empty.";
      result = false;
    }
  }

  return result;
}

const signUp = (fields) => {
  const errors = {};
  const validationResult = validateSignUpFields(fields, errors);
  if (!validationResult) {
    return Promise.resolve(
      buildErrorResponse({ fields: errors })
    );
  }

  const { email, password, name } = fields;
  return axiosInstance.post(
    "/user/sign-up",
    { email, password, name }
  ).then(response => {
    if (response.status === 201) {
      const body = response.data;
      return buildSuccessResponse({
        id: body.id,
        email: body.email,
        name: body.name
      });
    }
  }).catch(error => {
    if (error.response) {
      const response = error.response;
      switch (response.status) {
        case 409: {
          return buildErrorResponse({
            fields: {
              email: "Email already exists"
            }
          });
        }

        case 422: {
          const errorFields = {};
          const respErrorFields = response.data.fields;
          for (const respErrorField of respErrorFields) {
            errorFields[respErrorField.name] = respErrorField.message;
          }

          return buildErrorResponse({ fields: errorFields });
        }
      }
    }

    return buildErrorResponse({ message: "Unable to process the request. Try again later." });
  });
}

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
  // TODO the whole validation could have been implemented better, it does not check if username is even there
  const validationResult = validateSignInFields(fields, errors);
  if (!validationResult) {
    return Promise.resolve(
      buildErrorResponse({ fields: errors })
    );
  }

  const { username, password, csrfToken } = fields;
  const requestData = new URLSearchParams();
  requestData.append("username", username);
  requestData.append("password", password);

  return axiosInstance
    .post(
      "/sign-in",
      requestData,
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
          return handleRedirect(body.redirectUrl);
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
        }
      }

      return buildErrorResponse({ message: "Unable to process the request. Try again later." });
    });
};

const authorize = (request) => {
  if (!request) {
    return buildErrorResponse({ message: "Authorization request is empty."})
  }

  return axiosInstance
    .get(
      request,
      {
        withCredentials: true
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
    }).catch(() => {
      return buildErrorResponse({message: "Unable to process the request. Try again later."});
    });
};

const validateConsentFields = (fields) => {
  const fieldsToValidate = { ...fields };
  delete fieldsToValidate.scopes;

  for (const key in fieldsToValidate) {
    if (!fields[key]) {
      return false;
    }
  }

  return true;
};

const consent = (fields) => {
  if (!validateConsentFields(fields)) {
    return Promise.resolve(
      buildErrorResponse({ message: "Unable to process the request. Try again later." })
    );
  }

  const { clientId, state, scopes } = fields;
  const requestData = new URLSearchParams();
  requestData.append("client_id", clientId);
  requestData.append("state", state);
  for (const scope of scopes) {
    requestData.append("scope", scope);
  }

  return axiosInstance
    .post(
      // TODO get url from config
      "/oauth2/authorize",
      requestData,
      {
        withCredentials: true,
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        }
      }
    ).then(response => {
      switch (response.status) {
        case 200: {
          const body = response.data;
          return buildSuccessResponse({
            redirectUrl: body.redirectUrl ?? ""
          });
        }

        default: {
          return buildErrorResponse({ message: "Unable to process the request. Try again later." });
        }
      }
    }).catch(() => {
      return buildErrorResponse({ message: "Unable to process the request. Try again later." });
    });
};

const userService = {
  signUp,
  signIn,
  authorize,
  consent,
};

export { userService };