import React from "react";
import Link from "next/link";
import {NavBarMenu} from "@/app/_components/NavBarMenu";
import {isSignedIn} from "@/app/_api/auth";

const NavBar = () => {
  const signedIn = isSignedIn();

  return (
    <nav className="navbar navbar-expand bg-body-tertiary">
      <div className="container-fluid text-center">
        <Link href="/" className="navbar-brand">Cook it yourself</Link>
        {signedIn ? <NavBarMenu /> : <SignInButton />}
      </div>
    </nav>
  );
};

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

export {NavBar};