import {render, screen} from "@testing-library/react";
import Page from "@/app/page";
import {useAuth} from "@/app/_api/hooks";
import {Mock} from "vitest";


vi.mock("@/app/_api/hooks", () => {
  return {
    useAuth: vi.fn(),
  };
});

describe("page component", () => {
  test("render when signed out", () => {
    // Given
    (useAuth as Mock).mockImplementation(() => {
      return {
        isSignedIn: false,
        name: undefined
      };
    });

    // When
    const page = render(<Page />);

    // Then
    const signInLink = screen.getByRole("link", { name: /sign in/i });
    expect(signInLink).not.toBeNull();
    expect(signInLink?.getAttribute("href")).toBe("/sign-in");

    expect(page.container).toMatchSnapshot();
  });

  test("render when signed in", () => {
    // Given
    (useAuth as Mock).mockImplementation(() => {
      return {
        isSignedIn: true,
        name: "Dawid"
      };
    });

    // When
    const page = render(<Page />);

    // Then
    const welcomeText = screen.getByText(/welcome/i);
    expect(welcomeText).not.toBeNull();

    expect(page.container).toMatchSnapshot();
  });
});