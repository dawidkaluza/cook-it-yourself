"use client"

import {useRouter, useSearchParams} from "next/navigation";
import {useEffect} from "react";
import {useAuth} from "@/app/_hooks/auth/useAuth";

export const SignIn = () => {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { signIn } = useAuth();

  useEffect(() => {
    if (searchParams.has('success')) {
      signIn();
    }

    router.push("/");
  }, []);
};
