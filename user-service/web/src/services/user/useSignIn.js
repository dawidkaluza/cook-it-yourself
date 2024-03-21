import {useCallback, useState} from "react";
import {userService} from "./userService";
import {useCookies} from "react-cookie";
import {useNavigate} from "react-router-dom";

const useSignIn = () => {
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");
  const [fieldsErrors, setFieldsErrors] = useState({});
  const [cookies] = useCookies();
  const navigate = useNavigate();

  const signIn = useCallback(
    ({username, password}) => {
      setLoading(true);
      setSuccess("");
      setError("");
      setFieldsErrors({});

      // TODO what if cookie does not exist?
      const csrfToken = cookies['XSRF-TOKEN'];

      return userService
        .signIn({ username, password, csrfToken })
        .then(result => {
          if (!result.success) {
            const response = result.response;
            setError(response.message ?? "");
            setFieldsErrors(response.fields ?? {});
            return result;
          }

          setSuccess("Signing in proceeded successfully.");

          const response = result.response;
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
        .finally(() => {
          setLoading(false);
        });
    }, []
  );

  return { signIn, loading, success, error, fieldsErrors };
};

export { useSignIn };