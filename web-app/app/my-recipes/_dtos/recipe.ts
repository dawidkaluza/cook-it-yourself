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