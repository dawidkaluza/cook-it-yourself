import {Alert, Box, Button, CircularProgress, Link, TextField, Typography} from "@mui/material";
import {Link as RouterLink} from "react-router-dom";
import React, {useState} from "react";
import LoginIcon from "@mui/icons-material/Login";
import {useSignUp} from "../../services/user/useSignUp";

const Form = () => {
  const { signUp, loading, success, error, fieldsErrors } = useSignUp();
  const [fields, setFields] = useState({});

  const handleFieldChange = (event) => {
    const target = event.target;
    setFields({
      ...fields,
      [target.name]: target.value
    });
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    signUp(fields);
  };

  return (
    <Box
      onSubmit={handleSubmit}
      component={"form"}
      sx={{
        mt: 2,
        mb: 2,
        width: 1,
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "space-evenly"
      }}
    >
      <Typography
        component={"h1"}
        variant={"h5"}
        align={"center"}
      >
        Sign up
      </Typography>

      <FormTextField
        name={"email"}
        label={"E-mail"}
        onChange={handleFieldChange}
        fields={fields}
        errors={fieldsErrors}
      />

      <FormTextField
        name={"password"}
        type={"password"}
        label={"Password"}
        onChange={handleFieldChange}
        fields={fields}
        errors={fieldsErrors}
      />

      <FormTextField
        name={"name"}
        label={"Name"}
        onChange={handleFieldChange}
        fields={fields}
        errors={fieldsErrors}
      />

      <SubmitButton
        loading={loading}
        sx={{
          mt: 1,
          mb: 1,
        }}
      />
      <Typography>
        Already signed up? <Link component={RouterLink} to={"/sign-in"}>Sign in.</Link>
      </Typography>
      {error &&
        <Alert
          severity={"error"}
          sx={{ width: 1 }}
        >
          {error}
        </Alert>
      }

      {success &&
        <Alert
          severity={"success"}
          sx={{ width: 1 }}
        >
          <Typography>
            {success}
          </Typography>
        </Alert>
      }
    </Box>
  );
};



const FormTextField = ({
  name, type = "text", label = "", placeholder = "", fields, errors, onChange, ...props
}) => {
  return (
    <TextField
      name={name}
      type={type}
      label={label}
      placeholder={placeholder}
      fullWidth
      margin={"dense"}
      variant={"standard"}
      onChange={onChange}
      value={fields[name] ?? ""}
      error={!!errors[name]}
      helperText={errors[name]}
      {...props}
    />
  );
};

const SubmitButton = ({
  loading, ...props
}) => {
  return (
    <Button
      variant={"contained"}
      type={"submit"}
      endIcon={loading ? <CircularProgress size={16} /> : <LoginIcon />}
      disabled={loading}
      {...props}
    >
      Sign up
    </Button>
  );
};

export { Form }