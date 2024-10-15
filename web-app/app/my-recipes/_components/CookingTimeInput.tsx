type Props = {
  errors?: string[];
  cookingTime?: number;
};

const CookingTimeInput = ({ errors, cookingTime }: Props) => {
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
          defaultValue={cookingTime}
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

export {CookingTimeInput};