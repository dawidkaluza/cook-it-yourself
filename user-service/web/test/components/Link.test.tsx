import {render, screen} from "@testing-library/react";
import {expect} from "vitest";
import {Link} from "../../src/components/Link";
import {MemoryRouter} from "react-router-dom";

describe("Link component", () => {
  test("render", () => {
    // Given
    // When
    const component = render(
      <MemoryRouter>
        <Link to={"/about-me"}>About me</Link>
      </MemoryRouter>
    );

    // Then
    const aEl = screen.getByRole("link", { name: "About me" });
    expect(aEl).toBeDefined();
    expect(aEl.getAttribute("href")).toBe("/about-me");

    expect(component.container).toMatchSnapshot();
  });
})