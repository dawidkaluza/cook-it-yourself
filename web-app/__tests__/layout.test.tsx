import {describe, expect, test, vi} from "vitest";
import {render, screen} from "@testing-library/react";
import RootLayout from "@/app/layout";
import Page from "@/app/page";

vi.mock("@/app/_api/actions", async (importOriginal) => {
  const mod = await importOriginal<typeof import("@/app/_api/actions")>();

  return {
    ...mod,
    isSignedIn: () => false,
  };
});

describe("layout component", () => {
  test("renders", () => {
    // Given, when
    const layout = render(<RootLayout><Page /></RootLayout>);

    // Then
    const navBar = screen.queryByRole("navigation");
    expect(navBar).not.toBeNull();

    const main = screen.queryByRole("main");
    expect(main).not.toBeNull();

    expect(layout.container).toMatchSnapshot();
    layout.unmount();
  });
});