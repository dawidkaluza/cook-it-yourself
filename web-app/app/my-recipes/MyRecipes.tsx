import { Suspense } from 'react'

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

const getData = () => {
  return new Promise((resolve) => {
    setTimeout(() => resolve({
      items: [
        {
          id: 1,
          name: "Boiled sausages",
          description: "Boileeeeed sausagesss"
        },
        {
          id: 2,
          name: "Spaghetti",
          description: "Spaghetti bolognese"
        }
      ],
      totalPages: 1
    }), 1000)
  })
};

const MyRecipesList = async () => {
  const recipes = await getData();
  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col">
          <h1>My recipes</h1>
        </div>
      </div>
      <div className="row row-cols-1 row-cols-md-2">
        {recipes.items.map((recipe) =>
          <div key={recipe.id} className="col" style={{ margin: "2em 0" }}>
            <RecipeCard recipe={recipe} />
          </div>
        )}
      </div>
    </div>
  );
};

const RecipeCard = (
  { recipe } : { recipe: { id: number; name: string; description: string } }
) => {
  return (
    <div className="card">
      <div className="card-body">
        <h5 className="card-title">{recipe.name}</h5>
        <p className="card-text">{recipe.description}</p>
        <a href="#" className="card-link">See more</a>
      </div>
    </div>
  );
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