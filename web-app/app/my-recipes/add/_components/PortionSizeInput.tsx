const PortionSizeInput = () => {
  return (
    <div className="row mb-4">
      <label htmlFor="portionSize" className="col-sm-2 col-form-label">Portion size</label>
      <div className="col-sm-10">
        <input name="portionSize" id="portiomSize" className="form-control" placeholder="Amount and unit (4 plates, 800g, etc.)"/>
      </div>
    </div>
  );
};

export { PortionSizeInput };