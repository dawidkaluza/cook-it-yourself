"use client"

import React, {useState} from "react";
import {FieldError} from "@/app/my-recipes/add/actions";

type Props = {
  fieldErrors?: FieldError[];
};

type MethodStep = {
  id: number;
  text: string;
};

const MethodStepsInput = ({ fieldErrors }: Props) => {
  const [newStep, setNewStep] = useState("");
  const [steps, setSteps] = useState<MethodStep[]>([]);
  const [generatedId, setGeneratedId] = useState(0);

  const generateNewId = () => {
    const newId = generatedId + 1;
    setGeneratedId(newId);
    return newId;
  };

  const addStep = () => {
    if (newStep.trim().length === 0) {
      return;
    }

    const newSteps = [...steps];
    newSteps.push({
      id: generateNewId(),
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
      <label htmlFor="newMethodStep" className="col-md-3 col-form-label text-md-end">Method steps</label>
      <div className="col-md-9">
        {steps.map(step => (
          <div key={step.id} className="row">
            <MethodStepFields step={step} onDelete={() => deleteStep(step.id)} />
          </div>
        ))}

        <div className="row">
          <NewMethodStepFields value={newStep} onChange={setNewStep} onSubmit={addStep} />
        </div>

        <div className="row">
          {fieldErrors && fieldErrors.map(fieldError => {
            return (
              <div key={fieldError.message} className="invalid-feedback d-block">
                {fieldError.message}
              </div>
            )
          })}
        </div>
      </div>
    </div>
  );
};

const MethodStepFields = (props: { step: MethodStep, onDelete: () => void }) => {
  const { step, onDelete } = props;
  const [ text, setText ] = useState(step.text);

  return (
    <div className="input-group">
      <textarea
        name="methodSteps"
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