const MethodStepsInput = () => {
  return (
    <div className="row mb-4">
      <label htmlFor="methodSteps" className="col-sm-2 col-form-label">Method steps</label>
      <div className="col-sm-10">
        <div className="row mb-3">
          <div className="input-group">
                    <textarea id="methodStep"
                              className="form-control"
                              placeholder="Method step"
                              style={{height: "100px"}}
                    />

            <div className="input-group-text">
              <button className="btn btn-sm btn-outline-danger" type="button">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                     className="bi bi-dash-lg" viewBox="0 0 16 16">
                  <path fillRule="evenodd" d="M2 8a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11A.5.5 0 0 1 2 8"/>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <div className="row mb-3">
          <div className="input-group">
                    <textarea id="methodStep"
                              className="form-control"
                              placeholder="Method step"
                              style={{height: "100px"}}
                    />

            <div className="input-group-text">
              <button className="btn btn-sm btn-success" type="button">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                     className="bi bi-plus-lg" viewBox="0 0 16 16">
                  <path fillRule="evenodd"
                        d="M8 2a.5.5 0 0 1 .5.5v5h5a.5.5 0 0 1 0 1h-5v5a.5.5 0 0 1-1 0v-5h-5a.5.5 0 0 1 0-1h5v-5A.5.5 0 0 1 8 2"/>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export { MethodStepsInput };