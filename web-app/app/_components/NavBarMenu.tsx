"use client";

import React from "react";
import {usePathname} from "next/navigation";
import Link from "next/link";

export const NavBarMenu = () => {
  return (
    <>
      <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbar-menu"
              aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span className="navbar-toggler-icon"></span>
      </button>

      <div className="collapse navbar-collapse" id="navbar-menu">
        <ul className="navbar-nav">
          <NavBarItem path="/my-recipes">My recipes</NavBarItem>
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