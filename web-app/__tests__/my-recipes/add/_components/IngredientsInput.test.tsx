import {describe, expect, test} from "vitest";
import {IngredientsInput} from "@/app/my-recipes/add/_components/IngredientsInput";
import {fireEvent, render, screen, within} from "@testing-library/react";
import {FieldError} from "@/app/my-recipes/add/actions";

describe("IngredientsInput component", () => {
  test("render without errors", () => {
    // Given
    // When
    const component = render(<IngredientsInput />);

    // Then
    const inputElement = screen.getByLabelText("Ingredients");
    expect(inputElement).not.toBeNull();

    expect(component.container).toMatchSnapshot();
    component.unmount();
  });

  test("render with errors", () => {
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
    component.unmount();
  });

  test("render and add new ingredient", () => {
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

    const component = render(<IngredientsInput />);

    // When
    const newIngredientInputEl = screen.getByLabelText("Ingredients");
    const addButtonEl = screen.getByRole("button");

    fireEvent.change(newIngredientInputEl, { target: { value: "" } });
    fireEvent.click(addButtonEl);

    fireEvent.change(newIngredientInputEl, { target: { value: "Sausages" } });
    fireEvent.click(addButtonEl);

    fireEvent.change(newIngredientInputEl, { target: { value: "Pot 2" } });
    fireEvent.click(addButtonEl);

    fireEvent.change(newIngredientInputEl, { target: { value: "Water 500ml" } });
    fireEvent.keyDown(newIngredientInputEl, {key: 'Enter', code: 'Enter', charCode: 13});

    // Then
    const allInputElements = screen.getAllByRole("textbox");

    for (const expectedInput of expectedInputs) {
      expect(allInputElements.filter(
        el => el.getAttribute("value") === expectedInput
      )).toHaveLength(1);
    }

    expect(component.container).toMatchSnapshot();
    component.unmount();
  });

  test("render, add and modify added ingredients", () => {
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

    const component = render(<IngredientsInput />);

    const newIngredientInputEl = screen.getByLabelText("Ingredients");
    const addButtonEl = screen.getByRole("button");

    fireEvent.change(newIngredientInputEl, { target: { value: "Sausages" } });
    fireEvent.click(addButtonEl);

    fireEvent.change(newIngredientInputEl, { target: { value: "Pot 2" } });
    fireEvent.click(addButtonEl);

    fireEvent.change(newIngredientInputEl, { target: { value: "Water 500ml" } });
    fireEvent.click(addButtonEl);

    // When
    const sausagesIngredientEl = screen.getByDisplayValue("Sausages");
    fireEvent.change(sausagesIngredientEl, { target: { value: "Sausage" } });

    const potAmountEl = screen.getByDisplayValue("2");
    fireEvent.change(potAmountEl, { target: { value: "3" } });

    const waterUnitEl = screen.getByDisplayValue("ml");
    fireEvent.change(waterUnitEl, { target: { value: "g" } });

    fireEvent.click(addButtonEl); // workaround to cause state update

    // Then
    const allInputElements = screen.getAllByRole("textbox");

    for (const expectedInput of expectedInputs) {
      console.log("Input: ", expectedInput);
      expect(allInputElements.filter(
        el => el.getAttribute("value") === expectedInput
      )).toHaveLength(1);
    }

    expect(component.container).toMatchSnapshot();
    component.unmount();
  });

  test("render, add and remove ingredients", () => {
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

    const component = render(<IngredientsInput />);

    const newIngredientInputEl = screen.getByLabelText("Ingredients");
    const addButtonEl = screen.getByRole("button");

    fireEvent.change(newIngredientInputEl, { target: { value: "Sausages" } });
    fireEvent.click(addButtonEl);

    fireEvent.change(newIngredientInputEl, { target: { value: "Pot 2" } });
    fireEvent.click(addButtonEl);

    fireEvent.change(newIngredientInputEl, { target: { value: "Water 500ml" } });
    fireEvent.click(addButtonEl);

    // When
    const sausagesDeleteBtnEl = within(
      screen.getByDisplayValue("Sausages").parentElement as HTMLElement
    ).getByRole("button");
    fireEvent.click(sausagesDeleteBtnEl);

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
    component.unmount();
  });
});