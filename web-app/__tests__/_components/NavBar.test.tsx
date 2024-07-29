import {Mock} from 'vitest'
import {NavBar} from "@/app/_components/NavBar";
import {render, screen} from "@testing-library/react";
import {isSignedIn} from "@/app/_api/auth";

vi.mock("@/app/_api/auth", () => {
  return {
    isSignedIn: vi.fn(),
  };
});

describe('NavBar component', () => {
  test('render when signed out', () => {
    // Given
    (isSignedIn as Mock).mockImplementation(() => false);

    // When
    const navBar = render(<NavBar />);

    // Then
    const indexLink = screen.getByRole("link", { name: "Cook it yourself" });
    expect(indexLink).not.toBeNull();
    expect(indexLink?.getAttribute("href")).toBe("/");

    const signInLink = screen.getByRole("link", { name: "Sign in"});
    expect(signInLink).not.toBeNull();
    expect(signInLink?.getAttribute("href")).toBe("/sign-in");

    const navBarMenu = screen.queryByRole('list');
    expect(navBarMenu).toBeNull();

    expect(navBar.container).toMatchSnapshot();
  });

  test('render when signed in', () => {
    // Given
    (isSignedIn as Mock).mockImplementation(() => true);

    // When
    const navBar = render(<NavBar />);

    // Then
    const indexLink = screen.getByRole("link", { name: "Cook it yourself" });
    expect(indexLink).not.toBeNull();
    expect(indexLink?.getAttribute("href")).toBe("/");

    const signInLink = screen.queryByRole("link", { name: "Sign in"});
    expect(signInLink).toBeNull();

    const navBarMenu = screen.getByRole('list');
    expect(navBarMenu).not.toBeNull();

    expect(navBar.container).toMatchSnapshot();
  });
});