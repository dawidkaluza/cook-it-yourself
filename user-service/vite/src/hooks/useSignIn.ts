import {useCallback, useState} from "react";
import {userService} from "../domain/userService.ts";
import {useNavigate} from "react-router-dom";
import {InvalidCredentialsError} from "../domain/errors/user.tsx";
import {SignInRequest} from "../domain/dtos/user.ts";

export const useSignIn = () => {
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const signIn = useCallback(
    (signInReq: SignInRequest) => {
      setLoading(true);
      setSuccess("");
      setError("");

      userService.signIn(signInReq)
        .then(response => {
          setSuccess("Signing in proceeded successfully.");

          if (response.external) {
            setTimeout(() => {
              window.location.assign(response.redirectUrl);
            }, 800);
          } else {
            setTimeout(() => {
              navigate(response.redirectUrl);
            }, 800);
          }
        })
        .catch(error => {
          if (error instanceof InvalidCredentialsError) {
            setError("Invalid email or password.");
          } else {
            setError("Unable to process the request. Try again later.");
            console.error("Unable to process the request.", error);
          }
        })
        .finally(() => {
          setLoading(false)
        });

    }, [setLoading, setSuccess, setError, userService, setTimeout, window.location.assign, navigate]
  )

  return {
    signIn,
    loading,
    error,
    success,
  }
};