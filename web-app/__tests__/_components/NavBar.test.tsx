import {describe, expect, Mock, test, vi} from 'vitest'
import {NavBar} from "@/app/_components/NavBar";
import {render, screen} from "@testing-library/react";
import {isSignedIn} from "@/app/_api/actions";

vi.mock("@/app/_api/actions", async (importOriginal) => {
  const mod = await importOriginal<typeof import("@/app/_api/actions")>();

  return {
    ...mod,
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
    const indexLink = screen.queryByRole("link", { name: "Cook it yourself" });
    expect(indexLink).not.toBeNull();
    expect(indexLink?.getAttribute("href")).toBe("/");

    const signInLink = screen.queryByRole("link", { name: "Sign in"});
    expect(signInLink).not.toBeNull();
    expect(signInLink?.getAttribute("href")).toBe("/sign-in");

    const navBarMenu = screen.queryByRole('list');
    expect(navBarMenu).toBeNull();

    expect(navBar.container).toMatchSnapshot();
    navBar.unmount();
  });

  test('render when signed in', () => {
    // Given
    (isSignedIn as Mock).mockImplementation(() => true);

    // When
    const navBar = render(<NavBar />);

    // Then
    const indexLink = screen.queryByRole("link", { name: "Cook it yourself" });
    expect(indexLink).not.toBeNull();
    expect(indexLink?.getAttribute("href")).toBe("/");

    const signInLink = screen.queryByRole("link", { name: "Sign in"});
    expect(signInLink).toBeNull();

    const navBarMenu = screen.queryByRole('list');
    expect(navBarMenu).not.toBeNull();

    expect(navBar.container).toMatchSnapshot();
    navBar.unmount();
  });
});