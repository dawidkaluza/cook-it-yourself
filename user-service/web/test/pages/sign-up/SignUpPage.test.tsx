import {render, screen} from "@testing-library/react";
import {MemoryRouter} from "react-router-dom";
import {SignUpPage} from "../../../src/pages/sign-up/SignUpPage.tsx";
import {userEvent} from "@testing-library/user-event";
import {userService} from "../../../src/domain/userService.ts";
import {afterAll, beforeAll, beforeEach, expect, Mock} from "vitest";
import {InvalidFieldsError} from "../../../src/domain/errors/user.tsx";
import {SignUpRequest, SignUpResponse} from "../../../src/domain/dtos/user.ts";


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
      signUp: vi.fn(),
    }
  };
});

beforeAll(() => {
  vi.stubGlobal("console", {
    error: vi.fn()
  });
});

beforeEach(() => {
  vi.clearAllMocks();
});

afterAll(() => {
  vi.unstubAllGlobals();
});

describe("SignUpPage component", () => {
  test("render", () => {
    // Given
    // When
    const component = render(
      <MemoryRouter>
        <SignUpPage />
      </MemoryRouter>
    );

    // Then
    const emailInputEl = screen.getByLabelText("E-mail");
    expect(emailInputEl).toBeDefined();
    const nameInputEl = screen.getByLabelText("Name");
    expect(nameInputEl).toBeDefined();
    const passwordInputEl = screen.getByLabelText("Password");
    expect(passwordInputEl).toBeDefined();
    expect(passwordInputEl.getAttribute("type")).toBe("password");
    const submitBtnEl = screen.getByRole("button", { name: /sign up/i });
    expect(submitBtnEl).toBeDefined();
    expect(submitBtnEl.getAttribute("type")).toBe("submit");
    const signInLinkEl = screen.getByRole("link", { name: /sign in/i });
    expect(signInLinkEl).toBeDefined();
    expect(signInLinkEl.getAttribute("href")).toBe("/sign-in");

    expect(component.container).toMatchSnapshot();
  });

  test("type email, name and password, inputs change", async () => {
    // Given
    const user = userEvent.setup();
    const component = render(
      <MemoryRouter>
        <SignUpPage />
      </MemoryRouter>
    );

    const emailInputEl = screen.getByLabelText("E-mail");
    await user.click(emailInputEl);
    await user.keyboard("dawid@mail.com");

    const nameInputEl = screen.getByLabelText("Name");
    await user.click(nameInputEl);
    await user.keyboard("Dawid");

    const passwordInputEl = screen.getByLabelText("Password");
    await user.click(passwordInputEl);
    await user.keyboard("passwd");

    // Then
    expect(emailInputEl.getAttribute("value")).toBe("dawid@mail.com");
    expect(nameInputEl.getAttribute("value")).toBe("Dawid");
    expect(passwordInputEl.getAttribute("value")).toBe("passwd");

    expect(component.container).toMatchSnapshot();
  });

  test.each([
    {
      error: new InvalidFieldsError({
        email: "E-mail already exists."
      }),
      inputs: {
        email: "dawid@mail.com",
        name: "Dawid",
        password: "passwd",
      },
      expectedMessages: [
        "E-mail already exists."
      ]
    },
    {
      error: new InvalidFieldsError({
        name: "Name must have from 3 to 256 chars.",
        password: "Field must not be empty.",
      }),
      inputs: {
        email: "dawid@email.com",
        name: "D",
        password: "",
      },
      expectedMessages: [
        "Name must have from 3 to 256 chars.",
        "Field must not be empty.",
      ]
    }
  ])("fill out the form and submit, $error.message thrown, errors shown", async (
    { error, inputs, expectedMessages }: { error: Error, inputs: Record<string, string>, expectedMessages: Array<string> }
  ) => {
    // Given
    (userService.signUp as Mock).mockRejectedValue(error);

    const user = userEvent.setup();
    const component = render(
      <MemoryRouter>
        <SignUpPage />
      </MemoryRouter>
    );

    const labels: Record<string, string> = {
      email: "E-mail",
      name: "Name",
      password: "Password",
    };
    for (const key in labels) {
      if (!inputs[key]) {
        continue;
      }

      const inputEl = screen.getByLabelText(labels[key]);
      await user.click(inputEl);
      await user.keyboard(inputs[key]);
    }

    // When
    const submitBtnEl = screen.getByRole("button", { name: /sign up/i });
    await user.click(submitBtnEl);

    // Then
    for (const expectedMessage of expectedMessages) {
      const errorMsgEl = screen.getByText(expectedMessage);
      expect(errorMsgEl).toBeDefined();
    }

    expect(component.container).toMatchSnapshot();
  });

  test("fill out the form and submit, unexpected error thrown, error message shown", async () => {
    // Given
    const error = new Error("Unexpected error");
    (userService.signUp as Mock).mockRejectedValue(error);

    const user = userEvent.setup();
    const component = render(
      <MemoryRouter>
        <SignUpPage />
      </MemoryRouter>
    );

    const emailInputEl = screen.getByLabelText("E-mail");
    await user.click(emailInputEl);
    await user.keyboard("dawid@mail.com");

    const nameInputEl = screen.getByLabelText("Name");
    await user.click(nameInputEl);
    await user.keyboard("Dawid");

    const passwordInputEl = screen.getByLabelText("Password");
    await user.click(passwordInputEl);
    await user.keyboard("passwd");

    // When
    const submitBtnEl = screen.getByRole("button", { name: /sign up/i });
    await user.click(submitBtnEl);

    // Then
    const errorMsgEl = screen.getByText(/unable to process the request/i);
    expect(errorMsgEl).toBeDefined();

    expect(console.error).toHaveBeenCalledWith(
      expect.anything(),
      error
    );

    expect(component.container).toMatchSnapshot();
  });

  test("fill out the form and submit, sign up succeeded, show message and redirect", async () => {
    // Given
    (userService.signUp as Mock).mockImplementation((request: SignUpRequest): Promise<SignUpResponse> => {
      return Promise.resolve({
        id: 1,
        email: request.email,
        name: request.name
      });
    });

    const user = userEvent.setup();
    const component = render(
      <MemoryRouter>
        <SignUpPage />
      </MemoryRouter>
    );

    const emailInputEl = screen.getByLabelText("E-mail");
    await user.click(emailInputEl);
    await user.keyboard("dawid@mail.com");

    const nameInputEl = screen.getByLabelText("Name");
    await user.click(nameInputEl);
    await user.keyboard("Dawid");

    const passwordInputEl = screen.getByLabelText("Password");
    await user.click(passwordInputEl);
    await user.keyboard("passwd");

    // When
    const submitBtnEl = screen.getByRole("button", { name: /sign up/i });
    await user.click(submitBtnEl);

    // Then
    const successMsgEl = screen.getByText(/signed up successfully/i);
    expect(successMsgEl).toBeDefined();
    await expect.poll(() => navigateMock).toHaveBeenCalledWith("/sign-in");

    expect(component.container).toMatchSnapshot();
  });
});