import {Mock} from 'vitest'
import {render, screen} from "@testing-library/react";
import {isSignedIn} from "@/app/_api/auth";
import {Header} from "@/app/_components/Header";

vi.mock("@/app/_api/auth", () => {
  return {
    isSignedIn: vi.fn(),
  };
});

describe('Header component', () => {
  test('render when signed out', () => {
    // Given
    (isSignedIn as Mock).mockImplementation(() => false);

    // When
    const navBar = render(<Header />);

    // Then
    const indexLink = screen.getByRole("link", { name: "Cook it yourself" });
    expect(indexLink).not.toBeNull();
    expect(indexLink?.getAttribute("href")).toBe("/");

    const signInLink = screen.getByRole("link", { name: "Sign in"});
    expect(signInLink).not.toBeNull();
    expect(signInLink?.getAttribute("href")).toBe("/sign-in");

    const signOutLink = screen.queryByRole("link", { name: "Sign out"});
    expect(signOutLink).toBeNull();

    expect(navBar.container).toMatchSnapshot();
  });

  test('render when signed in', () => {
    // Given
    (isSignedIn as Mock).mockImplementation(() => true);

    // When
    const navBar = render(<Header />);

    // Then
    const indexLink = screen.getByRole("link", { name: "Cook it yourself" });
    expect(indexLink).not.toBeNull();
    expect(indexLink?.getAttribute("href")).toBe("/");

    const signInLink = screen.queryByRole("link", { name: "Sign in"});
    expect(signInLink).toBeNull();

    const signOutLink = screen.getByRole("link", { name: "Sign out"});
    expect(signOutLink).not.toBeNull();
    expect(signOutLink?.getAttribute("href")).toBe("/sign-out");

    expect(navBar.container).toMatchSnapshot();
  });
});