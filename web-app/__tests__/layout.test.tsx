import {render, screen} from "@testing-library/react";
import RootLayout from "@/app/layout";

vi.mock("@/app/_api/auth", () => {
  return {
    isSignedIn: () => false,
  };
});

describe("layout component", () => {
  test("renders", () => {
    // Given, when
    const layout = render(<RootLayout><p>Hello World</p></RootLayout>);

    // Then
    const header = screen.getByRole("banner");
    expect(header).not.toBeNull();

    const main = screen.getByRole("main");
    expect(main).not.toBeNull();

    const text = screen.getByText(/hello world/i);
    expect(text).not.toBeNull();

    expect(layout.container).toMatchSnapshot();
  });
});