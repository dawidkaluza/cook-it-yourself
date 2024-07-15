import {AddRecipeForm} from "@/app/my-recipes/add/_components/AddRecipeForm";

const AddRecipe = () => {
  return (
    <div className="container-fluid">
      <div className="row justify-content-center">
        <div className="col-12 col-sm-8 col-md-6">
          <h1>Add recipe</h1>
          <AddRecipeForm />
        </div>
      </div>
    </div>
  );
};

export {AddRecipe};