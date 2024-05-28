"use client"

import {useRouter, useSearchParams} from "next/navigation";
import {useAuth} from "@/hooks/auth/useAuth";
import {useEffect} from "react";

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
