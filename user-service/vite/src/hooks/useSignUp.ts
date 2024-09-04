import {useCallback, useState} from "react";
import {useNavigate} from "react-router-dom";
import {userService} from "../domain/userService.ts";
import {SignUpRequest} from "../domain/dtos/user.ts";
import {InvalidFieldsError} from "../domain/errors/user.tsx";

export const  useSignUp = () => {
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");
  const [fieldsErrors, setFieldsErrors] = useState<Record<string, string>>({});
  const navigate = useNavigate();

  const signUp = useCallback((request: SignUpRequest) => {
    setLoading(true);
    setSuccess("");
    setError("");
    setFieldsErrors({});

    userService.signUp(request)
      .then(() => {
        setSuccess("Signed up successfully. You will be redirected to sign in page...");
        setTimeout(() => navigate("/sign-in"), 800);
      })
      .catch(error => {
        if (error instanceof InvalidFieldsError) {
          setFieldsErrors(error.fields);
        } else {
          setError("Unable to process the request. Try again later.");
          console.error("Unable to process the request.", error);
        }
      })
      .finally(() => {
        setLoading(false);
      });
  }, [setSuccess, setError, setLoading, setFieldsErrors, navigate]);

  return {
    signUp,
    loading,
    error,
    fieldsErrors,
    success
  }
};