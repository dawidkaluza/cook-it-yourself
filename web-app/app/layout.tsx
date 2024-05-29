import type { Metadata } from "next";
import React from "react";
import 'bootstrap/dist/css/bootstrap.min.css'
import {NavBar} from "@/app/_components/NavBar";

export const metadata: Metadata = {
  title: "Cook It Yourself",
  description: "Cooking made easier",
};

const RootLayout = ({
  children
}: Readonly<{
  children: React.ReactNode
}>) => {
  return (
    <html lang="en" data-bs-theme="dark">
      <body>
        <NavBar />
        <main style={{ margin: "1em" }}>
          {children}
        </main>
      </body>
    </html>
  );
};

export default RootLayout;
