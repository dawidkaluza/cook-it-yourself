import {FieldError} from "@/app/my-recipes/add/actions";

type Props = {
  fieldErrors: FieldError[];
};

const NameInput = ({ fieldErrors } : Props) => {
  return (
    <div className="row mb-4">
      <label htmlFor="name" className="col-sm-2 col-form-label">Name</label>
      <div className="col-sm-10">
        <input
          name="name"
          id="name"
          className={!fieldErrors.length ? "form-control" : "form-control is-invalid"}
          placeholder="Name of your new recipe"
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

export {NameInput};