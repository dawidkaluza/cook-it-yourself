import {MyRecipes} from "@/app/my-recipes/MyRecipes";

const Page = () => {
  return (
    <section className="container">
      <div className="row">
        <div className="col">
          <MyRecipes />
        </div>
      </div>
    </section>

  );
};

export default Page;