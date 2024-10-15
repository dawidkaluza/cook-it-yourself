import {AddRecipeForm} from "@/app/my-recipes/add/_components/AddRecipeForm";

const Page = () => {
  return (
    <div className="container-fluid">
      <div className="row justify-content-center">
        <div className="col-12 col-md-10 col-lg-8 col-xl-6">
          <h1>Add recipe</h1>
          <AddRecipeForm/>
        </div>
      </div>
    </div>
  );
};

export default Page;