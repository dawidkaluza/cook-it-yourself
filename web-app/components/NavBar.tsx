import React from "react";
import Link from "next/link";

const NavBar = () => {
  return (
    <nav className="navbar navbar-expand bg-body-tertiary">
      <div className="container-fluid text-center">
        <Link href="/" className="navbar-brand">Cook it yourself</Link>
        <Link href="/sign-in" className="btn btn-primary btn-sm">Sign in</Link>
      </div>
    </nav>
  );
};

export { NavBar };