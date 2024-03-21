import {Box} from "@mui/material";
import React from "react";
import {Form} from "./Form";

const Consent = () => {
  return (
    <Box sx={{
      mt: "5em",
      display: "flex",
      flexDirection: "column",
      alignItems: "center",
      justifyContent: "center",
    }}>
      <Box sx={{
        width: {
          xs: 0.9,
          sm: 0.6,
          md: 0.3
        },
      }}>
        <Form />
      </Box>
    </Box>
  );
};

export { Consent };
