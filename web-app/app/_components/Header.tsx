import React from "react";
import {isSignedIn} from "@/app/_api/auth";
import Link from "next/link";

const Header = () => {
  const signedIn = isSignedIn();

  return (
    <header className="navbar navbar-expand bg-body-tertiary">
      <div className="container-fluid text-center">
        <Link href="/" className="navbar-brand">Cook it yourself</Link>
        {signedIn
          ? <SignOutButton />
          : <SignInButton />
        }
      </div>
    </header>
  );
}

const SignInButton = () => {
  // noinspection HtmlUnknownTarget
  return (
    <a
      href="/sign-in"
      className="btn btn-primary btn-sm"
    >
      Sign in
    </a>
  );
};


const SignOutButton = () => {
  // noinspection HtmlUnknownTarget
  return (
    <a
      href="/sign-out"
      className="btn btn-primary btn-sm"
    >
      Sign out
    </a>
  );
};

export {Header};