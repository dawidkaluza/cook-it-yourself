import React from "react";

export const SubmitButton = (props: {
  loading?: boolean,
  style?: string
  children: React.ReactNode;
}) => {
  // TODO implement loading state

  return (
    <button
      type="submit"
      className={"bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-6 rounded " + (props.style ?? "")}
    >
      {props.children}
    </button>
  );
};