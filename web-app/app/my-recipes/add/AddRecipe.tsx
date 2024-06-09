import {NameInput} from "@/app/my-recipes/add/_components/NameInput";
import {DescriptionInput} from "@/app/my-recipes/add/_components/DescriptionInput";
import {IngredientsInput} from "@/app/my-recipes/add/_components/IngredientsInput";
import {MethodStepsInput} from "@/app/my-recipes/add/_components/MethodStepsInput";
import {CookingTimeInput} from "@/app/my-recipes/add/_components/CookingTimeInput";
import {PortionSizeInput} from "@/app/my-recipes/add/_components/PortionSizeInput";
import {FormButtons} from "@/app/my-recipes/add/_components/FormButtons";

const AddRecipe = () => {
  return (
    <div className="container-fluid">
      <div className="row justify-content-center">
        <div className="col-12 col-sm-8 col-md-6">
          <h1>Add recipe</h1>
          <form className="mt-4">
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


/*
POST to:
record AddRecipeRequest(
    String name, String description,
    List<Ingredient> ingredients, List<Step> methodSteps,
    Long cookingTime,
    PortionSize portionSize
) {
    record Ingredient(String name, BigDecimal value, String measure) { }

    record Step(String text) { }

    record PortionSize(BigDecimal value, String measure) { }
}
 */

export {AddRecipe};