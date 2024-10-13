"use server";

import {ApiError, fetchFromServer} from "@/app/_api/fetch";
import {redirect} from "next/navigation";

export async function deleteRecipe(prevState: any, formData: FormData): Promise<string> {
  const id = formData.get("id");

  try {
    await fetchFromServer({
      endpoint: `/kitchen/recipe/${id}`,
      method: "DELETE"
    });
  } catch (error) {
    if (error instanceof ApiError) {
      const response = error.response;
      switch (response.status) {
        case 404: {
          return "The recipe is already deleted.";
        }

        case 422: {
          return "Couldn't process the request.";
        }
      }
    }

    throw error;
  }

  redirect("/my-recipes");
}