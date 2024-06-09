"use client";

import React, {useState} from "react";

type Ingredient = {
  id: number;
  name: string;
  amount: number;
  unit: string;
};

const IngredientsInput = () => {
  const [newIngredient, setNewIngredient] = useState("");
  const [ingredients, setIngredients] = useState<Ingredient[]>([]);

  const addNewIngredient = () => {
    if (newIngredient.trim().length === 0) {
      return;
    }

    const newIngredients = [...ingredients];
    const newIngredientId = newIngredients.length + 1;

    const lastSpaceIndex = newIngredient.lastIndexOf(" ");
    if (lastSpaceIndex === -1 || newIngredient.slice(lastSpaceIndex).search(/ \d/g) !== 0) {
      newIngredients.push({
        id: newIngredientId,
        name: newIngredient,
        amount: 1,
        unit: "",
      });
    } else {
      const name = newIngredient.slice(0, lastSpaceIndex);
      const fullAmount = newIngredient.slice(lastSpaceIndex + 1);
      const unitIndex = fullAmount.search(/\D/g); // find non-digit character

      if (unitIndex === -1) {
        newIngredients.push({
          id: newIngredientId,
          name: name,
          amount: Number(fullAmount),
          unit: "",
        });
      } else {
        const amount = Number(fullAmount.slice(0, unitIndex));
        const unit = fullAmount.slice(unitIndex);

        newIngredients.push({
          id: newIngredientId,
          name: name,
          amount: amount,
          unit: unit,
        });
      }
    }

    setIngredients(newIngredients);
    setNewIngredient("");
  }

  const deleteIngredient = (id: number) => {
    setIngredients(ingredients.filter((ingredient) => ingredient.id !== id));
  };

  return (
    <div className="row mb-4">
      <label htmlFor="newIngredient" className="col-sm-2 col-form-label">Ingredients</label>
      <div className="col-sm-10">
        {ingredients.map(ingredient => (
          <div key={ingredient.id} className="row">
            <div className="input-group">
              <input
                name="ingredient_name" defaultValue={ingredient.name}
                className="form-control" placeholder="Name" style={{minWidth: "60%"}}
              />

              <input
                name="ingredient_amount" defaultValue={ingredient.amount}
                className="form-control" placeholder="Amount" style={{minWidth: "20%"}}
              />

              <input
                name="ingredient_unit" defaultValue={ingredient.unit}
                className="form-control" placeholder="Unit"
              />

              <button className="btn btn-outline-danger" type="button" onClick={() => deleteIngredient(ingredient.id)}>
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                     className="bi bi-dash-lg" viewBox="0 0 16 16">
                  <path fillRule="evenodd" d="M2 8a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11A.5.5 0 0 1 2 8"/>
                </svg>
              </button>
            </div>
          </div>
        ))}
        <div className="row">
          <div className="input-group">
            <input
              name="newIngredient" id="newIngredient" className="form-control"
              value={newIngredient}
              placeholder="Name and optionally amount (paprikas 3, sugar 250g, etc.)"
              onChange={(e) => setNewIngredient(e.currentTarget.value)}
              onKeyDown={(e) => e.key === 'Enter' && addNewIngredient()}
            />
            <button className="btn btn-success" type="button" onClick={() => addNewIngredient()}>
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                   className="bi bi-plus-lg" viewBox="0 0 16 16">
                <path fillRule="evenodd"
                      d="M8 2a.5.5 0 0 1 .5.5v5h5a.5.5 0 0 1 0 1h-5v5a.5.5 0 0 1-1 0v-5h-5a.5.5 0 0 1 0-1h5v-5A.5.5 0 0 1 8 2"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export { IngredientsInput };