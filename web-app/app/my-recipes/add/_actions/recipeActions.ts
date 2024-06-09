"use server";

import {Recipe} from "@/app/my-recipes/add/_domain/Recipe";
import {cookies} from "next/headers";

export const addRecipe = async (formData: FormData) : Promise<Recipe> => {
  const requestBody = {
    name: formData.get("name"),
    description: formData.get("description"),
    ingredients: [],
    methodSteps: [],
    cookingTime: 0,
    portionSize: { value: "", measure: "" },
  };

  const cookieStore = cookies();
  const sessionId = cookieStore.get("JSESSIONID")?.value;
  const response = await fetch(
    process.env.API_GATEWAY_SERVER_URL + "/kitchen/recipe",
    {
      method: "POST",
      headers: {
        "Accept": "application/json",
        "Content-Type": "application/json",
        "Cookie": `JSESSIONID=${sessionId}`
      },
      body: JSON.stringify(requestBody),
    });

  return await response.json();
};
