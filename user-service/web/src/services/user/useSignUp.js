import {useCallback, useState} from "react";
import {useCookies} from "react-cookie";
import {userService} from "./userService";
import {useNavigate} from "react-router-dom";

const useSignUp = () => {
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");
  const [fieldsErrors, setFieldsErrors] = useState({});
  const [cookies] = useCookies();
  const navigate = useNavigate();

  const signUp = useCallback(
    ({ email, password, name }) => {
      setLoading(true);
      setSuccess("");
      setError("");
      setFieldsErrors({});

      userService
        .signUp({ email, password, name })
        .then(result => {
          if (!result.success) {
            const response = result.response;
            setError(response.message ?? "");
            setFieldsErrors(response.fields ?? {});
            return;
          }

          setSuccess("Signing up proceeded successfully. You will be redirected to sign in page...");
          setTimeout(() => {
            navigate("/sign-in");
          }, 800);
        })
        .finally(() => {
          setLoading(false);
        })
    }, []
  );

  return { signUp, loading, success, error, fieldsErrors };
};

export { useSignUp }