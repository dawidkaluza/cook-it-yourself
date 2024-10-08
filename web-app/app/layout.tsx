import type { Metadata } from "next";
import React from "react";
import 'bootstrap/dist/css/bootstrap.min.css'
import {Main} from "@/app/_components/Main";
import {Header} from "@/app/_components/Header";

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
        <Header />
        <Main>{children}</Main>
      </body>
    </html>
  );
};

export default RootLayout;
