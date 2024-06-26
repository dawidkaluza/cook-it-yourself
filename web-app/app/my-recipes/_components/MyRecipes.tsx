import {Suspense} from 'react'
import {MyRecipesList} from "@/app/my-recipes/_components/MyRecipesList";

export const MyRecipes = () => {
  return (
    <Suspense fallback={<MyRecipesSkeleton />}>
      <MyRecipesList />
    </Suspense>
  )
};

const MyRecipesSkeleton = () => {
  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col">
          <h1>My recipes</h1>
        </div>
      </div>
      <div className="row row-cols-1 row-cols-md-2">
        <div className="col" style={{margin: "2em 0"}}>
          <RecipeCardSkeleton/>
        </div>
        <div className="col" style={{margin: "2em 0"}}>
          <RecipeCardSkeleton/>
        </div>
      </div>
    </div>
  )
};

const RecipeCardSkeleton = () => {
  return (
    <div className="card">
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
    </div>
  );
};