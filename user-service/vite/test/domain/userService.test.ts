import {ApiRequest, fetchApi} from "../../src/api/fetch.ts";
import {afterEach, expect, Mock} from "vitest";
import {ApiError} from "../../src/api/ApiError.ts";
import {userService} from "../../src/domain/userService.ts";
import {settings} from "../../src/settings/settings.ts";
import {InvalidCredentialsError} from "../../src/domain/errors/user.tsx";

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
      const response = {
        ok: false,
        status: statusCode,
      } as Response;
      throw new ApiError(response);
    });
    
    // When, then
    await expect(() => userService.signIn({ email, password }))
      .rejects.toThrowError("Invalid email or password.");
  });

  test.each([
    [
      new ApiError(
        {
          ok: false,
          status: 500
        } as Response
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
    try {
      await userService.signIn({
        email: "dawid@mail.com",
        password: "passwd"
      });
    } catch (error) {
      // Then
      expect(error).instanceOf(Error);
      expect(error).not.instanceOf(InvalidCredentialsError);
      expect((error as Error).message).not.match(/Invalid email or password/i);
    }
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
    "signIn succeeded with userServiceUrl=$userServiceUrl, " +
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