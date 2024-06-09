const FormButtons = () => {
  return (
    <div className="row mb-4">
      <div className="col-12 text-center">
        <div className="btn-group">
          <button className="btn btn-primary" type="submit">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                 className="bi bi-check2" viewBox="0 0 16 16">
              <path
                d="M13.854 3.646a.5.5 0 0 1 0 .708l-7 7a.5.5 0 0 1-.708 0l-3.5-3.5a.5.5 0 1 1 .708-.708L6.5 10.293l6.646-6.647a.5.5 0 0 1 .708 0"/>
            </svg>

            {" Submit "}
          </button>
          <button className="btn btn-outline-secondary" type="reset">
            <svg
              xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
              className="bi bi-x" viewBox="0 0 16 16"
            >
              <path
                d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708"
              />
            </svg>
            {" Reset "}
          </button>
        </div>
      </div>
    </div>
  );
};

export {FormButtons};