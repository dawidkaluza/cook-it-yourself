import type { Metadata } from "next";
import React from "react";

export const metadata: Metadata = {
  title: "Cook It Yourself",
  description: "Cooking made easier",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
