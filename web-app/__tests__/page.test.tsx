import {render, screen} from "@testing-library/react";
import Page from "@/app/page";
import {isSignedIn} from "@/app/_api/auth";
import {afterEach, Mock} from "vitest";
import {redirect} from "next/navigation";

vi.mock("@/app/_api/auth", () => {
  return {
    isSignedIn: vi.fn(),
  };
});

vi.mock("next/navigation", () => {
  return {
    redirect: vi.fn(),
  };
});

describe("page component", () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  test("render as a new user", () => {
    // Given
    (isSignedIn as Mock).mockImplementation(() => false);

    // When
    const page = render(<Page />);

    // Then
    const signInLink = screen.getByRole("link", { name: /sign in/i });
    expect(signInLink).not.toBeNull();
    expect(signInLink?.getAttribute("href")).toBe("/sign-in");

    expect(page.container).toMatchSnapshot();
  });

  test("render as a signed-in user", () => {
    // Given
    (isSignedIn as Mock).mockImplementation(() => true);

    // When
    const page = render(<Page />);

    // Then
    expect(redirect).toHaveBeenCalledOnce();
  });
});