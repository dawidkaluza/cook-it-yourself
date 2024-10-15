import React, {useState} from "react";
import {Ingredient} from "@/app/my-recipes/_dtos/recipe";

type IngredientComponentData = Ingredient & {
  key: number;
};

type IngredientsInputProps = {
  errors?: string[];
  ingredients: Ingredient[];
};

const IngredientsInput = (props: IngredientsInputProps) => {
  const initialIngredients = props.ingredients.map((ingredient, index) => {
    return {
      ...ingredient,
      value: Number(ingredient.value).toString(), //temporary workaround
      key: index + 1,
    };
  });

  const [newIngredient, setNewIngredient] = useState("");
  const [ingredients, setIngredients] = useState<IngredientComponentData[]>(initialIngredients);
  const [ingredientsToDelete, setIngredientsToDelete] = useState<number[]>([]);
  const [lastKey, setLastKey] = useState(initialIngredients.length);

  // TODO maybe extract it to a separate hook?
  const generateKey = () => {
    const generatedKey = lastKey + 1;
    setLastKey(generatedKey);
    return generatedKey;
  };

  const addNewIngredient = () => {
    if (newIngredient.trim().length === 0) {
      return;
    }

    const newIngredients = [...ingredients];

    const lastSpaceIndex = newIngredient.lastIndexOf(" ");
    if (lastSpaceIndex === -1 || newIngredient.slice(lastSpaceIndex).search(/ \d/g) !== 0) {
      newIngredients.push({
        name: newIngredient,
        value: "1",
        measure: "",
        key: generateKey(),
      });
    } else {
      const name = newIngredient.slice(0, lastSpaceIndex);
      const fullAmount = newIngredient.slice(lastSpaceIndex + 1);
      const unitIndex = fullAmount.search(/\D/g); // find non-digit character

      if (unitIndex === -1) {
        newIngredients.push({
          name: name,
          value: fullAmount,
          measure: "",
          key: generateKey(),
        });
      } else {
        const amount = fullAmount.slice(0, unitIndex);
        const unit = fullAmount.slice(unitIndex).trim();

        newIngredients.push({
          name: name,
          value: amount,
          measure: unit,
          key: generateKey(),
        });
      }
    }

    setIngredients(newIngredients);
    setNewIngredient("");
  }

  const updateIngredient = (ingredient: IngredientComponentData) => {
    const newIngredients = [ ...ingredients ];
    const index = newIngredients.findIndex(filteredIngredient => filteredIngredient.key === ingredient.key);
    newIngredients[index] = ingredient;
    setIngredients(newIngredients);
  };

  const deleteIngredient = (key: number) => {
    const index = ingredients.findIndex(ingredient => ingredient.key === key);
    const ingredient = ingredients[index];
    if (ingredient.id) {
      const newIngredientsToDelete = [ ...ingredientsToDelete ];
      newIngredientsToDelete.push(ingredient.id);
      setIngredientsToDelete(newIngredientsToDelete);
    }

    setIngredients(
      ingredients.slice(0, index).concat(ingredients.slice(index + 1))
    );
  };

  const errors = props.errors;
  return (
    <div className="row mb-4">
      <label htmlFor="newIngredient" className="col-md-3 col-form-label text-md-end">Ingredients</label>
      <div className="col-md-9">
        <div className="row">
          {ingredientsToDelete.map(ingredientId => (
            <input key={ingredientId.toString()}
                   type="hidden"
                   name="ingredientsToDelete"
                   value={ingredientId.toString()}
            />
          ))}
        </div>

        {ingredients.map(ingredient => (
          <div key={ingredient.key} className="row">
            <IngredientFields
              ingredient={ingredient}
              onUpdate={(ingredient) => updateIngredient(ingredient)}
              onDelete={() => deleteIngredient(ingredient.key)}
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
          {errors && errors.map(error => {
            return (
              <div key={error} className="invalid-feedback d-block">
                {error}
              </div>
            )
          })}
        </div>
      </div>
    </div>
  );
};

type IngredientFieldsProps = {
  ingredient: IngredientComponentData;
  onDelete: () => void;
  onUpdate: (ingredient: IngredientComponentData) => void;
};

const IngredientFields = (props: IngredientFieldsProps) => {
  const { ingredient, onDelete, onUpdate } = props;
  const { id } = ingredient;

  return (
    <div className="input-group">
      <input
        type="hidden"
        name="ingredientId"
        value={id}
      />

      <input
        name="ingredientName"
        value={ingredient.name}
        onChange={(event) => onUpdate({ ...ingredient, name: event.target.value })}
        className="form-control"
        style={{minWidth: "50%"}}
        placeholder="Name"
      />

      <input
        name="ingredientValue"
        value={ingredient.value}
        onChange={(event) => onUpdate({ ...ingredient, value: event.target.value })}
        className="form-control"
        style={{minWidth: "15%"}}
        placeholder="Amount"
      />

      <input
        name="ingredientMeasure"
        value={ingredient.measure}
        onChange={(event) => onUpdate({ ...ingredient, measure: event.target.value })}
        className="form-control"
        style={{minWidth: "15%"}}
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
        name="newIngredient" id="newIngredient"
        className="form-control"
        value={value}
        placeholder="e.g., water, apple 2, sugar 250g"
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