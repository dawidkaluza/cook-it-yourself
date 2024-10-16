import {PortionSize} from "@/app/my-recipes/_dtos/recipe";

type Props = {
  errors?: string[];
  portionSize?: PortionSize;
};

const PortionSizeInput = ({ errors, portionSize }: Props) => {
  return (
    <div className="row mb-4">
      <label htmlFor="portionSize" className="col-md-3 col-form-label text-md-end">Portion size</label>
      <div className="col-md-9">
        <input
          name="portionSize"
          id="portionSize"
          className="form-control"
          placeholder="Amount and unit (e.g., 4 plates, 800g)"
          defaultValue={portionSize ? (Number(portionSize.value) + " " + portionSize.measure) : undefined}
        />
        {errors && errors.map(error => {
          return (
            <div className="invalid-feedback d-block" key={error}>
              {error}
            </div>
          )
        })}
      </div>
    </div>
  );
};

export { PortionSizeInput };