"use client";

import {RecipeDetails} from "@/app/my-recipes/_dtos/recipe";
import {useFormState} from "react-dom";
import {editRecipe} from "@/app/my-recipes/[id]/edit/actions";
import {NameInput} from "@/app/my-recipes/[id]/edit/_components/NameInput";
import {DescriptionInput} from "@/app/my-recipes/[id]/edit/_components/DescriptionInput";
import {IngredientsInput} from "@/app/my-recipes/[id]/edit/_components/IngredientsInput";
import {MethodStepsInput} from "@/app/my-recipes/[id]/edit/_components/MethodStepsInput";
import {CookingTimeInput} from "@/app/my-recipes/[id]/edit/_components/CookingTimeInput";
import {PortionSizeInput} from "@/app/my-recipes/[id]/edit/_components/PortionSizeInput";

const EditRecipeForm = ({ recipe }: { recipe: RecipeDetails }) => {
  const [fieldErrors, action] = useFormState(editRecipe, []);

  return (
    <form action={action} noValidate className="mt-4">
      <NameInput
        name={recipe.name}
        errors={
          fieldErrors
            .filter(fieldError => fieldError.name.startsWith("name"))
            .map(fieldError => fieldError.message)
        }
      />

      <DescriptionInput
        description={recipe.description}
        errors={
          fieldErrors
            .filter(fieldError => fieldError.name.startsWith("name"))
            .map(fieldError => fieldError.message)
        }
      />

      <IngredientsInput
        ingredients={recipe.ingredients}
        errors={
          fieldErrors
            .filter(fieldError => fieldError.name.startsWith("ingredient"))
            .map(fieldError => fieldError.message)
        }
      />

      <MethodStepsInput
        steps={recipe.methodSteps}
        errors={
          fieldErrors
            .filter(fieldError => fieldError.name.startsWith("methodStep"))
            .map(fieldError => fieldError.message)
        }
      />

      <CookingTimeInput
        cookingTime={recipe.cookingTime}
        errors={
          fieldErrors
            .filter(fieldError => fieldError.name.startsWith("cookingTime"))
            .map(fieldError => fieldError.message)
        }
      />

      <PortionSizeInput
        portionSize={recipe.portionSize}
        errors={
          fieldErrors
            .filter(fieldError => fieldError.name.startsWith("portionSize"))
            .map(fieldError => fieldError.message)
        }
      />
    </form>
  );
};

export {EditRecipeForm};