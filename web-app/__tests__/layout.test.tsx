import {render, screen} from "@testing-library/react";
import RootLayout from "@/app/layout";
import Page from "@/app/page";

vi.mock("@/app/_api/auth", () => {
  return {
    isSignedIn: () => false,
  };
});

describe("layout component", () => {
  test("renders", () => {
    // Given, when
    const layout = render(<RootLayout><Page /></RootLayout>);

    // Then
    const navBar = screen.getByRole("navigation");
    expect(navBar).not.toBeNull();

    const main = screen.getByRole("main");
    expect(main).not.toBeNull();

    expect(layout.container).toMatchSnapshot();
  });
});