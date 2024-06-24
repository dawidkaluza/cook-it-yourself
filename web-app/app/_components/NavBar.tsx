import React from "react";
import Link from "next/link";
import {isSignedIn} from "@/app/_api/actions";
import {NavBarMenu} from "@/app/_components/NavBarMenu";

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
    <Link
      href="/sign-in"
      className="btn btn-primary btn-sm"
    >
      Sign in
    </Link>
  );
};

export {NavBar};