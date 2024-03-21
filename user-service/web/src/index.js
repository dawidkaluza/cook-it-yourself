import React from "react";
import {createBrowserRouter, Navigate, RouterProvider} from "react-router-dom";
import {SignIn} from "./pages/signIn/SignIn";
import {CookiesProvider} from "react-cookie";
import {Consent} from "./pages/consent/Consent";
import {createRoot} from "react-dom/client";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Navigate to={"/sign-in"} />
  },
  {
    path: "/sign-in",
    element: <SignIn />
  },
  {
    path: "/consent",
    element: <Consent />
  }
]);

const rootElement = document.getElementById("app");
const root = createRoot(rootElement);
root.render(
  <>
    <CookiesProvider>
      <RouterProvider router={router} />
    </CookiesProvider>
  </>
);