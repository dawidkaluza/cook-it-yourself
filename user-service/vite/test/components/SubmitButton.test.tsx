import {SubmitButton} from "../../src/components/SubmitButton.tsx";
import {render, screen} from "@testing-library/react";

describe("SubmitButton component", () => {
  test("render", () => {
    // Given
    // When
    const component = render(<SubmitButton style="w-full">Submit form</SubmitButton>);

    // Then
    const btnEl = screen.getByRole("button", { name: "Submit form" });
    expect(btnEl).toBeDefined();
    expect(btnEl.getAttribute("class")).toContain("w-full");

    expect(component.container).toMatchSnapshot();
  });
});