import React from "react";
import {createRoot} from "react-dom";
import {createBrowserRouter, Navigate, RouterProvider} from "react-router-dom";
import {SignIn} from "./pages/signIn/SignIn";
import {CookiesProvider} from "react-cookie";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Navigate to={"/sign-in"} />
  },
  {
    path: "/sign-in",
    element: <SignIn />
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