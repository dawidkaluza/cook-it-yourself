import {ChangeEvent, FormEvent, useState} from "react";
import {TextField} from "../../components/TextField.tsx";
import {SubmitButton} from "../../components/SubmitButton.tsx";
import {Link} from "../../components/Link.tsx";
import {useSignIn} from "../../hooks/useSignIn.ts";
import {SignInRequest} from "../../domain/dtos/user.ts";

export const SignInPage = () => {
  const [fields, setFields] = useState<SignInRequest>({ email: "", password: "" });
  const { signIn, loading, success, error} = useSignIn();

  const onFieldChange = (event: ChangeEvent<HTMLInputElement>) => {
    const target = event.target;
    setFields({
      ...fields,
      [target.name]: target.value
    });
  }

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    signIn(fields);
  };

  return (
    <form
      className="flex flex-col items-center max-w-sm mx-auto mt-8"
      onSubmit={handleSubmit}
    >
      <h1
        className="mb-4 text-center text-2xl"
      >
        Sign in
      </h1>

      <TextField
        name="email"
        label="E-mail"
        value={fields.email}
        onChange={onFieldChange}
        style="w-full mb-3"
      />

      <TextField
        name="password"
        label="Password"
        type="password"
        value={fields.password}
        onChange={onFieldChange}
        style="w-full mb-3"
      />

      <SubmitButton loading={loading} style="mb-5">Sign in</SubmitButton>

      <p>
        Don't have an account? {" "}
        <Link to="/sign-up">
          Sign up.
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