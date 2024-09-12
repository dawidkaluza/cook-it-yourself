import {useCallback, useEffect, useState} from "react";

export const useAuth = (isAlreadySignedIn?: boolean) => {
  const [isSignedIn, setSignedIn] = useState(!!isAlreadySignedIn);
  const [name, setName] = useState<string | undefined>(undefined);

  useEffect(() => {
    if (!isAlreadySignedIn) {
      const name = localStorage.getItem("name");
      setSignedIn(name !== null);
      setName(name ?? undefined);
    }
  }, []);

  const signIn = useCallback((name?: string) => {
    setSignedIn(true);
    setName(name);
    localStorage.setItem("name", name ?? "");
  }, []);

  const signOut = useCallback(() => {
    localStorage.removeItem("name");
    setSignedIn(false);
    setName(undefined);
  }, []);

  return { isSignedIn, name, signIn, signOut };
};