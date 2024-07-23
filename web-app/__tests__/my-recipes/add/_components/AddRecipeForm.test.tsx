import {describe, expect, Mock, test, vi} from "vitest";
import {useFormState} from "react-dom";
import {render, screen} from "@testing-library/react";
import {AddRecipeForm} from "@/app/my-recipes/add/_components/AddRecipeForm";
import {FieldError} from "@/app/my-recipes/add/actions";

vi.mock("react-dom", () => {
  return {
    useFormState: vi.fn(),
  };
});

describe("AddRecipeForm component", () => {
  test("render empty form", () => {
    // Given
    (useFormState as Mock).mockReturnValue([
      [],
      () => {}
    ]);

    // When
    const formComponent = render(<AddRecipeForm />);

    // Then
    const nameInput = screen.queryByLabelText("Name");
    expect(nameInput).not.toBeNull();

    const descriptionInput = screen.queryByLabelText("Description");
    expect(descriptionInput).not.toBeNull();

    const ingredientsInput = screen.queryByLabelText("Ingredients");
    expect(ingredientsInput).not.toBeNull();

    const stepsInput = screen.queryByLabelText("Method steps");
    expect(stepsInput).not.toBeNull();

    const cookingTimeInput = screen.queryByLabelText("Cooking time");
    expect(cookingTimeInput).not.toBeNull();

    const portionSizeInput = screen.queryByLabelText("Portion size");
    expect(portionSizeInput).not.toBeNull();

    const submitButton = screen.queryByRole("button", { name: "Submit" });
    expect(submitButton).not.toBeNull();

    expect(formComponent.container).toMatchSnapshot();
    formComponent.unmount();
  });

  test.each([
    // Case 1
    [
      [
        {
          name: "name",
          message: "Some name error."
        },
      ]
    ],

    // Case 2
    [
      [
        {
          name: "description",
          message: "Some description error."
        },
        {
          name: "ingredient[0].name",
          message: "Some ingredient error."
        },
        {
          name: "methodStep[1].text",
          message: "Some method step error."
        },
      ],
    ],

    // Case 3
    [
      [
        {
          name: "cookingTime",
          message: "Some cooking time error."
        },
        {
          name: "portionSize",
          message: "Some portion size error."
        },
      ]
    ]
  ])("render with various errors", (fieldErrors: FieldError[]) => {
    // Given
    (useFormState as Mock).mockReturnValue([
      fieldErrors,
      () => {}
    ]);

    // When
    const formComponent = render(<AddRecipeForm />);

    // Then
    for (const fieldError of fieldErrors) {
      const errorMessage = screen.queryByText(fieldError.message);
      expect(errorMessage).not.toBeNull();
    }
    expect(formComponent.container).toMatchSnapshot();
    formComponent.unmount();
  });
});