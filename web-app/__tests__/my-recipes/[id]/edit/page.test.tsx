import {useFormState} from "react-dom";
import {Mock} from "vitest";
import {render, screen} from "@testing-library/react";
import {getRecipe} from "@/app/my-recipes/actions";
import {RecipeDetails} from "@/app/my-recipes/_dtos/recipe";
import Page from "@/app/my-recipes/[id]/edit/page";

vi.mock("react-dom", () => {
  return {
    useFormState: vi.fn(),
  };
});

vi.mock("@/app/my-recipes/actions", () => {
  return {
    getRecipe: vi.fn(),
    updateRecipe: vi.fn(),
  };
});

describe("page component", () => {
  test("render", async () => {
    // Given
    const recipeId = 1;
    const recipe: RecipeDetails = {
      id: recipeId,
      name: "Boiled sausages",
      description: "How to boil delicious sausages",
      ingredients: [
        {
          id: 1,
          name: "Sausage",
          value: "3",
          measure: "pc",
        }
      ],
      methodSteps: [
        {
          id: 1,
          text: "Boil sausages for about 3 mins",
        }
      ],
      cookingTime: 3,
      portionSize: {
        value: "1",
        measure: "plate",
      }
    };
    (getRecipe as Mock).mockResolvedValue(recipe);

    (useFormState as Mock).mockReturnValue([
      [],
      () => {}
    ]);

    // When
    const formComponent = render(await Page({ params: { id: recipeId }}));

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

    const submitButton = screen.getByRole("button", { name: /submit/i });
    expect(submitButton).not.toBeNull();

    expect(formComponent.container).toMatchSnapshot();
  });

  test("render with errors", async () => {
    // Given
    const recipeId = 1;
    const recipe: RecipeDetails = {
      id: recipeId,
      name: "Boiled sausages",
      description: "How to boil delicious sausages",
      ingredients: [
        {
          id: 1,
          name: "Sausage",
          value: "3",
          measure: "pc",
        }
      ],
      methodSteps: [
        {
          id: 1,
          text: "Boil sausages for about 3 mins",
        }
      ],
      cookingTime: 3,
      portionSize: {
        value: "1",
        measure: "plate",
      }
    };
    (getRecipe as Mock).mockResolvedValue(recipe);

    const fieldErrors = [
      {
        name: "basicInformation.name",
        message: "Some name error.",
      },
      {
        name: "ingredients.ingredientsToAdd",
        message: "Some ingredients error.",
      }
    ];
    (useFormState as Mock).mockReturnValue([
      fieldErrors,
      () => {}
    ]);

    // When
    const formComponent = render(await Page({ params: { id: recipeId }}));

    // Then
    for (const fieldError of fieldErrors) {
      const errorMessage = screen.getByText(fieldError.message);
      expect(errorMessage).not.toBeNull();
    }
    expect(formComponent.container).toMatchSnapshot();
  });
});