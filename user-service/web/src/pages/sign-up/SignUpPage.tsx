import {TextField} from "../../components/TextField.tsx";
import {SubmitButton} from "../../components/SubmitButton.tsx";
import {Link} from "../../components/Link.tsx";
import {useSignUp} from "../../hooks/useSignUp.ts";
import {ChangeEvent, FormEvent, useState} from "react";

export const SignUpPage = () => {
  const [ fields, setFields ] = useState({ email: "", name: "", password: "" });
  const [ changedFields, setChangedFields ] = useState(new Set<string>());
  const { signUp, loading, error, fieldsErrors, success } = useSignUp();

  const onFieldChange = (event: ChangeEvent<HTMLInputElement>) => {
    const target = event.target;
    setFields({
      ...fields,
      [target.name]: target.value
    });
    const newChangedFields = new Set<string>(changedFields);
    newChangedFields.add(target.name);
    setChangedFields(newChangedFields);
  };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    signUp(fields);
    setChangedFields(new Set<string>());
  };

  return (
    <form
      className="flex flex-col items-center max-w-sm mx-auto mt-8"
      onSubmit={handleSubmit}
    >
      <h1
        className="mb-4 text-center text-2xl"
      >
        Sign up
      </h1>

      <TextField
        name="email"
        label="E-mail"
        value={fields.email}
        error={changedFields.has("email") ? undefined : fieldsErrors.email}
        onChange={onFieldChange}
        style="w-full mb-3 px-8"
      />

      <TextField
        name="name"
        label="Name"
        value={fields.name}
        error={changedFields.has("name") ? undefined : fieldsErrors.name}
        onChange={onFieldChange}
        style="w-full mb-3 px-8"
      />

      <TextField
        name="password"
        label="Password"
        type="password"
        value={fields.password}
        error={changedFields.has("password") ? undefined : fieldsErrors.password}
        onChange={onFieldChange}
        style="w-full mb-3 px-8"
      />

      <SubmitButton loading={loading} style="mb-5">Sign up</SubmitButton>

      <p>
        Already signed up? {" "}
        <Link to="/sign-in">
          Sign in.
        </Link>
      </p>

      {error &&
        <p className="text-red-600">
          {error}
        </p>
      }

      {success &&
        <p className="text-green-600">
          {success}
        </p>
      }
    </form>
  );
};