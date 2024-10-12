import {FieldError} from "@/app/my-recipes/add/actions";

type Props = {
  fieldErrors?: FieldError[];
};

const NameInput = ({ fieldErrors }: Props) => {
  return (
    <div className="row mb-4">
      <label htmlFor="name" className="col-md-3 col-form-label text-md-end">Name</label>
      <div className="col-md-9">
        <input
          name="name"
          id="name"
          className="form-control"
          placeholder="Name of your new recipe"
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

export {NameInput};