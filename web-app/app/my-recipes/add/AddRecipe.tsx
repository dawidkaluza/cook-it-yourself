"use client"

const AddRecipe = () => {
  return (
    <div className="container-fluid">
      <div className="row justify-content-center">
        <div className="col-12 col-sm-8 col-md-6">
          <h1>Add recipe</h1>
          <form className="mt-4">
            <NameField />
            <DescriptionField />
            <IngredientsField />
            <MethodStepsField />
            <CookingTimeField />
            <PortionSizeField />
            <FormButtons />
          </form>
        </div>
      </div>
    </div>
  );
};

const NameField = () => {
  return (
    <div className="row mb-4">
      <label htmlFor="name" className="col-sm-2 col-form-label">Name</label>
      <div className="col-sm-10">
        <input id="name" className="form-control" placeholder="Name"/>
      </div>
    </div>
  );
};

const DescriptionField = () => {
  return (
    <div className="row mb-4">
      <label htmlFor="description" className="col-sm-2 col-form-label">Description</label>
      <div className="col-sm-10">
                <textarea id="description" className="form-control" placeholder="Description"
                          style={{height: "100px"}}/>
      </div>
    </div>
  );
};

const IngredientsField = () => {
  return (
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
  );
};

const MethodStepsField = () => {
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

const CookingTimeField = () => {
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

const PortionSizeField = () => {
  return (
    <div className="row mb-4">
      <label htmlFor="portionSize" className="col-sm-2 col-form-label">Portion size</label>
      <div className="col-sm-10">
        <input id="portiomSize" className="form-control" placeholder="Amount and unit (4 plates, 800g, etc.)"/>
      </div>
    </div>
  );
};

const FormButtons = () => {
  return (
    <div className="row mb-4">
      <div className="col-12 text-center">
        <div className="btn-group">
          <button className="btn btn-primary" type="button">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                 className="bi bi-check2" viewBox="0 0 16 16">
              <path
                d="M13.854 3.646a.5.5 0 0 1 0 .708l-7 7a.5.5 0 0 1-.708 0l-3.5-3.5a.5.5 0 1 1 .708-.708L6.5 10.293l6.646-6.647a.5.5 0 0 1 .708 0"/>
            </svg>

            {" Submit "}
          </button>
          <button className="btn btn-outline-secondary" type="button">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                 className="bi bi-backspace-reverse-fill" viewBox="0 0 16 16">
              <path
                d="M0 3a2 2 0 0 1 2-2h7.08a2 2 0 0 1 1.519.698l4.843 5.651a1 1 0 0 1 0 1.302L10.6 14.3a2 2 0 0 1-1.52.7H2a2 2 0 0 1-2-2zm9.854 2.854a.5.5 0 0 0-.708-.708L7 7.293 4.854 5.146a.5.5 0 1 0-.708.708L6.293 8l-2.147 2.146a.5.5 0 0 0 .708.708L7 8.707l2.146 2.147a.5.5 0 0 0 .708-.708L7.707 8z"/>
            </svg>
            {" Reset "}
          </button>
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