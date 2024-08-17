import {ChangeEventHandler, useId} from "react";

export const TextField = (props: {
  name: string;
  label: string;
  type?: "text" | "password";
  value?: string;
  onChange?: ChangeEventHandler<HTMLInputElement>;
  placeholder?: string;
  style?: string;
}) => {
  const id = useId();
  return (
    <div className={props.style}>
      <label htmlFor={props.name + "_" + id} className="block mb-1 font-medium">
        {props.label}
      </label>
      <input
        id={props.name + "_" + id}
        name={props.name}
        type={props.type}
        value={props.value}
        onChange={props.onChange}
        placeholder={props.placeholder}
        className="w-full py-2 px-3 rounded border shadow text-gray-700 outline-none focus:border-blue-400"
      />
    </div>
  );
};