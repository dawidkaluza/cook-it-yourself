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
      const name = searchParams.get("nickname") ?? undefined;
      signIn(name);
      router.push("/");
    } else if (searchParams.has("sign-out") || searchParams.has("error")) {
      signOut();
      router.push("/");
    }
  }, []);

  return (
    <></>
  );
};
