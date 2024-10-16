import {getRecipe} from "@/app/my-recipes/actions";
import Link from "next/link";

const Page = async ({ params } : { params: { id: number }}) => {
  const { id } = params;
  const recipe = await getRecipe(id);

  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col-md-8 offset-md-2">
          <div>
            <h1>{recipe.name}</h1>
            <h6>{recipe.description}</h6>
            <ul className="list-unstyled">
              <li>Portion size: {Number(recipe.portionSize.value) + " " + recipe.portionSize.measure}</li>
              <li>Cooking time: {recipe.cookingTime} minutes</li>
            </ul>
          </div>

          <div className="mt-4">
            <h2>Ingredients</h2>
            <ul>
              {recipe.ingredients.map((ingredient) => (
                <li key={ingredient.id}>
                  {ingredient.name + " " + Number(ingredient.value) + " " + ingredient.measure}
                </li>
              ))}
            </ul>
          </div>

          <div className="mt-4">
            <h2>Method steps</h2>

            {recipe.methodSteps.map(step => (
              <p key={step.id}>{step.text}</p>
            ))}
          </div>
        </div>
      </div>
      <div className="row">
        <div className="col-md-8 offset-md-2">
          <hr/>
        </div>

        <div className="col-md-8 offset-md-2 d-flex justify-content-center justify-content-sm-start">
          <Link href={`/my-recipes/${recipe.id}/edit`} className="btn btn-primary mx-2">Modify</Link>
          <Link href={`/my-recipes/${recipe.id}/delete`} className="btn btn-danger mx-2">Delete</Link>
        </div>
      </div>
    </div>
  );
};

export default Page;