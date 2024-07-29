"use client";

import React, {useState} from "react";
import {FieldError} from "@/app/my-recipes/add/actions";

type Props = {
  fieldErrors?: FieldError[];
};

type Ingredient = {
  id: number;
  name: string;
  amount: number;
  unit: string;
};

const IngredientsInput = ({ fieldErrors }: Props) => {
  const [newIngredient, setNewIngredient] = useState("");
  const [ingredients, setIngredients] = useState<Ingredient[]>([]);
  const [generatedId, setGeneratedId] = useState(0);

  const generateNewId = () => {
    const newId = generatedId + 1;
    setGeneratedId(newId);
    return newId;
  }

  const addNewIngredient = () => {
    if (newIngredient.trim().length === 0) {
      return;
    }

    const newIngredients = [...ingredients];
    const newIngredientId = generateNewId();

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
            <IngredientFields
              ingredient={ingredient}
              onDelete={() => deleteIngredient(ingredient.id)}
            />
          </div>
        ))}
        <div className="row">
          <NewIngredientFields
            value={newIngredient}
            onChange={setNewIngredient}
            onSubmit={addNewIngredient}
          />
        </div>
        <div className="row">
          {fieldErrors && fieldErrors.map(fieldError => {
            return (
              <div key={fieldError.message} className="invalid-feedback d-block">
                {fieldError.message}
              </div>
            )
          })}
        </div>
      </div>
    </div>
  );
};

const IngredientFields = (props: { ingredient: Ingredient, onDelete: () => void }) => {
  const { ingredient, onDelete } = props;
  const [ name, setName ] = useState(ingredient.name);
  const [ amount, setAmount ] = useState(ingredient.amount.toString());
  const [ unit, setUnit ] = useState(ingredient.unit);

  return (
    <div className="input-group">
      <input
        name="ingredientName"
        value={name}
        onChange={(event) => setName(event.target.value)}
        className="form-control"
        placeholder="Name" style={{minWidth: "60%"}}
      />

      <input
        name="ingredientAmount"
        value={amount}
        onChange={(event) => setAmount(event.target.value)}
        className="form-control"
        placeholder="Amount" style={{minWidth: "20%"}}
      />

      <input
        name="ingredientUnit"
        value={unit}
        onChange={(event) => setUnit(event.target.value)}
        className="form-control"
        placeholder="Unit"
      />

      <button className="btn btn-outline-danger" type="button" onClick={onDelete}>
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
           className="bi bi-dash-lg" viewBox="0 0 16 16">
          <path fillRule="evenodd" d="M2 8a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11A.5.5 0 0 1 2 8"/>
        </svg>
      </button>
    </div>
  );
};

const NewIngredientFields = (
  props: {
    value: string,
    onChange: (value: string) => void,
    onSubmit: () => void,
  }
) => {
  const { value, onChange, onSubmit } = props;
  return (
    <div className="input-group">
      <input
        name="newIngredient" id="newIngredient" className="form-control"
        value={value}
        placeholder="Name and optionally amount (paprikas 3, sugar 250g, etc.)"
        onChange={(e) => onChange(e.currentTarget.value)}
        onKeyDown={(e) => {
          if (e.key === 'Enter') {
            e.preventDefault();
            onSubmit();
          }
        }}
      />
      <button className="btn btn-success" type="button" onClick={onSubmit}>
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
             className="bi bi-plus-lg" viewBox="0 0 16 16">
          <path fillRule="evenodd"
                d="M8 2a.5.5 0 0 1 .5.5v5h5a.5.5 0 0 1 0 1h-5v5a.5.5 0 0 1-1 0v-5h-5a.5.5 0 0 1 0-1h5v-5A.5.5 0 0 1 8 2"/>
        </svg>
      </button>
    </div>
  );
};

export {IngredientsInput};