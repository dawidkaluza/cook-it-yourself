import {beforeEach, describe, expect, test, vi} from 'vitest'
import {NavBar} from "@/app/_components/NavBar";
import {render, screen} from "@testing-library/react";
import {isSignedIn} from "@/app/_api/actions";
import {Mock} from "node:test";

vi.mock("@/app/_api/actions", async (importOriginal) => {
  const mod = await importOriginal<typeof import("@/app/_api/actions")>();

  return {
    ...mod,
    isSignedIn: vi.fn(),
  };
});

describe('NavBar component', () => {
  test('render when not signed in', () => {
    // Given
    (isSignedIn as Mock<any>).mockImplementation(() => false);

    // When
    render(<NavBar />);

    // Then
    const signInLink = screen.queryByRole("link", { name: "Sign in"});
    expect(signInLink).not.toBeNull();
    expect(signInLink?.getAttribute("href")).toBe("/sign-in")

    const navBarMenu = screen.queryByRole('list');
    expect(navBarMenu).toBeNull();
  })
});