import { createRoot } from 'react-dom/client'
import './index.css'
import {createBrowserRouter, Navigate, RouterProvider} from "react-router-dom";
import {SignUpPage} from "./pages/sign-up/SignUpPage.tsx";
import {SignInPage} from "./pages/sign-in/SignInPage.tsx";
import {ConsentPage} from "./pages/consent/ConsentPage.tsx";

const router = createBrowserRouter(
  [
    {
      path: "/",
      element: <Navigate to={"/sign-in"} />
    },
    {
      path: "/sign-up",
      element: <SignUpPage />
    },
    {
      path: "/sign-in",
      element: <SignInPage />
    },
    {
      path: "/consent",
      element: <ConsentPage />
    },
  ],
  {
    basename: import.meta.env.VITE_PUBLIC_PATH
  }
);

createRoot(document.getElementById('root')!).render(
  <RouterProvider router={router} />,
);
