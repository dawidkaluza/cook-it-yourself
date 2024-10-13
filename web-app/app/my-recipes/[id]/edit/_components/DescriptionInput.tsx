type Props = {
  errors?: string[];
  defaultValue?: string;
};

const DescriptionInput = ({ errors, defaultValue }: Props) => {
  return (
    <div className="row mb-4">
      <label htmlFor="description" className="col-md-3 col-form-label text-md-end">Description</label>
      <div className="col-md-9">
        <textarea
          name="description"
          id="description"
          className="form-control"
          defaultValue={defaultValue}
          placeholder="A few words describing your recipe"
          style={{height: "100px"}}
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

export { DescriptionInput };