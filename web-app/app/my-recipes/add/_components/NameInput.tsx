const NameInput = () => {
  return (
    <div className="row mb-4">
      <label htmlFor="name" className="col-sm-2 col-form-label">Name</label>
      <div className="col-sm-10">
        <input name="name" id="name" className="form-control" placeholder="Name of your new recipe"/>
      </div>
    </div>
  );
};

export { NameInput };