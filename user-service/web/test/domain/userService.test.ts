import {ApiRequest, fetchApi} from "../../src/api/fetch.ts";
import {afterEach, expect, Mock} from "vitest";
import {ApiError} from "../../src/api/ApiError.ts";
import {userService} from "../../src/domain/userService.ts";
import {settings} from "../../src/settings/settings.ts";
import {InvalidCredentialsError, InvalidFieldsError} from "../../src/domain/errors/user.tsx";
import {SignUpRequest} from "../../src/domain/dtos/user.ts";

vi.mock("../../src/api/fetch.ts", () => {
  return {
    fetchApi: vi.fn(),
  };
});

afterEach(() => {
  vi.unstubAllEnvs();
})

describe("signIn function", () => {
  test.each([
    [ "", "", 401 ],
    [ "dawid", "validpwd", 401 ],
    [ "dawid@gmail.com", "invalidpwd", 403 ],
    [ "invalid@gmail.com", "invalidpwd", 403 ],
  ])("signIn with invalid credentials (email=%s, password=%s), throw error", async (email, password, statusCode) => {
    // Given
    (fetchApi as Mock).mockImplementation(async () => {
      throw new ApiError(statusCode, {});
    });
    
    // When, then
    await expect(() => userService.signIn({ email, password }))
      .rejects.toThrowError("Invalid email or password.");
  });

  test.each([
    [
      new ApiError(
        500,
        {}
      ),
    ],
    [
      new Error("Network unavailable")
    ],
  ])("signIn throws unexpected error, throw it further", async (error) => {
    // Given
    (fetchApi as Mock).mockImplementation(async () => {
      throw error;
    });

    // When
    let caughtError;
    try {
      await userService.signIn({
        email: "dawid@mail.com",
        password: "passwd"
      });
    } catch (error) {
      caughtError = error;
    }

    // Then
    expect(caughtError).instanceOf(Error);
    expect(caughtError).not.instanceOf(InvalidCredentialsError);
    expect((caughtError as Error).message).not.match(/Invalid email or password/i);
  });

  test.each([
    {
      userServiceUrl: "http://user-service",
      redirectUrl: "",
      authorizeRedirectUrl: "",
      expectedResponse: {
        redirectUrl: "",
        external: false,
      }
    },
    {
      userServiceUrl: "http://user-service",
      redirectUrl: "http://user-service/web/consent",
      authorizeRedirectUrl: "",
      expectedResponse: {
        redirectUrl: "http://user-service/web/consent",
        external: false,
      }
    },
    {
      userServiceUrl: "http://user-service",
      redirectUrl: "http://user-service/oauth2/authorize",
      authorizeRedirectUrl: "http://api-gateway/oauth2?code=123xyz",
      expectedResponse: {
        redirectUrl: "http://api-gateway/oauth2?code=123xyz",
        external: true,
      }
    },
    {
      userServiceUrl: "",
      redirectUrl: "http://user-service/oauth2/authorize",
      authorizeRedirectUrl: "http://api-gateway/oauth2?code=123xyz",
      expectedResponse: {
        redirectUrl: "http://api-gateway/oauth2?code=123xyz",
        external: true,
      }
    },
  ])(
    "signIn succeeds with userServiceUrl=$userServiceUrl, " +
    "redirectUrl=$redirectUrl, authorizeRedirectUrl=$authorizeRedirectUrl, return $expectedResponse",
    async (
      {userServiceUrl, redirectUrl, authorizeRedirectUrl, expectedResponse}
    ) => {
      // Given
      vi.stubEnv("BASE_URL", "http://user-service/web")
      vi.spyOn(settings, "userServiceUrl", "get").mockReturnValue(userServiceUrl);

      (fetchApi as Mock).mockImplementation(async (request: ApiRequest) => {
        switch (request.endpoint) {
          case "/sign-in": {
            return {redirectUrl}
          }

          default: {
            return {redirectUrl: authorizeRedirectUrl}
          }
        }
      });

      // When
      const response = await userService.signIn({
        email: "dawid@mail.com",
        password: "passwd",
      });

      // Then
      expect(response).toEqual(expectedResponse);
    }
  );
});

describe("signUp function", () => {
  test.each([
    {
      email: "",
      name: "",
      password: "",
      status: 200,
      body: {},
      expectedErrorFields: [ "email", "name", "password" ],
    },
    {
      email: "dawid@mail.com",
      name: "Dawid",
      password: "passwd",
      status: 409,
      body: {
        error: "E-mail already exists"
      },
      expectedErrorFields: [ "email" ],
    },
    {
      email: "dawid@mail.com",
      name: "D",
      password: "pwd",
      status: 422,
      body: {
        fields: [
          {
            name: "name",
            message: "Name must have from 3 to 256 chars."
          },
          {
            name: "password",
            message: "Password must have from 3 to 32 chars."
          },
        ]
      },
      expectedErrorFields: [ "name", "password" ],
    },
  ])("signUp with invalid fields (email=$email, name=$name, password=$password), throw error", async ({ email, name, password, status, body, expectedErrorFields}) => {
    // Given
    (fetchApi as Mock).mockRejectedValue(new ApiError(status, body));

    // When
    let caughtError;
    try {
      await userService.signUp({ email, name, password });
    } catch (error) {
      caughtError = error;
    }

    // Then
    expect(caughtError).toBeDefined();
    expect(caughtError).toBeInstanceOf(InvalidFieldsError);
    const errorFieldsNames = Object.keys((caughtError as InvalidFieldsError).fields);
    expect(errorFieldsNames).toEqual(expectedErrorFields);
  });

  test.each([
    [new Error("Network error."), "Network error."],
    [new ApiError(500, {}), "API error, http status 500"],
  ])("signUp throws unexpected error, throw it further", async (error, expectedMessage) => {
    // Given
    (fetchApi as Mock).mockRejectedValue(error);

    // When, then
    await expect(() => userService.signUp({
      email: "dawid@mail.com",
      name: "Dawid",
      password: "password"
    })).rejects.toThrowError(expectedMessage);
  });

  test("signUp succeeds, return new user", async () => {
    // Given
    (fetchApi as Mock).mockImplementation(async (request: { body: string }) => {
      const body: SignUpRequest = JSON.parse(request.body)
      return {
        id: 1,
        email: body.email,
        name: body.name,
      }
    });

    const request: SignUpRequest = {
      email: "dawid@mail.com",
      name: "Dawid",
      password: "passwd"
    };

    // When
    const newUser = await userService.signUp(request);

    // Then
    expect(newUser).toBeDefined();
    expect(newUser.id).toBeDefined();
    expect(newUser.email).toBe(request.email);
    expect(newUser.name).toBe(request.name);
  });
});