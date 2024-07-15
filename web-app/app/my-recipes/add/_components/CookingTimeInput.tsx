import {FieldError} from "@/app/my-recipes/add/actions";

type Props = {
  fieldErrors: FieldError[];
};

const CookingTimeInput = ({ fieldErrors } : Props) => {
  return (
    <div className="row mb-4">
      <label htmlFor="cookingTime" className="col-sm-2 col-form-label">Cooking time</label>
      <div className="col-sm-10">
        <input
          name="cookingTime"
          id="cookingTime"
          type="number"
          className={!fieldErrors.length ? "form-control" : "form-control is-invalid"}
          placeholder="In minutes"
        />
        {fieldErrors.map(fieldError => {
          return (
            <div className="invalid-feedback" key={fieldError.message}>
              {fieldError.message}
            </div>
          )
        })}
      </div>
    </div>
  );
};

export {CookingTimeInput};
