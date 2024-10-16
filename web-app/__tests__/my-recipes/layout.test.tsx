import {render, screen} from "@testing-library/react";
import Layout from "@/app/my-recipes/layout";

vi.mock("next/navigation", () => {
  return {
    usePathname: () => "/my-recipes/",
  };
});

describe("layout component", () => {
  test("renders", () => {
    // Given, when
    const layout = render(<Layout><p>Hello World</p></Layout>);

    // Then
    const navigation = screen.getByRole("navigation");
    expect(navigation).not.toBeNull();

    const text = screen.getByText(/hello world/i);
    expect(text).not.toBeNull();

    expect(layout.container).toMatchSnapshot();
  });
});