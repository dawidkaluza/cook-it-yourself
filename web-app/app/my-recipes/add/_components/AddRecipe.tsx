import {NameInput} from "@/app/my-recipes/add/_components/NameInput";
import {DescriptionInput} from "@/app/my-recipes/add/_components/DescriptionInput";
import {IngredientsInput} from "@/app/my-recipes/add/_components/IngredientsInput";
import {MethodStepsInput} from "@/app/my-recipes/add/_components/MethodStepsInput";
import {CookingTimeInput} from "@/app/my-recipes/add/_components/CookingTimeInput";
import {PortionSizeInput} from "@/app/my-recipes/add/_components/PortionSizeInput";
import {FormButtons} from "@/app/my-recipes/add/_components/FormButtons";
import {addRecipe} from "@/app/my-recipes/add/actions";

const AddRecipe = () => {
  return (
    <div className="container-fluid">
      <div className="row justify-content-center">
        <div className="col-12 col-sm-8 col-md-6">
          <h1>Add recipe</h1>
          <form action={addRecipe} noValidate className="mt-4">
            <NameInput />
            <DescriptionInput />
            <IngredientsInput />
            <MethodStepsInput />
            <CookingTimeInput />
            <PortionSizeInput />
            <FormButtons />
          </form>
        </div>
      </div>
    </div>
  );
};

export {AddRecipe};