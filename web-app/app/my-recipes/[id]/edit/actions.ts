"use server";

import {FieldError} from "@/app/my-recipes/_dtos/errors";
import {ApiError, fetchFromServer} from "@/app/_api/fetch";
import {
  NewIngredient, NewStep,
  PersistedIngredient, PersistedStep,
  PortionSize,
  RecipeDetails,
  UpdateRecipeRequest
} from "@/app/my-recipes/_dtos/recipe";
import {redirect} from "next/navigation";

async function editRecipe(prevState: any, formData: FormData): Promise<FieldError[]> {
  const requestData: UpdateRecipeRequest = {
    basicInformation: buildBasicInformation(formData),
    ingredients: buildIngredients(formData),
    steps: buildSteps(formData),
  };

  const id = formData.get("id") as string;
  try {
    const updatedRecipe = await fetchFromServer<RecipeDetails>({
      endpoint: `/kitchen/recipe/${id}`,
      method: "PUT",
      body: requestData
    });

    if (updatedRecipe) {
      redirect(`/my-recipes/${updatedRecipe.id}/`);
    }
  } catch (error) {
    if (error instanceof ApiError) {
      const response = error.response;

      // TODO cover other possible errors
      if (response.status === 422) {
        const body = await response.json();
        return body.fields;
      }

      return [];
    }

    throw error;
  }

  throw new Error("Couldn't process the request");
}

function buildBasicInformation(formData: FormData) {
  return {
    name: formData.get("name") as string,
    description: formData.get("description") as string,
    cookingTime: Number(formData.get("cookingTime")),
    portionSize: toPortionSize(formData.get("portionSize")?.toString()),
  };
}

function buildIngredients(formData: FormData) {
  const { ingredientsToAdd, ingredientsToUpdate } = filterIngredients(
    formData.getAll("ingredientId") as string[],
    formData.getAll("ingredientName") as string[],
    formData.getAll("ingredientValue") as string[],
    formData.getAll("ingredientMeasure") as string[],
  );
  const ingredientsToDelete = (formData.getAll("ingredientsToDelete") as string[]).map(id => Number(id));

  return {
    ingredientsToAdd,
    ingredientsToUpdate,
    ingredientsToDelete
  };
}

function buildSteps(formData: FormData) {
  const { stepsToAdd, stepsToUpdate } = filterSteps(
    formData.getAll("methodStepId") as string[],
    formData.getAll("methodStepText") as string[],
  );
  const stepsToDelete = (formData.getAll("stepsToDelete") as string[]).map(id => Number(id));

  return {
    stepsToAdd,
    stepsToUpdate,
    stepsToDelete
  };
}

function filterIngredients(ids: string[], names: string[], values: string[], measures: string[]) {
  const ingredientsToAdd: NewIngredient[] = [];
  const ingredientsToUpdate: PersistedIngredient[] = [];

  const ingredientsNum = ids.length;
  for (let i = 0; i < ingredientsNum; i++) {
    const id = ids[i];
    if (id) {
      ingredientsToUpdate.push({
        id: Number(id),
        name: names[i],
        value: values[i],
        measure: measures[i],
      });
    } else {
      ingredientsToAdd.push({
        name: names[i],
        value: values[i],
        measure: measures[i],
      });
    }
  }

  return { ingredientsToAdd, ingredientsToUpdate };
}

function filterSteps(ids: string[], texts: string[]) {
  const stepsToAdd: NewStep[] = [];
  const stepsToUpdate: PersistedStep[] = [];

  const stepsNum = ids.length;
  for (let i = 0; i < stepsNum; i++) {
    const id = ids[i];
    if (id) {
      stepsToUpdate.push({
        id: Number(id),
        text: texts[i],
      });
    } else {
      stepsToAdd.push({
        text: texts[i],
      });
    }
  }

  return { stepsToAdd, stepsToUpdate };
}

function toPortionSize(portionSizeInput: string | undefined): PortionSize {
  if (!portionSizeInput) {
    return {
      value: "",
      measure: "",
    };
  }

  const numberIndex = portionSizeInput.search(/\d/g);
  if (numberIndex === -1) {
    return {
      value: "",
      measure: "",
    };
  }

  const nonNumberIndex = portionSizeInput.search(/\D/g);
  if (nonNumberIndex === -1 || nonNumberIndex < numberIndex) {
    return {
      value: portionSizeInput.slice(numberIndex),
      measure: "",
    };
  }

  return {
    value: portionSizeInput.slice(numberIndex, nonNumberIndex).trim(),
    measure: portionSizeInput.slice(nonNumberIndex).trim()
  };
}

export { editRecipe };