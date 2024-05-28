"use server"

type Recipes = {
  items: [
    id: number,
    name: string,
    description: string
  ]
};

export const getMyRecipes = async () : Promise<Recipes> => {

};
