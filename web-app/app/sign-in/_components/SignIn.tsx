"use client";

import {useRouter, useSearchParams} from "next/navigation";
import {useEffect} from "react";
import {useAuth} from "@/app/_api/hooks";

export const SignIn = () => {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { signIn, signOut } = useAuth();

  useEffect(() => {
    if (searchParams.has('success')) {
      signIn();
      router.push("/");
    } else if (searchParams.has("sign-out") || searchParams.has("error")) {
      signOut();
      router.push("/");
    } else {
      router.push(process.env.NEXT_PUBLIC_API_GATEWAY_CLIENT_URL + "/oauth2/authorization/ciy");
    }
  }, []);

  return (
    <></>
  );
};
