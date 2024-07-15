import {FieldError} from "@/app/my-recipes/add/actions";

type Props = {
  fieldErrors: FieldError[];
};

const PortionSizeInput = ({ fieldErrors } : Props) => {
  return (
    <div className="row mb-4">
      <label htmlFor="portionSize" className="col-sm-2 col-form-label">Portion size</label>
      <div className="col-sm-10">
        <input
          name="portionSize"
          id="portiomSize"
          className="form-control"
          placeholder="Amount and unit (4 plates, 800g, etc.)"
        />
        {fieldErrors.map(fieldError => {
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

export { PortionSizeInput };