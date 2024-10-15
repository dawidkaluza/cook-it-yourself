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

export type Step = {
  id?: number;
  text: string;
};

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
    ingredientsToAdd: (Ingredient & { id: undefined })[],
    ingredientsToUpdate: (Ingredient & { id: number })[],
    ingredientsToDelete: number[],
  };
  steps?: {
    stepsToAdd: (Step & { id: undefined })[],
    stepsToUpdate: (Step & { id: number })[],
    stepsToDelete: number[],
  };
};