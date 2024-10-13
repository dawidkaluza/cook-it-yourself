import {FieldError} from "@/app/my-recipes/_dtos/errors";

type Props = {
  fieldErrors?: FieldError[];
};

const CookingTimeInput = ({ fieldErrors }: Props) => {
  return (
    <div className="row mb-4">
      <label htmlFor="cookingTime" className="col-md-3 col-form-label text-md-end">Cooking time</label>
      <div className="col-md-9">
        <input
          name="cookingTime"
          id="cookingTime"
          type="number"
          className="form-control"
          placeholder="In minutes"
        />
        {fieldErrors && fieldErrors.map(fieldError => {
          return (
            <div className="invalid-feedback d-block" key={fieldError.message}>
              {fieldError.message}
            </div>
          )
        })}
      </div>
    </div>
  );
};

export {CookingTimeInput};
