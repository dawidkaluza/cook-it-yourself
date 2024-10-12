import React from "react";

const Main = ({
  children
}: Readonly<{
  children: React.ReactNode
}>) => {
  return (
    <main className="m-3">
      {children}
    </main>
  );
};

export { Main };