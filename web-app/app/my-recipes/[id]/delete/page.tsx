import {DeleteRecipeForm} from "@/app/my-recipes/[id]/delete/_components/DeleteRecipeForm";

const Page = ({ params } : { params: { id: number }}) => {
  const { id } = params;

  return (
    <div className="container-fluid">
      <div className="row justify-content-center">
        <div className="col-12 text-center">
          <h1>Delete the recipe</h1>
          <DeleteRecipeForm id={id} />
        </div>
      </div>
    </div>
  );
};

export default Page;