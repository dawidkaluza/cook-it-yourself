"use client"

import {useState} from "react";

type MethodStep = {
  id: number;
  text: string;
};

const MethodStepsInput = () => {
  const [newStep, setNewStep] = useState("");
  const [steps, setSteps] = useState<MethodStep[]>([]);

  const addStep = () => {
    if (newStep.trim().length === 0) {
      return;
    }

    const newSteps = [...steps];
    newSteps.push({
      id: steps.length + 1,
      text: newStep,
    });
    setSteps(newSteps);
    setNewStep("");
  };

  const deleteStep = (id: number) => {
    setSteps(
      steps.filter(step => step.id !== id)
    );
  };

  return (
    <div className="row mb-4">
      <label htmlFor="newMethodStep" className="col-sm-2 col-form-label">Method steps</label>
      <div className="col-sm-10">
        {steps.map(step => (
          <div key={step.id} className="row">
            <div className="input-group">
              <textarea
                name="methodSteps" className="form-control"
                placeholder="Method step"
                defaultValue={step.text}
                style={{height: "100px"}}
              />

              <div className="input-group-text">
                <button className="btn btn-sm btn-outline-danger" type="button" onClick={() => deleteStep(step.id)}>
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                       className="bi bi-dash-lg" viewBox="0 0 16 16">
                    <path fillRule="evenodd" d="M2 8a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11A.5.5 0 0 1 2 8"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>
        ))}

        <div className="row">
          <div className="input-group">
            <textarea
              name="newMethodStep" id="newMethodStep" className="form-control"
              value={newStep}
              placeholder="Method step"
              style={{height: "100px"}}
              onChange={(e) => setNewStep(e.currentTarget.value)}
              onKeyDown={(e) => e.key === 'Enter' && addStep()}
            />

            <div className="input-group-text">
              <button className="btn btn-sm btn-success" type="button" onClick={() => addStep()}>
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