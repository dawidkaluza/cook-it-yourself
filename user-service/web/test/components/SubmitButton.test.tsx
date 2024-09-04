import {SubmitButton} from "../../src/components/SubmitButton.tsx";
import {render, screen} from "@testing-library/react";

describe("SubmitButton component", () => {
  test("render with required props", () => {
    // Given
    // When
    const component = render(<SubmitButton>Submit form</SubmitButton>);

    // Then
    const btnEl = screen.getByRole("button", { name: "Submit form" });
    expect(btnEl).toBeDefined();
    expect(btnEl.getAttribute("disabled")).toBeNull();

    expect(component.container).toMatchSnapshot();
  });

  test("render with all props", () => {
    // Given
    // When
    const component = render(<SubmitButton loading={true} style="w-full">Submit form</SubmitButton>);

    // Then
    const btnEl = screen.getByRole("button", { name: "Submit form" });
    expect(btnEl).toBeDefined();
    expect(btnEl.getAttribute("class")).toContain("w-full");
    expect(btnEl.getAttribute("disabled")).not.toBeNull();

    expect(component.container).toMatchSnapshot();
  })
});