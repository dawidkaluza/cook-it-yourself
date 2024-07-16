"use server";

import {RecipeDetails} from "@/app/my-recipes/_dtos/recipe";
import { fetchFromServer} from "@/app/_api/fetch";

export async function reviewRecipe(id: number) : Promise<RecipeDetails> {
  return await fetchFromServer<RecipeDetails>({
    endpoint: `/kitchen/recipe/${id}`
  }) as RecipeDetails;
}