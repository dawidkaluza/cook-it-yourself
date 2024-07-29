import {render, screen} from "@testing-library/react";
import Page from "@/app/page";

describe("page component", () => {
  test("render", () => {
    // Given, when
    const page = render(<Page />);

    // Then
    const welcomeText = screen.getByText(/Welcome my cook/);
    expect(welcomeText).not.toBeNull();
    expect(page.container).toMatchSnapshot();
  })
});