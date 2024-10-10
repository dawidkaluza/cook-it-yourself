import {render, screen} from "@testing-library/react";
import Page from "@/app/page";
import {isSignedIn} from "@/app/_api/auth";
import {Mock} from "vitest";


vi.mock("@/app/_api/auth", () => {
  return {
    isSignedIn: vi.fn(),
  };
});

describe("page component", () => {
  test("render", () => {
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
});