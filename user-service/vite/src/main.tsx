import { createRoot } from 'react-dom/client'
import App from './App.tsx'
import './index.css'
import {createBrowserRouter, Navigate, RouterProvider} from "react-router-dom";
import {SignUp} from "./pages/SignUp.tsx";
import {SignIn} from "./pages/SignIn.tsx";

const router = createBrowserRouter(
  [
    {
      path: "/",
      element: <Navigate to={"/sign-in"} />
    },
    {
      path: "/sign-up",
      element: <SignUp />
    },
    {
      path: "/sign-in",
      element: <SignIn />
    },
    {
      path: "/consent",
      element: <App />
    },
  ],
  {
    basename: import.meta.env.VITE_PUBLIC_PATH
  }
);

createRoot(document.getElementById('root')!).render(
  <RouterProvider router={router} />,
);
