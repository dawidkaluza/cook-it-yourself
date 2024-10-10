import {render, screen} from "@testing-library/react";
import {WelcomeMessage} from "@/app/_components/WelcomeMessage";

describe("WelcomeMessage component", () => {
  test("render", () => {
    // Given
    // When
    const page = render(<WelcomeMessage />);

    // Then
    const signInLink = screen.getByRole("link", { name: /sign in/i });
    expect(signInLink).not.toBeNull();
    expect(signInLink?.getAttribute("href")).toBe("/sign-in");

    expect(page.container).toMatchSnapshot();
  });
});