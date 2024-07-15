"use client";

import {addRecipe} from "@/app/my-recipes/add/actions";
import {NameInput} from "@/app/my-recipes/add/_components/NameInput";
import {DescriptionInput} from "@/app/my-recipes/add/_components/DescriptionInput";
import {IngredientsInput} from "@/app/my-recipes/add/_components/IngredientsInput";
import {MethodStepsInput} from "@/app/my-recipes/add/_components/MethodStepsInput";
import {CookingTimeInput} from "@/app/my-recipes/add/_components/CookingTimeInput";
import {PortionSizeInput} from "@/app/my-recipes/add/_components/PortionSizeInput";
import {FormButtons} from "@/app/my-recipes/add/_components/FormButtons";
import {useFormState} from "react-dom";

const AddRecipeForm = () => {
  const [fieldErrors, action] = useFormState(addRecipe, []);

  return (
    <form action={action} noValidate className="mt-4">
      <NameInput fieldErrors={fieldErrors.filter(error => error.name.startsWith("name"))} />
      <DescriptionInput fieldErrors={fieldErrors.filter(error => error.name.startsWith("description"))} />
      <IngredientsInput fieldErrors={fieldErrors.filter(error => error.name.startsWith("ingredient"))} />
      <MethodStepsInput fieldErrors={fieldErrors.filter(error => error.name.startsWith("methodStep"))} />
      <CookingTimeInput fieldErrors={fieldErrors.filter(error => error.name.startsWith("cookingTime"))} />
      <PortionSizeInput fieldErrors={fieldErrors.filter(error => error.name.startsWith("portionSize"))} />
      <FormButtons/>
    </form>
  );
};

export {AddRecipeForm};