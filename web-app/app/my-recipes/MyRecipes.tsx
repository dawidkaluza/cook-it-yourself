"use client"

import {Suspense, useEffect, useState} from 'react'

type PageResponse<T> = {
  items: [T];
  totalPages: number;
};

type Recipe = {
  id: number;
  name: string;
  description: string;
};

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

const getData = async () : Promise<PageResponse<Recipe>> => {
  const response = await fetch("http://ciy.localhost:8080/api/kitchen/recipe", {
    method: "GET",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      "Accept": "application/json",
    }
  });

  return await response.json();
};

const MyRecipesList = () => {
  const [recipes, setRecipes] = useState<Recipe[]>([]);

  useEffect(() => {
    async function loadData() {
      const initialRecipes = await getData();
      setRecipes(initialRecipes.items);
    }

    loadData();
  }, []);

  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col">
          <h1>My recipes</h1>
        </div>
      </div>
      <div className="row row-cols-1 row-cols-md-2">
        {recipes.map((recipe) =>
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