import React, {useState} from "react";
import {Step} from "@/app/my-recipes/_dtos/recipe";
import CryptoJS from 'crypto-js';

type Props = {
  errors?: string[];
  steps: Step[];
};

const MethodStepsInput = (props: Props) => {
  const [newStep, setNewStep] = useState("");
  const [steps, setSteps] = useState<Step[]>(props.steps);
  const [stepsToDelete, setStepsToDelete] = useState<Number[]>([]);

  const calcKey = (step: Step) => {
    return CryptoJS.MD5(step.text).toString();
  };

  const addStep = () => {
    if (newStep.trim().length === 0) {
      return;
    }

    const newSteps = [...steps];
    newSteps.push({ text: newStep });
    setSteps(newSteps);
    setNewStep("");
  };

  const deleteStep = (index: number) => {
    const step = steps[index];
    if (step.id) {
      const newStepsToDelete = [ ...stepsToDelete ];
      newStepsToDelete.push(step.id);
      setStepsToDelete(newStepsToDelete);
    }

    setSteps(
      steps.slice(0, index).concat(steps.slice(index + 1))
    );

    // TODO add shifting indexes down?
  };

  const errors = props.errors;
  return (
    <div className="row mb-4">
      <label htmlFor="newMethodStep" className="col-md-3 col-form-label text-md-end">Method steps</label>
      <div className="col-md-9">
        <div className="row">
          {stepsToDelete.map(stepId => (
            <input key={stepId.toString()}
                   type="hidden"
                   name="stepsToDelete"
                   value={stepId.toString()}
            />
          ))}
        </div>

        {steps.map((step, index) => (
          <div key={calcKey(step)} className="row">
            <MethodStepFields
              step={step}
              onDelete={() => deleteStep(index)}
            />
          </div>
        ))}

        <div className="row">
          <NewMethodStepFields value={newStep} onChange={setNewStep} onSubmit={addStep} />
        </div>

        <div className="row">
          {errors && errors.map(error => {
            return (
              <div key={error} className="invalid-feedback d-block">
                {error}
              </div>
            )
          })}
        </div>
      </div>
    </div>
  );
};

const MethodStepFields = (props: { step: Step, onDelete: () => void }) => {
  const { step, onDelete } = props;
  const { id } = step;
  const [ text, setText ] = useState(step.text);

  return (
    <div className="input-group">
      {id && (
        <input
          type="hidden"
          name="stepid"
          value={id}
        />
      )}

      <textarea
        name="methodStepText"
        className="form-control"
        placeholder="Method step"
        value={text}
        onChange={(event) => setText(event.target.value)}
        style={{height: "100px"}}
      />

      <div className="input-group-text">
        <button className="btn btn-sm btn-outline-danger" type="button" onClick={onDelete}>
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
               className="bi bi-dash-lg" viewBox="0 0 16 16">
            <path fillRule="evenodd" d="M2 8a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11A.5.5 0 0 1 2 8"/>
          </svg>
        </button>
      </div>
    </div>
  );
};

const NewMethodStepFields = (
  props: {
    value: string,
    onChange: (value: string) => void,
    onSubmit: () => void,
  }
) => {
  const {value, onChange, onSubmit} = props;
  return (
    <div className="input-group">
      <textarea
        name="newMethodStep" id="newMethodStep" className="form-control"
        value={value}
        placeholder="Instructions step-by-step to cook your recipe"
        style={{height: "100px"}}
        onChange={(e) => onChange(e.currentTarget.value)}
        onKeyDown={(e) => {
          if (e.key === 'Enter') {
            e.preventDefault();
            onSubmit();
          }
        }}
      />
      <div className="input-group-text">
        <button className="btn btn-sm btn-success" type="button" onClick={onSubmit}>
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
               className="bi bi-plus-lg" viewBox="0 0 16 16">
            <path fillRule="evenodd"
                  d="M8 2a.5.5 0 0 1 .5.5v5h5a.5.5 0 0 1 0 1h-5v5a.5.5 0 0 1-1 0v-5h-5a.5.5 0 0 1 0-1h5v-5A.5.5 0 0 1 8 2"/>
          </svg>
        </button>
      </div>
    </div>
  );
};

export {MethodStepsInput};