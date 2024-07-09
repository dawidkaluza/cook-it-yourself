"use client";

import { useFormState } from "react-dom";
import {addRecipe} from "@/app/my-recipes/add/actions";

const NameInput = () => {
  const [fieldErrors, action] = useFormState(addRecipe, []);
  const nameErrors = fieldErrors.filter(error => error.name === "name");
  const errorMessages = nameErrors.map(field => field.message);
  const isValid = !errorMessages.length;

  return (
    <div className="row mb-4">
      <label htmlFor="name" className="col-sm-2 col-form-label">Name</label>
      <div className="col-sm-10">
        <input name="name" id="name" className={isValid ? "form-control" : "form-control is-invalid"} placeholder="Name of your new recipe"/>
        {errorMessages.map(message => {
          return (
            <div className="invalid-feedback">
              {message}
            </div>
          )
        })}
      </div>
    </div>
  );
};

export {NameInput};