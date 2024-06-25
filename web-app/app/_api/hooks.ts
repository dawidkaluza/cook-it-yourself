import {useCallback, useEffect, useState} from "react";

export const useAuth = (isAlreadySignedIn? : boolean) => {
  const [isSignedIn, setSignedIn] = useState(!!isAlreadySignedIn);

  useEffect(() => {
    if (!isAlreadySignedIn) {
      setSignedIn(sessionStorage.getItem("isSignedIn") === "true");
    }
  }, []);

  const signIn = useCallback(() => {
    sessionStorage.setItem("isSignedIn", "true");
    setSignedIn(true);
  }, []);

  const signOut = useCallback(() => {
    sessionStorage.removeItem("isSignedIn");
    setSignedIn(false);
  }, []);

  return { isSignedIn: isSignedIn, signIn, signOut };
};