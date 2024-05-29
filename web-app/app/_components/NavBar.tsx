"use client"

import React from "react";
import Link from "next/link";
import {usePathname} from "next/navigation";
import {useAuth} from "@/app/_hooks/auth/useAuth";

const NavBar = () => {
  const { isSignedIn } = useAuth();

  return (
    <nav className="navbar navbar-expand bg-body-tertiary">
      <div className="container-fluid text-center">
        <Link href="/" className="navbar-brand">Cook it yourself</Link>
        {isSignedIn ? <NavBarMenu /> : <SignInButton />}
      </div>
    </nav>
  );
};

const NavBarMenu = () => {
  return (
    <>
      <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbar-menu"
              aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span className="navbar-toggler-icon"></span>
      </button>

      <div className="collapse navbar-collapse" id="navbar-menu">
        <ul className="navbar-nav">
          <NavBarItem path="/my-recipes">My recipes</NavBarItem>
          <NavBarItem path="/cooking">Cooking</NavBarItem>
        </ul>
      </div>
    </>
  );
};

const NavBarItem = ({
  path,
  children
}: Readonly<{
  path: string,
  children: React.ReactNode,
}>) => {
  const currentPath = usePathname();

  return (
    <li className="nav-item">
      <Link className={path === currentPath ? "nav-link active" : "nav-link"} href={path}>{children}</Link>
    </li>
  );
};

const SignInButton = () => {
  return (
    <a
      href={process.env.API_GATEWAY_CLIENT_URL + "/oauth2/authorization/ciy"}
      className="btn btn-primary btn-sm"
    >
      Sign in
    </a>
  );
};

export {NavBar};