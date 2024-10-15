"use server";

import {ApiError, fetchFromServer} from "@/app/_api/fetch";
import {Page} from "@/app/my-recipes/_dtos/page";
import {
  NewIngredient, NewStep,
  PersistedIngredient, PersistedStep, PortionSize,
  Recipe,
  RecipeDetails,
  UpdateRecipeRequest
} from "@/app/my-recipes/_dtos/recipe";
import {FieldError} from "@/app/my-recipes/_dtos/errors";
import {redirect} from "next/navigation";

export async function getRecipes(){
  return await fetchFromServer<Page<Recipe>>({ endpoint: "/kitchen/recipe" });
}

export async function getRecipe(id: number) : Promise<RecipeDetails> {
  return await fetchFromServer<RecipeDetails>({
    endpoint: `/kitchen/recipe/${id}`
  }) as RecipeDetails;
}

export async function updateRecipe(prevState: any, formData: FormData): Promise<FieldError[]> {
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
    }) as RecipeDetails;
    redirect(`/my-recipes/${updatedRecipe.id}/`);
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

export async function deleteRecipe(prevState: any, formData: FormData): Promise<string> {
  const id = formData.get("id");

  try {
    await fetchFromServer({
      endpoint: `/kitchen/recipe/${id}`,
      method: "DELETE"
    });
  } catch (error) {
    if (error instanceof ApiError) {
      const response = error.response;
      switch (response.status) {
        case 404: {
          return "The recipe is already deleted.";
        }

        case 422: {
          return "Couldn't process the request.";
        }
      }
    }

    throw error;
  }

  redirect("/my-recipes");
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