import {useCallback, useState} from "react";
import {userService} from "./userService";

const useConsent = () => {
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");

  const consent = useCallback(
    ({ clientId, state, scopes }) => {
      setLoading(false);
      setSuccess("");
      setError("");

      userService
        .consent({ clientId, state, scopes })
        .then(result => {
          if (!result.success) {
            const response = result.response;
            setError(response.message ?? "");
            return;
          }

          setSuccess("Consent approved successfully.");

          const redirectUrl = result.response.redirectUrl;
          if (redirectUrl) {
            setTimeout(() => {
              window.location.assign(redirectUrl);
            }, 800);
          }
        })
        .finally(() => {
          setLoading(false);
        });

    }, []
  );

  return { consent, loading, success, error };
};

export { useConsent };