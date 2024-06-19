
export type Recipe = {
  id: number | undefined;
  name: string;
  description: string;
  ingredients: Ingredient[];
  methodSteps: Step[];
  cookingTime: number;
  portionSize: PortionSize;
};

export type Ingredient = {
  id: number | undefined;
  name: string;
  value: string;
  measure: string;
};

export type Step = {
  id: number | undefined;
  text: string;
};

export type PortionSize = {
  value: string;
  measure: string;
};
