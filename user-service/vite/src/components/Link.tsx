import React from "react";
import {Link as RouterLink} from "react-router-dom";

export const Link = (props: {
  to: string;
  children: React.ReactNode;
}) => {
  return (
    <RouterLink
      to={props.to}
      className="font-bold text-blue-500 hover:text-blue-700"
    >
      {props.children}
    </RouterLink>
  );
};