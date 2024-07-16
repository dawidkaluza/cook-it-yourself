import {ReviewRecipe} from "@/app/my-recipes/[id]/_components/ReviewRecipe";

const Page = ({ params } : { params: { id: number }}) => {
  const { id } = params;

  return (
    <section className="container">
      <div className="row">
        <div className="col">
          <ReviewRecipe id={id} />
        </div>
      </div>
    </section>
  );
};

export default Page;