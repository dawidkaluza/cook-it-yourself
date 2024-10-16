"use client";

import {RecipeDetails} from "@/app/my-recipes/_dtos/recipe";
import {useFormState} from "react-dom";
import {updateRecipe} from "@/app/my-recipes/actions";
import {NameInput} from "@/app/my-recipes/_components/NameInput";
import {DescriptionInput} from "@/app/my-recipes/_components/DescriptionInput";
import {IngredientsInput} from "@/app/my-recipes/_components/IngredientsInput";
import {MethodStepsInput} from "@/app/my-recipes/_components/MethodStepsInput";
import React from "react";
import {CookingTimeInput} from "@/app/my-recipes/_components/CookingTimeInput";
import {PortionSizeInput} from "@/app/my-recipes/_components/PortionSizeInput";
import Link from "next/link";

const EditRecipeForm = ({ recipe }: { recipe: RecipeDetails }) => {
  const [fieldErrors, action] = useFormState(updateRecipe, []);

  return (
    <form action={action} noValidate className="mt-4">
      <input type="hidden" name="id" value={recipe.id} />

      <NameInput
        name={recipe.name}
        errors={
          fieldErrors
            .filter(fieldError => fieldError.name == "basicInformation.name")
            .map(fieldError => fieldError.message)
        }
      />

      <DescriptionInput
        description={recipe.description}
        errors={
          fieldErrors
            .filter(fieldError => fieldError.name == "basicInformation.description")
            .map(fieldError => fieldError.message)
        }
      />

      <IngredientsInput
        ingredients={recipe.ingredients}
        errors={
          fieldErrors
            .filter(fieldError => fieldError.name.startsWith("ingredients"))
            .map(fieldError => fieldError.message)
        }
      />

      <MethodStepsInput
        steps={recipe.methodSteps}
        errors={
          fieldErrors
            .filter(fieldError => fieldError.name.startsWith("steps"))
            .map(fieldError => fieldError.message)
        }
      />

      <CookingTimeInput
        cookingTime={recipe.cookingTime}
        errors={
          fieldErrors
            .filter(fieldError => fieldError.name == "basicInformation.cookingTime")
            .map(fieldError => fieldError.message)
        }
      />

      <PortionSizeInput
        portionSize={recipe.portionSize}
        errors={
          fieldErrors
            .filter(fieldError => fieldError.name.startsWith("basicInformation.portionSize"))
            .map(fieldError => fieldError.message)
        }
      />

      <Buttons id={recipe.id as number} />
    </form>
  );
};

const Buttons = ({ id }: { id: number }) => {
  return (
    <div className="row mb-4">
      <div className="col-12 d-flex justify-content-center">
        <div className="btn-group">
          <button className="btn btn-primary" type="submit">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                 className="bi bi-check2" viewBox="0 0 16 16">
              <path
                d="M13.854 3.646a.5.5 0 0 1 0 .708l-7 7a.5.5 0 0 1-.708 0l-3.5-3.5a.5.5 0 1 1 .708-.708L6.5 10.293l6.646-6.647a.5.5 0 0 1 .708 0"/>
            </svg>

            {" Submit "}
          </button>

          <Link href={`/my-recipes/${id}`} className="btn btn-outline-secondary">
            Cancel
          </Link>
        </div>
      </div>
    </div>
  );
};

export {EditRecipeForm};