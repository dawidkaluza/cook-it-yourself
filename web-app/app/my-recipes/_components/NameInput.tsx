type Props = {
  errors?: string[];
  name?: string;
};

const NameInput = ({ errors, name }: Props) => {
  return (
    <div className="row mb-4">
      <label htmlFor="name" className="col-md-3 col-form-label text-md-end">Name</label>
      <div className="col-md-9">
        <input
          name="name"
          id="name"
          className="form-control"
          placeholder="Name of your recipe"
          defaultValue={name}
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

export {NameInput};