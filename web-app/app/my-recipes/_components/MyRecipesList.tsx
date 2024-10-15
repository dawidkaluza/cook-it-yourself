import {getRecipes} from "@/app/my-recipes/actions";
import Link from "next/link";
import {Recipe} from "@/app/my-recipes/_dtos/recipe";

export const MyRecipesList = async () => {
  const recipesResponse = await getRecipes();
  const recipes : Recipe[] = recipesResponse ? recipesResponse.items : [];

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

      <div className="row row-cols-1 row-cols-md-2 row-cols-xl-3">
        {recipes.map((recipe) =>
          <div key={recipe.id} className="col my-3">
            <RecipeCard recipe={recipe}/>
          </div>
        )}
      </div>

      <div className="row justify-content-end">
        <div className="col-12">
          <hr/>
        </div>
        <div className="col-12 d-flex justify-content-end">
          <AddRecipeButton />
        </div>
      </div>
    </div>
  );
};

const RecipeCard = (
  {recipe}: { recipe: Recipe }
) => {
  return (
    <article className="card h-100">
      <div className="card-body">
        <h5 className="card-title">{recipe.name}</h5>
        <p className="card-text">{recipe.description}</p>
        <Link href={`/my-recipes/${recipe.id}`} className="card-link">See more</Link>
      </div>
    </article>
  );
};

const AddRecipeButton = () => {
  // noinspection HtmlUnknownTarget
  return (
    <Link href="/my-recipes/add" className="btn btn-success">Add recipe</Link>
  );
};