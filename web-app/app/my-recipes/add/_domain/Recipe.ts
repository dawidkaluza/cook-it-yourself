
type Recipe = {
  id: number | undefined;
  name: string;
  description: string;
  ingredients: Ingredient[];
  methodSteps: Step[];
  cookingTime: number;
  portionSize: PortionSize;
};

type Ingredient = {
  id: number | undefined;
  name: string;
  value: string;
  measure: string;
};

type Step = {
  id: number | undefined;
  text: string;
};

type PortionSize = {
  value: string;
  measure: string;
};

export { Recipe, Ingredient, Step, PortionSize };