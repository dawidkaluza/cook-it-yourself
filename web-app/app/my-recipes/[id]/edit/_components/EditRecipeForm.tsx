"use client";

import {RecipeDetails} from "@/app/my-recipes/_dtos/recipe";
import {useFormState} from "react-dom";
import {editRecipe} from "@/app/my-recipes/[id]/edit/actions";
import {NameInput} from "@/app/my-recipes/[id]/edit/_components/NameInput";
import {DescriptionInput} from "@/app/my-recipes/[id]/edit/_components/DescriptionInput";
import {IngredientsInput} from "@/app/my-recipes/[id]/edit/_components/IngredientsInput";
import {MethodStepsInput} from "@/app/my-recipes/[id]/edit/_components/MethodStepsInput";

const EditRecipeForm = ({ recipe }: { recipe: RecipeDetails }) => {
  const [fieldErrors, action] = useFormState(editRecipe, []);

  return (
    <form action={action} noValidate className="mt-4">
      <NameInput
        defaultValue={recipe.name}
        errors={
          fieldErrors
            .filter(fieldError => fieldError.name.startsWith("name"))
            .map(fieldError => fieldError.message)
        }
      />

      <DescriptionInput
        defaultValue={recipe.description}
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
    </form>
  );
};

export {EditRecipeForm};