"use server";

import {Ingredient, PortionSize, Recipe, Step} from "@/app/my-recipes/add/_dtos/Recipe";
import {ApiError, fetchFromServer} from "@/app/_api/fetch";
import {number} from "prop-types";
import {redirect} from "next/navigation";

export type FieldError = {
  name: string;
  message: string;
};

export async function addRecipe(prevState: any, formData: FormData) {
  const mapToIngredients = (formData: FormData) => {
    const ingredientsNames = formData.getAll("ingredientName");
    const ingredientsAmounts = formData.getAll("ingredientAmount");
    const ingredientsUnits = formData.getAll("ingredientUnit");
    const ingredients = [] as Ingredient[];
    const ingredientsNum = ingredientsNames.length;
    for (let i = 0; i < ingredientsNum; i++) {
      const ingredient = {
        name: ingredientsNames[i],
        value: ingredientsAmounts[i],
        measure: ingredientsUnits[i],
      } as Ingredient;
      ingredients.push(ingredient);
    }
    return ingredients;
  };

  const mapToSteps = (formData: FormData) => {
    return formData.getAll("methodSteps").map(methodStep => {
      return {
        text: methodStep,
      } as Step;
    });
  };

  const mapToPortionSize = (formData: FormData) => {
    const portionSizeInput = formData?.get("portionSize")?.toString();
    if (!portionSizeInput) {
      return {
        value: "",
        measure: "",
      } as PortionSize;
    }

    const numberIndex = portionSizeInput.search(/\d/g);
    if (numberIndex === -1) {
      return {
        value: "",
        measure: "",
      } as PortionSize;
    }

    const nonNumberIndex = portionSizeInput.search(/\D/g);
    if (nonNumberIndex === -1 || nonNumberIndex < numberIndex) {
      return {
        value: portionSizeInput.slice(numberIndex),
        measure: "",
      } as PortionSize;
    }

    return {
      value: portionSizeInput.slice(numberIndex, nonNumberIndex).trim(),
      measure: portionSizeInput.slice(nonNumberIndex).trim()
    } as PortionSize;
  }

  let recipe = {
    name: formData.get("name"),
    description: formData.get("description"),
    ingredients: mapToIngredients(formData),
    methodSteps: mapToSteps(formData),
    cookingTime: Number(formData.get("cookingTime")),
    portionSize: mapToPortionSize(formData)
  } as Recipe;

  try {
    recipe = await fetchFromServer({
      endpoint: "/kitchen/recipe",
      method: "POST",
      body: recipe,
    }) as Recipe;
    redirect(`/my-recipes/${recipe.id}/`);
  } catch (error) {
    if (error instanceof ApiError) {
      const response = error.response;

      // TODO cover other possible client errors
      if (response.status === 422) {
        const body = await response.json();
        const fields = body.fields as Array<FieldError>
        return fields.map(field => {
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
}