import {
  Alert,
  Box,
  Button,
  Checkbox,
  CircularProgress,
  FormControlLabel,
  FormGroup,
  TextField,
  Typography
} from "@mui/material";
import React, {useState} from "react";
import SendIcon from "@mui/icons-material/Send";
import CancelIcon from "@mui/icons-material/Cancel";
import {useSearchParams} from "react-router-dom";

/*
client_id - the client identifier
scope - a space-delimited list of scopes present in the authorization request
state - a CSRF protection token

 */


const Form = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const clientId = searchParams.get('client_id');

  const state = searchParams.get("state");

  const scope = searchParams.get("scope");
  const [scopes, setScopes] = useState(scope ? scope.split(" ") : []);
  const [checkedScopes, setCheckedScopes] = useState(new Set(scopes));

  const isScopeChecked = (scope) => {
    return checkedScopes.has(scope);
  };

  const handleScopeChange = (event) => {
    const target = event.target;
    const newCheckedScopes = new Set(checkedScopes);
    if (target.checked) {
      newCheckedScopes.add(target.value);
    } else {
      newCheckedScopes.delete(target.value);
    }

    setCheckedScopes(newCheckedScopes);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    console.log("submit");
  };

  return (
    <Box
      component={"form"}
      onSubmit={handleSubmit}
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
        Consent access
      </Typography>

      <Typography>
        {clientId} wants to get access to your account.
      </Typography>

      {scopes ?
        <Typography>
          Check which access you want to consent and approve or decline the request.
        </Typography>
        :
        <Typography>
          Decide if you want to approve the request or not.
        </Typography>
      }

      <FormGroup>
        {scopes.map(scope =>
          <FormControlLabel
            key={scope}
            control={<Checkbox name={"scope"} value={scope} checked={isScopeChecked(scope)} onChange={handleScopeChange} />}
            label={scope}
          />
        )}
      </FormGroup>

      <Box sx={{
        width: 1,
        display: "flex",
        flexDirection: "row",
        justifyContent: "center"
      }}>
        <ApproveButton loading={false} sx={{ m: 1 }} />
        <RejectButton loading={false} sx={{ m: 1 }} />
      </Box>
    </Box>
  );
};

const ApproveButton = ({
  loading, ...props
}) => {
  return (
    <Button
      variant={"contained"}
      color={"success"}
      type={"submit"}
      endIcon={loading ? <CircularProgress size={16} /> : <SendIcon />}
      disabled={loading}
      {...props}
    >
      Approve
    </Button>
  );
};

const RejectButton = ({
  loading, ...props
}) => {
  return (
    <Button
      variant={"outlined"}
      color={"error"}
      endIcon={loading ? <CircularProgress size={16} /> : <CancelIcon />}
      disabled={loading}
      {...props}
    >
      Reject
    </Button>
  );
};

export { Form };