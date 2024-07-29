import {MethodStepsInput} from "@/app/my-recipes/add/_components/MethodStepsInput";
import {render, screen, within} from "@testing-library/react";
import {FieldError} from "@/app/my-recipes/add/actions";
import userEvent from "@testing-library/user-event";

describe("MethodStepsInput component", () => {
  test("render without validation errors", () => {
    // Given
    // When
    const component = render(<MethodStepsInput />);

    // Then
    const inputElement = screen.getByLabelText("Method steps");
    expect(inputElement).not.toBeNull();

    expect(component.container).toMatchSnapshot();
  });

  test("render with validation errors", () => {
    // Given
    const fieldErrors: FieldError[] = [
      {
        name: "methodStep",
        message: "Some text error.",
      },
      {
        name: "methodStep",
        message: "Some other text error.",
      }
    ];

    // When
    const component = render(<MethodStepsInput fieldErrors={fieldErrors} />);

    // Then
    const inputElement = screen.getByLabelText("Method steps");
    expect(inputElement).not.toBeNull();

    for (const fieldError of fieldErrors) {
      const errorElement = screen.getByText(fieldError.message);
      expect(errorElement).not.toBeNull();
    }

    expect(component.container).toMatchSnapshot();
  });

  test("render and add new step", async () => {
    // Given
    const expectedInputs = [
      "Add some sugar",
      "Mix everything",
      "Pour water into the pot"
    ];

    const user = userEvent.setup();
    const component = render(<MethodStepsInput />);

    // When
    const newStepInputEl = screen.getByLabelText("Method steps");
    const addBtnEl = screen.getByRole("button");

    await user.clear(newStepInputEl);
    await user.click(addBtnEl);

    await user.type(newStepInputEl, "Add some sugar");
    await user.click(addBtnEl);

    await user.type(newStepInputEl, "Mix everything");
    await user.click(addBtnEl);

    await user.type(newStepInputEl, "Pour water into the pot");
    await user.click(newStepInputEl);
    await user.keyboard("{Enter}");

    // Then
    const allInputEls = screen.getAllByRole("textbox") as HTMLTextAreaElement[];
    for (const expectedInput of expectedInputs) {
      expect(allInputEls.filter(
        el => el.value === expectedInput
      )).toHaveLength(1);
    }

    expect(component.container).toMatchSnapshot();
  });

  test("render, add and modify added steps", async () => {
    // Given
    const expectedInputs = [
      "Add some salt",
      "Mix everything",
      "Add some oil"
    ];

    const user = userEvent.setup();
    const component = render(<MethodStepsInput />);

    const newStepInputEl = screen.getByLabelText("Method steps");
    const addBtnEl = screen.getByRole("button");

    await user.type(newStepInputEl, "Add some sugar");
    await user.click(addBtnEl);

    await user.type(newStepInputEl, "Mix everything");
    await user.click(addBtnEl);

    await user.type(newStepInputEl, "Pour water into the pot");
    await user.click(addBtnEl);

    // When
    const addSugarStepEl = await screen.getByDisplayValue("Add some sugar");
    await user.clear(addSugarStepEl);
    await user.type(addSugarStepEl, "Add some salt");

    const pourWaterStepEl = await screen.getByDisplayValue("Pour water into the pot");
    await user.clear(pourWaterStepEl);
    await user.type(pourWaterStepEl, "Add some oil");

    // Then
    const allInputEls = screen.getAllByRole("textbox") as HTMLTextAreaElement[];
    for (const expectedInput of expectedInputs) {
      expect(allInputEls.filter(
        el => el.value === expectedInput
      )).toHaveLength(1);
    }

    expect(component.container).toMatchSnapshot();
  });

  test("render, add and remove steps", async () => {
    // Given
    const expectedInputs = [
      "Add some sugar",
      "Pour water into the pot"
    ];

    const expectedRemovedInputs = [
      "Mix everything",
    ];

    const user = userEvent.setup();
    const component = render(<MethodStepsInput />);

    const newStepInputEl = screen.getByLabelText("Method steps");
    const addBtnEl = screen.getByRole("button");

    await user.type(newStepInputEl, "Add some sugar");
    await user.click(addBtnEl);

    await user.type(newStepInputEl, "Mix everything");
    await user.click(addBtnEl);

    await user.type(newStepInputEl, "Pour water into the pot");
    await user.click(addBtnEl);

    // When
    const mixDeleteBtnEl = within(
      screen.getByDisplayValue("Mix everything").parentElement as HTMLElement
    ).getByRole("button");
    await user.click(mixDeleteBtnEl);

    // Then
    const allInputEls = screen.getAllByRole("textbox") as HTMLTextAreaElement[];

    for (const expectedInput of expectedInputs) {
      expect(allInputEls.filter(
        el => el.value === expectedInput
      )).toHaveLength(1);
    }

    for (const expectedRemovedInput of expectedRemovedInputs) {
      expect(allInputEls.filter(
        el => el.value === expectedRemovedInput
      )).toHaveLength(0);
    }

    expect(component.container).toMatchSnapshot();
  });
});