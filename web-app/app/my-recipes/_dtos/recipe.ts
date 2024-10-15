export type Recipe = {
  id?: number;
  name: string;
  description: string;
};

export type Ingredient = {
  id?: number;
  name: string;
  value: string;
  measure: string;
};

export type NewIngredient = Omit<Ingredient, "id">;
export type PersistedIngredient = Ingredient & { id: number };

export type Step = {
  id?: number;
  text: string;
};

export type NewStep = Omit<Step, "id">;
export type PersistedStep = Step & { id: number };

export type PortionSize = {
  value: string;
  measure: string;
};

export type RecipeDetails = Recipe & {
  ingredients: Ingredient[];
  methodSteps: Step[];
  cookingTime: number;
  portionSize: PortionSize;
};

export type UpdateRecipeRequest = {
  basicInformation?: {
    name: string;
    description: string;
    cookingTime: number;
    portionSize: PortionSize;
  };
  ingredients?: {
    ingredientsToAdd: NewIngredient[],
    ingredientsToUpdate: PersistedIngredient[],
    ingredientsToDelete: number[],
  };
  steps?: {
    stepsToAdd: NewStep[],
    stepsToUpdate: PersistedStep[],
    stepsToDelete: number[],
  };
};