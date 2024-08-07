"use server";

import {fetchFromServer} from "@/app/_api/fetch";
import {Page} from "@/app/my-recipes/_dtos/page";
import {Recipe} from "@/app/my-recipes/_dtos/recipe";

export async function getMyRecipes(){
  return await fetchFromServer<Page<Recipe>>({ endpoint: "/kitchen/recipe" });
}