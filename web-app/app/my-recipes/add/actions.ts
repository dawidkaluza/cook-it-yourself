"use server";

import {Recipe} from "@/app/my-recipes/add/_dtos/Recipe";
import {fetchFromServer} from "@/app/_api/fetch";

export async function addRecipe(formData: FormData) {
  const recipe = {

  } as Recipe;
  
  return await fetchFromServer({
    endpoint: "/kitchen/recipe",
    method: "POST",
    body: recipe,
  }) as Promise<Recipe>;
}