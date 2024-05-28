"use client"

import {useCallback, useEffect, useState} from "react";
import Cookies from 'js-cookie'

export const useAuth = () => {
  const [hasSession, setHasSession] = useState(false);

  useEffect(() => {
    setHasSession(!!Cookies.get("clientSession"));
  }, []);

  const signIn = useCallback(() => {
    Cookies.set("clientSession", "true");
    setHasSession(true);
  }, []);

  const signOut = useCallback(() => {
    Cookies.remove("clientSession");
    setHasSession(true);
  }, []);

  return { isSignedIn: hasSession, signIn, signOut };
};