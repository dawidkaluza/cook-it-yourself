"use client"

import React from "react";
import Link from "next/link";
import {useAuth} from "@/hooks/auth/useAuth";

const NavBar = () => {
  const { isSignedIn } = useAuth();

  return (
    <nav className="navbar navbar-expand bg-body-tertiary">
      <div className="container-fluid text-center">
        <Link href="/" className="navbar-brand">Cook it yourself</Link>
        {isSignedIn
          ?
            <div>Hello!</div>
          :
            <a
              href={process.env.API_GATEWAY_CLIENT_URL + "/oauth2/authorization/ciy"}
              className="btn btn-primary btn-sm"
            >
              Sign in
            </a>
        }
      </div>
    </nav>
  );
};

export { NavBar };