import {render, screen} from "@testing-library/react";
import {NavBarMenu} from "@/app/_components/NavBarMenu";

describe("NavBarMenu component", () => {
  test("renders", () => {
    // Given, when
    const menu = render(<NavBarMenu />);

    // Then
    const button = screen.getByRole("button");
    expect(button).not.toBeNull();
    expect(button?.getAttribute("data-bs-toggle")).toBe("collapse");
    expect(button?.getAttribute("data-bs-target")).toBe("#navbar-menu");

    const menuContainer = menu.container.querySelector("#navbar-menu");
    expect(menuContainer).not.toBeNull();
    expect(menuContainer?.getAttribute("class")).toBe("collapse navbar-collapse");

    const links = menu.queryAllByRole("link");
    expect(links).toHaveLength(1);
    expect(links[0]).not.toBeNull();
    expect(links[0].getAttribute("href")).toBe("/my-recipes");

    expect(menu.container).toMatchSnapshot();
  });
})