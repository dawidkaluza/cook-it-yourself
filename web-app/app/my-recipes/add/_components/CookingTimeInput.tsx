const CookingTimeInput = () => {
  return (
    <div className="row mb-4">
      <label htmlFor="cookingTime" className="col-sm-2 col-form-label">Cooking time</label>
      <div className="col-sm-10">
        <input name="cookingTime" id="cookingTime" type="number" className="form-control" placeholder="In minutes"/>
      </div>
    </div>
  );
};

export {CookingTimeInput};
