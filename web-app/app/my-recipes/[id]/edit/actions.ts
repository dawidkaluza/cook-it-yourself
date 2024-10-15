"use server";

import {FieldError} from "@/app/my-recipes/_dtos/errors";
import {ApiError, fetchFromServer} from "@/app/_api/fetch";
import {PortionSize, RecipeDetails, UpdateRecipeRequest} from "@/app/my-recipes/_dtos/recipe";
import {redirect} from "next/navigation";

async function editRecipe(prevState: any, formData: FormData): Promise<FieldError[]> {
  const id = formData.get("id") as string;

  const mapToPortionSize = (formData: FormData): PortionSize => {
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

  const requestData: UpdateRecipeRequest = {
    basicInformation: {
      name: formData.get("name") as string,
      description: formData.get("description") as string,
      cookingTime: Number(formData.get("cookingTime")),
      portionSize: mapToPortionSize(formData),
    },
    ingredients: {
      ingredientsToAdd: [],
      ingredientsToUpdate: [],
      ingredientsToDelete: [],
    },
    steps: {
      stepsToAdd: [],
      stepsToUpdate: [],
      stepsToDelete: [],
    }
  };

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

  throw new Error("Couldn't process the request");
}

export { editRecipe };