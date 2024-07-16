"use server";

import {Ingredient, PortionSize, Recipe, Step} from "@/app/my-recipes/add/_dtos/Recipe";
import {ApiError, fetchFromServer} from "@/app/_api/fetch";
import {redirect} from "next/navigation";

export type FieldError = {
  name: string;
  message: string;
};

export async function addRecipe(prevState: any, formData: FormData) : Promise<FieldError[]> {
  const mapToIngredients = (formData: FormData) : Ingredient[] => {
    const ingredientsNames  = formData.getAll("ingredientName") as string[];
    const ingredientsAmounts = formData.getAll("ingredientAmount") as string[];
    const ingredientsUnits = formData.getAll("ingredientUnit") as string[];
    const ingredients : Ingredient[] = [];
    const ingredientsNum = ingredientsNames.length;
    for (let i = 0; i < ingredientsNum; i++) {
      const ingredient = {
        name: ingredientsNames[i],
        value: ingredientsAmounts[i],
        measure: ingredientsUnits[i],
      };
      ingredients.push(ingredient);
    }
    return ingredients;
  };

  const mapToSteps = (formData: FormData) : Step[] => {
    return formData.getAll("methodSteps").map(methodStep => {
      return {
        text: methodStep as string,
      };
    });
  };

  const mapToPortionSize = (formData: FormData) : PortionSize => {
    const portionSizeInput = formData?.get("portionSize")?.toString();
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

  let recipe : Recipe = {
    name: formData.get("name") as string,
    description: formData.get("description") as string,
    ingredients: mapToIngredients(formData),
    methodSteps: mapToSteps(formData),
    cookingTime: Number(formData.get("cookingTime")),
    portionSize: mapToPortionSize(formData)
  };

  try {
    const createdRecipe = await fetchFromServer<Recipe>({
      endpoint: "/kitchen/recipe",
      method: "POST",
      body: recipe,
    });
    if (createdRecipe) {
      redirect(`/my-recipes/${recipe.id}/`);
    }
  } catch (error) {
    if (error instanceof ApiError) {
      const response = error.response;

      // TODO cover other possible client errors
      if (response.status === 422) {
        const body = await response.json();
        const fields : FieldError[] = body.fields;
        return fields.map((field) => {
          const name = field.name;
          const message = field.message;
          const indexOfDot = name.indexOf(".");
          if (indexOfDot === -1) {
            return {
              name,
              message
            }
          } else {
            const mainName = name.slice(0, indexOfDot);
            return {
              name: mainName,
              message
            };
          }
        });
      }

      return [];
    }

    throw error;
  }

  throw new Error("Couldn't process the request")
}