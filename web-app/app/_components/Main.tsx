import React from "react";

const Main = ({
  children
}: Readonly<{
  children: React.ReactNode
}>) => {
  return (
    <main style={{margin: "1em"}}>
      {children}
    </main>
  );
};

export { Main };