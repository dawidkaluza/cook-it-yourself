import React from "react";

export const SubmitButton = (props: {
  loading?: boolean,
  style?: string
  children: React.ReactNode;
}) => {
  return (
    <button
      type="submit"
      disabled={props.loading}
      className={
        "bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-6 rounded disabled:opacity-75 disabled:hover:bg-blue-500 "
        + (props.style ?? "")
      }
    >
      {props.children}
    </button>
  );
};