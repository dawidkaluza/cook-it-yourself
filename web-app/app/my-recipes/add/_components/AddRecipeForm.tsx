"use client";

import {useFormState} from "react-dom";
import {addRecipe} from "@/app/my-recipes/actions";
import {NameInput} from "@/app/my-recipes/_components/NameInput";
import {DescriptionInput} from "@/app/my-recipes/_components/DescriptionInput";
import {IngredientsInput} from "@/app/my-recipes/_components/IngredientsInput";
import {MethodStepsInput} from "@/app/my-recipes/_components/MethodStepsInput";
import {CookingTimeInput} from "@/app/my-recipes/_components/CookingTimeInput";
import {PortionSizeInput} from "@/app/my-recipes/_components/PortionSizeInput";
import Link from "next/link";

const AddRecipeForm = () => {
  const [fieldErrors, action] = useFormState(addRecipe, []);

  const errorsStartingWith = (name: string) => {
    return fieldErrors
      .filter(error => error.name.startsWith(name))
      .map(fieldError => fieldError.message);
  };

  return (
    <form action={action} noValidate className="mt-4">
      <NameInput errors={errorsStartingWith("name")} />
      <DescriptionInput errors={errorsStartingWith("description")} />
      <IngredientsInput errors={errorsStartingWith("ingredient")} />
      <MethodStepsInput errors={errorsStartingWith("methodStep")} />
      <CookingTimeInput errors={errorsStartingWith("cookingTime")} />
      <PortionSizeInput errors={errorsStartingWith("portionSize")} />
      <Buttons />
    </form>
  );
};

const Buttons = () => {
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

          <Link href="/my-recipes" className="btn btn-outline-secondary">
            Cancel
          </Link>
        </div>
      </div>
    </div>
  );
};

export {AddRecipeForm};