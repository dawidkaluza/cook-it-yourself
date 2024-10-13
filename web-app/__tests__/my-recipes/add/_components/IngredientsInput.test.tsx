import {IngredientsInput} from "@/app/my-recipes/add/_components/IngredientsInput";
import {render, screen, within} from "@testing-library/react";
import {FieldError} from "@/app/my-recipes/_dtos/errors";
import userEvent from "@testing-library/user-event";

describe("IngredientsInput component", () => {
  test("render without validation errors", () => {
    // Given
    // When
    const component = render(<IngredientsInput />);

    // Then
    const inputElement = screen.getByLabelText("Ingredients");
    expect(inputElement).not.toBeNull();

    expect(component.container).toMatchSnapshot();
  });

  test("render with validation errors", () => {
    // Given
    const fieldErrors: FieldError[] = [
      {
        name: "ingredient",
        message: "Some ingredient error.",
      },
      {
        name: "ingredient",
        message: "Some other ingredient error.",
      },
    ];

    // When
    const component = render(<IngredientsInput fieldErrors={fieldErrors} />);

    // Then
    const inputElement = screen.getByLabelText("Ingredients");
    expect(inputElement).not.toBeNull();

    for (const fieldError of fieldErrors) {
      const errorElement = screen.getByText(fieldError.message);
      expect(errorElement).not.toBeNull();
    }

    expect(component.container).toMatchSnapshot();
  });

  test("render and add new ingredient", async () => {
    // Given
    const expectedInputs = [
      "Sausages",
      "1",
      "Pot",
      "2",
      "Water",
      "500",
      "ml"
    ];

    const user = userEvent.setup();
    const component = render(<IngredientsInput />);

    // When
    const newIngredientInputEl = screen.getByLabelText("Ingredients");
    const addButtonEl = screen.getByRole("button");

    await user.clear(newIngredientInputEl);
    await user.click(addButtonEl);

    await user.type(newIngredientInputEl, "Sausages");
    await user.click(addButtonEl);

    await user.type(newIngredientInputEl, "Pot 2");
    await user.click(addButtonEl);

    await user.type(newIngredientInputEl, "Water 500ml");
    await user.click(newIngredientInputEl);
    await user.keyboard("{Enter}");

    // Then
    const allInputElements = screen.getAllByRole("textbox");

    for (const expectedInput of expectedInputs) {
      expect(allInputElements.filter(
        el => el.getAttribute("value") === expectedInput
      )).toHaveLength(1);
    }

    expect(component.container).toMatchSnapshot();
  });

  test("render, add and modify added ingredients", async () => {
    // Given
    const expectedInputs = [
      "Sausage",
      "1",
      "Pot",
      "3",
      "Water",
      "500",
      "g"
    ];

    const user = userEvent.setup();
    const component = render(<IngredientsInput />);

    const newIngredientInputEl = screen.getByLabelText("Ingredients");
    const addButtonEl = screen.getByRole("button");

    await user.type(newIngredientInputEl, "Sausages");
    await user.click(addButtonEl);

    await user.type(newIngredientInputEl, "Pot 2");
    await user.click(addButtonEl);

    await user.type(newIngredientInputEl, "Water 500ml");
    await user.click(addButtonEl);

    // When
    const sausagesIngredientEl = screen.getByDisplayValue("Sausages");
    await user.clear(sausagesIngredientEl);
    await user.type(sausagesIngredientEl, "Sausage");

    const potAmountEl = screen.getByDisplayValue("2");
    await user.clear(potAmountEl);
    await user.type(potAmountEl, "3");

    const waterUnitEl = screen.getByDisplayValue("ml");
    await user.clear(waterUnitEl);
    await user.type(waterUnitEl, "g");

    // Then
    const allInputElements = screen.getAllByRole("textbox");

    for (const expectedInput of expectedInputs) {
      expect(allInputElements.filter(
        el => el.getAttribute("value") === expectedInput
      )).toHaveLength(1);
    }

    expect(component.container).toMatchSnapshot();
  });

  test("render, add and remove ingredients", async () => {
    // Given
    const expectedInputs = [
      "Pot",
      "2",
      "Water",
      "500",
      "ml"
    ];

    const expectedRemovedInputs = [
      "Sausages",
    ];

    const user = userEvent.setup();
    const component = render(<IngredientsInput />);

    const newIngredientInputEl = screen.getByLabelText("Ingredients");
    const addButtonEl = screen.getByRole("button");

    await user.type(newIngredientInputEl, "Sausages");
    await user.click(addButtonEl);

    await user.type(newIngredientInputEl, "Pot 2");
    await user.click(addButtonEl);

    await user.type(newIngredientInputEl, "Water 500ml");
    await user.click(addButtonEl);

    // When
    const sausagesDeleteBtnEl = within(
      screen.getByDisplayValue("Sausages").parentElement as HTMLElement
    ).getByRole("button");
    await user.click(sausagesDeleteBtnEl);

    // Then
    const allInputElements = screen.getAllByRole("textbox");

    for (const expectedInput of expectedInputs) {
      expect(allInputElements.filter(
        el => el.getAttribute("value") === expectedInput
      )).toHaveLength(1);
    }

    for (const expectedRemovedInput of expectedRemovedInputs) {
      expect(allInputElements.filter(
        el => el.getAttribute("value") === expectedRemovedInput
      )).toHaveLength(0);
    }

    expect(component.container).toMatchSnapshot();
  });
});