"use client"

const AddRecipe = () => {
  return (
    <div className="container-fluid">
      <div className="row justify-content-center">
        <div className="col-12 col-sm-8 col-md-6">
          <h1>Add recipe</h1>
          <form className="mt-4">

            <div className="row mb-4">
              <label htmlFor="name" className="col-sm-2 col-form-label">Name</label>
              <div className="col-sm-10">
                <input id="name" className="form-control" placeholder="Name"/>
              </div>
            </div>

            <div className="row mb-4">
              <label htmlFor="description" className="col-sm-2 col-form-label">Description</label>
              <div className="col-sm-10">
                <textarea id="description" className="form-control" placeholder="Description"
                          style={{height: "100px"}}/>
              </div>
            </div>

            <div className="row mb-4">
              <label htmlFor="ingredient" className="col-sm-2 col-form-label">Ingredients</label>
              <div className="col-sm-10">
                <div className="row">
                  <div className="input-group">
                    <input type="text" className="form-control" placeholder="Name" style={{minWidth: "60%"}}/>
                    <input type="text" className="form-control" placeholder="Amount" style={{minWidth: "20%"}}/>
                    <input type="text" className="form-control" placeholder="Unit"/>
                    <button className="btn btn-outline-danger" type="button">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                           className="bi bi-dash-lg" viewBox="0 0 16 16">
                        <path fillRule="evenodd" d="M2 8a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11A.5.5 0 0 1 2 8"/>
                      </svg>
                    </button>
                  </div>
                </div>
                <div className="row mt-3">
                  <div className="input-group">
                    <input id="ingredient" type="text" className="form-control"
                           placeholder="Name and optionally amount (paprikas 3, sugar 250g, etc.)"/>
                    <button className="btn btn-success" type="button">
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
                      <button className="btn btn-outline-danger" type="button">
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
                      <button className="btn btn-success" type="button">
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

            <div className="row mb-4">
              <label htmlFor="portionSize" className="col-sm-2 col-form-label">Portion size</label>
              <div className="col-sm-10">
                <input id="portiomSize" className="form-control" placeholder="Amount and unit (4 plates, 800g, etc.)"/>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

/*
POST to:
record AddRecipeRequest(
    String name, String description,
    List<Ingredient> ingredients, List<Step> methodSteps,
    Long cookingTime,
    PortionSize portionSize
) {
    record Ingredient(String name, BigDecimal value, String measure) { }

    record Step(String text) { }

    record PortionSize(BigDecimal value, String measure) { }
}
 */

export {AddRecipe};