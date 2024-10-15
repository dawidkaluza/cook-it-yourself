import {EditRecipeForm} from "@/app/my-recipes/[id]/edit/_components/EditRecipeForm";
import {getRecipe} from "@/app/my-recipes/actions";

const Page = async ({ params } : { params: { id: number }}) => {
  const { id } = params;
  const recipe = await getRecipe(id);

  return (
    <div className="container-fluid">
      <div className="row justify-content-center">
        <div className="col-12 col-md-10 col-lg-8 col-xl-6">
          <h1>Edit the recipe</h1>
          <EditRecipeForm recipe={recipe} />
        </div>
      </div>
    </div>
  );
};

export default Page;