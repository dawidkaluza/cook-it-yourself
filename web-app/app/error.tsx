"use client";

import {useEffect} from "react";

const Error = ({
  error, reset
} : {
  error: Error & { digest?: string },
  reset: () => void,
}) => {
  useEffect(() => {
    console.log("Caught error: ", error);
  }, []);
  return (
    <>
      <p>Something went wrong...</p>
    </>
  );
};

export default Error;