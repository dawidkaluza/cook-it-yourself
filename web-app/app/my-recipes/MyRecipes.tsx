import {Suspense} from 'react'
import Link from "next/link";
import {fetchApi} from "@/app/_api/fetchApi";
import {cookies} from "next/headers";

type PageResponse<T> = {
  items: [T];
  totalPages: number;
};

type Recipe = {
  id: number;
  name: string;
  description: string;
};

export const MyRecipes = () => {
  return (
    <Suspense fallback={<MyRecipesSkeleton />}>
      <MyRecipesList />
    </Suspense>
  )
};

const MyRecipesSkeleton = () => {
  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col">
          <h1>My recipes</h1>
        </div>
      </div>
      <div className="row row-cols-1 row-cols-md-2">
        <div className="col" style={{margin: "2em 0"}}>
          <RecipeCardSkeleton/>
        </div>
        <div className="col" style={{margin: "2em 0"}}>
          <RecipeCardSkeleton/>
        </div>
      </div>
    </div>
  )
};

const getMyRecipes = async () : Promise<PageResponse<Recipe>> => {
  const cookieStore = cookies();
  return await fetchApi({
    endpoint: "/kitchen/recipe",
    headers: {
      "Cookie": cookieStore.toString()
    }
  }) as PageResponse<Recipe>;
};

const MyRecipesList = async () => {
  const recipesResponse = await getMyRecipes();
  const recipes = recipesResponse.items;

  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col">
          <h1>My recipes</h1>
        </div>
      </div>

      {!recipes.length &&
        <div className="row">
          <div className="col">
            <p className="text-center">No recipes found.</p>
          </div>
        </div>
      }

      <div className="row row-cols-1 row-cols-md-2">
        {recipes.map((recipe) =>
          <div key={recipe.id} className="col" style={{margin: "2em 0"}}>
            <RecipeCard recipe={recipe}/>
          </div>
        )}
      </div>

      <div className="row justify-content-end">
        <div className="col-12">
          <hr/>
        </div>
        <div className="col-3">
          <AddRecipeButton />
        </div>
      </div>
    </div>
  );
};

const RecipeCard = (
  {recipe}: { recipe: { id: number; name: string; description: string } }
) => {
  return (
    <div className="card">
      <div className="card-body">
        <h5 className="card-title">{recipe.name}</h5>
        <p className="card-text">{recipe.description}</p>
        <a href="#" className="card-link">See more</a>
      </div>
    </div>
  );
};

const RecipeCardSkeleton = () => {
  return (
    <div className="card">
      <div className="card-body">
        <h5 className="card-title placeholder-wave">
          <span className="placeholder col-3"></span>
        </h5>
        <p className="card-text placeholder-wave">
          <span className="placeholder col-8"></span>
        </p>
        <a href="#" className="card-link placeholder-wave">
          <span className="placeholder col-1"></span>
        </a>
      </div>
    </div>
  );
};

const AddRecipeButton = () => {
  // noinspection HtmlUnknownTarget
  return (
    <Link href="/my-recipes/add" className="btn btn-success">Add recipe</Link>
  );
};