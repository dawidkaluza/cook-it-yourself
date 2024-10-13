"use client";

import {deleteRecipe} from "@/app/my-recipes/[id]/delete/actions";
import Link from "next/link";
import {useFormState} from "react-dom";

const DeleteRecipeForm = ({ id } : { id: number }) => {
  let [error, action] = useFormState(deleteRecipe, "");

  return (
    <form action={action} noValidate>
      <input type="hidden" name="id" value={id} />
      <p>
        Are you sure that you want to delete the recipe?
      </p>

      <div className="my-4 d-flex justify-content-center btn-group">
        <div className="btn-group">
          <button className="btn btn-danger" type="submit">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-x"
                 viewBox="0 0 16 16">
              <path
                d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708"/>
            </svg>

            {" Delete "}
          </button>
          <Link href={`/my-recipes/${id}`} className="btn btn-outline-secondary">
            Cancel
          </Link>
        </div>
      </div>

      {error && (
        <p className="text-danger">
          {error}
        </p>
      )}
    </form>
  );
}

export {DeleteRecipeForm};