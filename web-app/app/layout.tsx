import type { Metadata } from "next";
import React from "react";
import 'bootstrap/dist/css/bootstrap.min.css'

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
    <html lang="en">
      <body>{children}</body>
    </html>
  );
};

export default RootLayout;
