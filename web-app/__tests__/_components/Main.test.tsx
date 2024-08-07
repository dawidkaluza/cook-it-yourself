import {render, screen} from "@testing-library/react";
import {Main} from "@/app/_components/Main";

describe("Main component", () => {
  test("renders", () => {
    // Given, when
    const menu = render(<Main><p>Main page</p></Main>);

    // Then
    const main = screen.getByRole("main");
    expect(main).not.toBeNull();

    const p = screen.getByText(/Main page/);
    expect(p).not.toBeNull();

    expect(menu.container).toMatchSnapshot();
  });
});