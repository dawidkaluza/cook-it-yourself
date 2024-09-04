import { createRoot } from 'react-dom/client'
import './index.css'
import {createBrowserRouter, Navigate, RouterProvider} from "react-router-dom";
import {SignUpPage} from "./pages/sign-up/SignUpPage.tsx";
import {SignInPage} from "./pages/sign-in/SignInPage.tsx";

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
  ],
  {
    basename: import.meta.env.VITE_PUBLIC_PATH
  }
);

createRoot(document.getElementById('root')!).render(
  <RouterProvider router={router} />,
);
