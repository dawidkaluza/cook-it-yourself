import {FieldError} from "@/app/my-recipes/add/actions";

type Props = {
  fieldErrors: FieldError[];
};

const NameInput = ({ fieldErrors } : Props) => {
  const nameFieldErrors = fieldErrors.filter(error => error.name === "name");
  const isValid = !nameFieldErrors.length;

  return (
    <div className="row mb-4">
      <label htmlFor="name" className="col-sm-2 col-form-label">Name</label>
      <div className="col-sm-10">
        <input name="name" id="name" className={isValid ? "form-control" : "form-control is-invalid"} placeholder="Name of your new recipe"/>
        {nameFieldErrors.map(fieldError => {
          return (
            <div className="invalid-feedback" key={fieldError.name}>
              {fieldError.message}
            </div>
          )
        })}
      </div>
    </div>
  );
};

export {NameInput};