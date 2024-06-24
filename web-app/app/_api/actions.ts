"use server"

import {cookies} from "next/headers";

export function isSignedIn(){
  const cookieStore = cookies();
  const sessionCookie = process.env.NEXT_PUBLIC_API_GATEWAY_SESSION_COOKIE;
  return sessionCookie ? cookieStore.has(sessionCookie) : false;
}