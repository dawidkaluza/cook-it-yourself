import {reviewRecipe} from "@/app/my-recipes/[id]/actions";

type Props = {
  id: number;
};

const ReviewRecipe = async ({ id } : Props) => {
  const recipe = await reviewRecipe(id);

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
    </div>
  );
};

export {ReviewRecipe};