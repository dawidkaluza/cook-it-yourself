import {FieldError} from "@/app/my-recipes/add/actions";

type Props = {
  fieldErrors?: FieldError[];
};

const DescriptionInput = ({ fieldErrors }: Props) => {
  return (
    <div className="row mb-4">
      <label htmlFor="description" className="col-sm-2 col-form-label">Description</label>
      <div className="col-sm-10">
        <textarea
          name="description"
          id="description"
          className="form-control"
          placeholder="A few words describing your recipe"
          style={{height: "100px"}}
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

export { DescriptionInput };
