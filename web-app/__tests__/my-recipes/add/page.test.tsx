import {useFormState} from "react-dom";
import {Mock} from "vitest";
import {render, screen} from "@testing-library/react";
import {FieldError} from "@/app/my-recipes/_dtos/errors";
import Page from "@/app/my-recipes/add/page";

vi.mock("react-dom", () => {
  return {
    useFormState: vi.fn(),
  };
});

describe("page component", () => {
  test("render empty form", () => {
    // Given
    (useFormState as Mock).mockReturnValue([
      [],
      () => {}
    ]);

    // When
    const formComponent = render(<Page />);

    // Then
    const nameInput = screen.getByLabelText("Name");
    expect(nameInput).not.toBeNull();

    const descriptionInput = screen.getByLabelText("Description");
    expect(descriptionInput).not.toBeNull();

    const ingredientsInput = screen.getByLabelText("Ingredients");
    expect(ingredientsInput).not.toBeNull();

    const stepsInput = screen.getByLabelText("Method steps");
    expect(stepsInput).not.toBeNull();

    const cookingTimeInput = screen.getByLabelText("Cooking time");
    expect(cookingTimeInput).not.toBeNull();

    const portionSizeInput = screen.getByLabelText("Portion size");
    expect(portionSizeInput).not.toBeNull();

    const submitButton = screen.getByRole("button", { name: "Submit" });
    expect(submitButton).not.toBeNull();

    expect(formComponent.container).toMatchSnapshot();
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
    const formComponent = render(<Page />);

    // Then
    for (const fieldError of fieldErrors) {
      const errorMessage = screen.getByText(fieldError.message);
      expect(errorMessage).not.toBeNull();
    }
    expect(formComponent.container).toMatchSnapshot();
  });
});