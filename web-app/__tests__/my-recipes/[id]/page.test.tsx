import {RecipeDetails} from "@/app/my-recipes/_dtos/recipe";
import {getRecipe} from "@/app/my-recipes/actions";
import {Mock} from "vitest";
import {render, screen} from "@testing-library/react";
import Page from "@/app/my-recipes/[id]/page";

vi.mock("@/app/my-recipes/actions", () => {
  return {
    getRecipe: vi.fn(),
  };
});

describe("page component", () => {
  test("renders given recipe", async () => {
    // Given
    const recipe : RecipeDetails = {
      id: 1,
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

    // When
    const reviewComponent = render(await Page({ params: { id: 1 } }));

    // Then
    const nameHeader = screen.getByRole("heading", { name: /Boiled sausages/ });
    expect(nameHeader).not.toBeNull();

    const descHeader = screen.getByRole("heading", { name: /How to boil delicious sausages/ });
    expect(descHeader).not.toBeNull();

    const portionSizeElement = screen.getByText("Portion size: " + Number(recipe.portionSize.value) + " " + recipe.portionSize.measure);
    expect(portionSizeElement).not.toBeNull();

    const cookingTimeElement = screen.getByText("Cooking time: " + recipe.cookingTime + " minutes");
    expect(cookingTimeElement).not.toBeNull();

    for(const ingredient of recipe.ingredients) {
      const ingredientElement = screen.getByText(
        ingredient.name + " " + Number(ingredient.value) + " " + ingredient.measure
      );

      expect(ingredientElement).not.toBeNull();
    }

    for(const step of recipe.methodSteps) {
      const stepElement = screen.getByText(step.text);
      expect(stepElement).not.toBeNull();
    }

    const modifyLink = screen.getByRole("link", { name: /modify/i });
    expect(modifyLink).not.toBeNull();
    expect(modifyLink?.getAttribute("href")).toBe("/my-recipes/1/edit");

    const deleteLink = screen.getByRole("link", { name: /delete/i });
    expect(deleteLink).not.toBeNull();
    expect(deleteLink?.getAttribute("href")).toBe("/my-recipes/1/delete");

    expect(reviewComponent.container).toMatchSnapshot();
  });
});