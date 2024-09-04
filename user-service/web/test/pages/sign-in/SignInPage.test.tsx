import {afterAll, beforeAll, beforeEach, describe, expect, Mock} from "vitest";
import {SignInPage} from "../../../src/pages/sign-in/SignInPage.tsx";
import {render, screen} from "@testing-library/react";
import {MemoryRouter} from "react-router-dom";
import {userEvent} from "@testing-library/user-event";
import {userService} from "../../../src/domain/userService.ts";
import {InvalidCredentialsError} from "../../../src/domain/errors/user.tsx";

vi.mock("react", async function <T>(importOriginal: () => Promise<T>) {
  const original = await importOriginal();
  return {
    ...original,
    useId: () => "1234",
  };
});

const navigateMock = vi.fn();
vi.mock("react-router-dom", async function <T>(importOriginal: () => Promise<T>) {
  const original = await importOriginal();
  return {
    ...original,
    useNavigate: () => navigateMock,
  };
});

vi.mock("../../../src/domain/userService.ts", () => {
  return {
    userService: {
      signIn: vi.fn(),
    }
  };
});

beforeAll(() => {
  vi.stubGlobal("console", {
    error: vi.fn()
  });

  vi.stubGlobal("location", {
    assign: vi.fn()
  });
});

beforeEach(() => {
  vi.clearAllMocks();
});

afterAll(() => {
  vi.unstubAllGlobals();
});

describe("SignInPage component", () => {
  test("render", () => {
    // Given, when
    const component = render(
      <MemoryRouter>
        <SignInPage />
      </MemoryRouter>
    );

    // Then
    const emailInputEl = screen.getByLabelText("E-mail");
    expect(emailInputEl).toBeDefined();
    const passwordInputEl = screen.getByLabelText("Password");
    expect(passwordInputEl).toBeDefined();
    const submitBtnEl = screen.getByRole("button", { name: "Sign in" });
    expect(submitBtnEl).toBeDefined();
    const signUpLinkEl = screen.getByText(/sign up/i);
    expect(signUpLinkEl).toBeDefined();
    expect(signUpLinkEl.getAttribute("href")).toBe("/sign-up");

    expect(component.container).toMatchSnapshot();
  });

  test("type email and password, inputs change", async () => {
    // Given
    const user = userEvent.setup();

    const component = render(
      <MemoryRouter>
        <SignInPage />
      </MemoryRouter>
    );

    // When
    const emailInputEl = screen.getByLabelText("E-mail");
    await user.click(emailInputEl);
    await user.keyboard("dawid@gmail.com");

    const passwordInputEl = screen.getByLabelText("Password");
    await user.click(passwordInputEl);
    await user.keyboard("passwd");

    // Then
    expect(emailInputEl.getAttribute("value")).toBe("dawid@gmail.com");
    expect(passwordInputEl.getAttribute("value")).toBe("passwd");

    expect(component.container).toMatchSnapshot();
  });

  test.each([
    {
      error: new InvalidCredentialsError("Invalid email or password."),
      expectedMessage: /invalid email or password/i,
      expectedLog: false,
    },
    {
      error: new Error("Network error."),
      expectedMessage: /unable to process the request/i,
      expectedLog: true,
    },
  ])("submit, $error.message error thrown, message matching $expectedMessage shown", async ({ error, expectedMessage, expectedLog }) => {
    // Given
    (userService.signIn as Mock).mockRejectedValue(error);

    const user = userEvent.setup();
    const component = render(
      <MemoryRouter>
        <SignInPage />
      </MemoryRouter>
    );

    const emailInputEl = screen.getByLabelText("E-mail");
    await user.click(emailInputEl);
    await user.keyboard("dawid@gmail.com");

    const passwordInputEl = screen.getByLabelText("Password");
    await user.click(passwordInputEl);
    await user.keyboard("invalidpasswd");

    // When
    const submitBtnEl = screen.getByRole("button", { name: "Sign in" });
    await user.click(submitBtnEl);

    // Then
    const errorMsgEl = screen.getByText(expectedMessage);
    expect(errorMsgEl).toBeDefined();

    if (expectedLog) {
      expect(console.error).toHaveBeenCalledWith(
        expect.anything(),
        error
      );
    }

    expect(component.container).toMatchSnapshot();
  });

  test.each([
    {
      signInResponse: {
        redirectUrl: "http://user-service/web/consent",
        external: false,
      }
    },
    {
      signInResponse: {
        redirectUrl: "http://api-gateway/oauth2?code=123xyz",
        external: true,
      }
    },
  ])("submit with valid username and password, redirect as expected", async ({ signInResponse }) => {
    // Given
    (userService.signIn as Mock).mockResolvedValue(signInResponse);

    const user = userEvent.setup();
    const component = render(
      <MemoryRouter>
        <SignInPage />
      </MemoryRouter>
    );

    const emailInputEl = screen.getByLabelText("E-mail");
    await user.click(emailInputEl);
    await user.keyboard("dawid@gmail.com");

    const passwordInputEl = screen.getByLabelText("Password");
    await user.click(passwordInputEl);
    await user.keyboard("passwd");

    // When
    const submitBtnEl = screen.getByRole("button", { name: "Sign in" });
    await user.click(submitBtnEl);

    // Then
    const successMsgEl = screen.getByText(/signing in proceeded successfully/i);
    expect(successMsgEl).toBeDefined();

    if (signInResponse.external) {
      await expect.poll(() => window.location.assign).toHaveBeenCalledWith(signInResponse.redirectUrl);
    } else{
      await expect.poll(() => navigateMock).toHaveBeenCalledWith(signInResponse.redirectUrl);
    }

    expect(component.container).toMatchSnapshot();
  });
});