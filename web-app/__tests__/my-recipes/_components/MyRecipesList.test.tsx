import {Mock} from "vitest";
import {getRecipes} from "@/app/my-recipes/actions";
import {Recipe} from "@/app/my-recipes/_dtos/recipe";
import {Page} from "@/app/my-recipes/_dtos/page";
import {render, screen} from "@testing-library/react";
import {MyRecipesList} from "@/app/my-recipes/_components/MyRecipesList";

vi.mock("@/app/my-recipes/actions", () => {
  return {
    getRecipes: vi.fn(),
  }
});

describe("MyRecipesList component", () => {
  test("renders with no recipes", async () => {
    // Given
    (getRecipes as Mock).mockResolvedValue(
      {
        items: [] as Recipe[],
        totalPages: 1,
      } as Page<Recipe>
    );

    // When
    const recipesComponent = render(await MyRecipesList());

    // Then
    const p = screen.getByText(/No recipes found/);
    expect(p).not.toBeNull();

    expect(recipesComponent.container).toMatchSnapshot();
  });

  test("renders with my recipes", async () => {
    // Given
    (getRecipes as Mock).mockResolvedValue(
      {
        items: [
          {
            id: 1,
            name: "Boiled sausages",
            description: "How to boil delicious sausages",
          }
        ] as Recipe[],
        totalPages: 1,
      } as Page<Recipe>
    );

    // When
    const recipesComponent = render(await MyRecipesList());

    // Then
    const noRecipesText = screen.queryByText(/No recipes found/);
    expect(noRecipesText).toBeNull();

    const cardTitle = screen.getByRole("heading", { name: /Boiled sausages/ });
    expect(cardTitle).not.toBeNull();
    expect(cardTitle?.getAttribute("class")).toBe("card-title");

    const cardText = screen.getByText(/How to boil delicious sausages/);
    expect(cardText).not.toBeNull();
    expect(cardText?.getAttribute("class")).toBe("card-text");

    const cardLink = screen.getByRole("link", { name: /See more/ });
    expect(cardLink).not.toBeNull();
    expect(cardLink?.getAttribute("href")).toBe("/my-recipes/1");

    expect(recipesComponent.container).toMatchSnapshot();
  });
});