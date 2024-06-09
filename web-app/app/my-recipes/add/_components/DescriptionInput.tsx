const DescriptionInput = () => {
  return (
    <div className="row mb-4">
      <label htmlFor="description" className="col-sm-2 col-form-label">Description</label>
      <div className="col-sm-10">
        <textarea
          name="description"
          id="description" className="form-control" placeholder="Description"
          style={{height: "100px"}}
        />
      </div>
    </div>
  );
};

export { DescriptionInput };
