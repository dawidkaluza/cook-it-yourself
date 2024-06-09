const DescriptionInput = () => {
  return (
    <div className="row mb-4">
      <label htmlFor="description" className="col-sm-2 col-form-label">Description</label>
      <div className="col-sm-10">
        <textarea
          name="description"
          id="description" className="form-control" placeholder="A few words describing your recipe"
          style={{height: "100px"}}
        />
      </div>
    </div>
  );
};

export { DescriptionInput };
