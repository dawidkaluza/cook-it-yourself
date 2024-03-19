import React from "react";
import {createRoot} from "react-dom";

const rootElement = document.getElementById("app");
const root = createRoot(rootElement);
root.render(<h1> Hello World! </h1>);