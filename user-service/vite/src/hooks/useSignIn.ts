import {useCallback, useState} from "react";
import {InvalidCredentialsError, userService} from "../services/userService.tsx";
import {useCookies} from "react-cookie";
import {useNavigate} from "react-router-dom";

export type SignInRequest = {
  email: string;
  password: string;
}

export const useSignIn = () => {
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");
  const [cookies] = useCookies(['XSRF-TOKEN']);
  const navigate = useNavigate();

  const signIn = useCallback(
    (signInReq: SignInRequest) => {
      setLoading(true);
      setSuccess("");
      setError("");

      userService.signIn({
        email: signInReq.email,
        password: signInReq.password,
        csrfToken: cookies["XSRF-TOKEN"] ?? ""
      })
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
            setError("Invalid username or password.");
          } else {
            setError("Unable to process the request. Try again later.");
            console.error("Unable to process the request.", error);
          }
        })
        .finally(() => {
          setLoading(false)
        });

    }, [setLoading, setSuccess, setError, cookies, userService, navigate, setTimeout, window.location.assign]
  )

  return {
    signIn,
    loading,
    error,
    success,
  }
};