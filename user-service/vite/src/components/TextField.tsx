export const TextField = (props: {
  name: string;
  type?: "text" | "password";
  label?: string;
  placeholder?: string;
  fullWidth?: boolean;
  style?: string;
}) => {
  const style: string = (props.style ?? "") + (props.fullWidth ? " w-full" : "");
  return (
    <div className={style}>
      <label htmlFor="email" className="block mb-1 font-medium">
        {props.label}
      </label>
      <input
        id={props.name}
        name={props.name}
        placeholder={props.placeholder}
        type={props.type}
        className="w-full py-2 px-3 rounded border shadow text-gray-700 outline-none focus:border-blue-400"
      />
    </div>
  );
};