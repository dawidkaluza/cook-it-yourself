import {FieldError} from "@/app/my-recipes/add/actions";

type Props = {
  fieldErrors?: FieldError[];
};

const PortionSizeInput = ({ fieldErrors }: Props) => {
  return (
    <div className="row mb-4">
      <label htmlFor="portionSize" className="col-md-3 col-form-label text-md-end">Portion size</label>
      <div className="col-md-9">
        <input
          name="portionSize"
          id="portionSize"
          className="form-control"
          placeholder="Amount and unit (e.g., 4 plates, 800g)"
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

export { PortionSizeInput };