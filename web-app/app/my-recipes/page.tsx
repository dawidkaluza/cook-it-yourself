import {Suspense} from "react";
import {MyRecipesList} from "@/app/my-recipes/_components/MyRecipesList";

const Page = () => {
  return (
    <Suspense fallback={<MyRecipesSkeleton />}>
      <MyRecipesList />
    </Suspense>
  );
};

const MyRecipesSkeleton = () => {
  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col">
          <h1>My recipes</h1>
        </div>
      </div>
      <div className="row row-cols-1 row-cols-md-2 row-cols-xl-3">
        <div className="col my-3">
          <RecipeCardSkeleton/>
        </div>
        <div className="col my-3">
          <RecipeCardSkeleton/>
        </div>
      </div>
    </div>
  )
};

const RecipeCardSkeleton = () => {
  return (
    <article className="card h-100">
      <div className="card-body">
        <h5 className="card-title placeholder-wave">
          <span className="placeholder col-3"></span>
        </h5>
        <p className="card-text placeholder-wave">
          <span className="placeholder col-8"></span>
        </p>
        <a href="#" className="card-link placeholder-wave">
          <span className="placeholder col-1"></span>
        </a>
      </div>
    </article>
  );
};

export default Page;