"use server";

import {fetchFromServer} from "@/app/_api/fetch";
import {redirect} from "next/navigation";

export async function deleteRecipe(formData: FormData) {
  const id = formData.get("id");

  await fetchFromServer({
    endpoint: `/kitchen/recipe/${id}`,
    method: "DELETE"
  });

  redirect("/my-recipes");
}