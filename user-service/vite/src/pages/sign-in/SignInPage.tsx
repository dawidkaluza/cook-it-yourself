import {FormEvent} from "react";
import {TextField} from "../../components/TextField.tsx";
import {SubmitButton} from "../../components/SubmitButton.tsx";
import {Link} from "../../components/Link.tsx";
import {useSignIn} from "../../hooks/useSignIn.ts";

export const SignInPage = () => {
  const { signIn, loading, success, error} = useSignIn();

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    signIn();
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

      <TextField name="email" label="E-mail" fullWidth style="mb-3" />
      <TextField name="password" type="password" label="Password" fullWidth style="mb-3" />

      <SubmitButton loading={loading} style="mb-5">Sign in</SubmitButton>

      <p>
        Don't have an account? {" "}
        <Link to="/sign-up">
          Sign up.
        </Link>
      </p>

      {error &&
        <p className="text-red-600">
          Invalid e-mail or password.
        </p>
      }

      {success &&
        <p className="text-green-600">
          Signed in successfully.
        </p>
      }
    </form>
  );
};