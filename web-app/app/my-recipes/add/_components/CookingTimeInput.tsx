const CookingTimeInput = () => {
  return (
    <div className="row mb-4">
      <label htmlFor="cookingTimeHours" className="col-sm-2 col-form-label">Cooking time</label>
      <div className="col-sm-10">
        <div className="input-group">
          <input id="cookingTimeHours" type="number" className="form-control"/>
          <span className="input-group-text">Hours</span>
          <input id="cookingTimeMinutes" type="number" className="form-control"/>
          <span className="input-group-text">Minutes</span>
        </div>
      </div>
    </div>
  );
};

export {CookingTimeInput};
