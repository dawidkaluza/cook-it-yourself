import {Alert, Box, Button, Checkbox, CircularProgress, FormControlLabel, FormGroup, Typography} from "@mui/material";
import React, {useState} from "react";
import SendIcon from "@mui/icons-material/Send";
import CancelIcon from "@mui/icons-material/Cancel";
import {useSearchParams} from "react-router-dom";
import {useConsent} from "../../services/user/useConsent";

const Form = () => {
  const [searchParams] = useSearchParams();

  const scope = searchParams.get("scope");
  const scopes = scope ? scope.split(" ") : [];
  const [checkedScopes, setCheckedScopes] = useState(new Set(scopes));

  const { consent, loading, success, error } = useConsent();

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

  const clientId = searchParams.get('client_id');
  const state = searchParams.get("state");
  const handleSubmit = (event) => {
    event.preventDefault();
    consent({ clientId, state, scopes: checkedScopes });
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

      {scopes.length ?
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
            control={<Checkbox name={"scope"} value={scope} checked={checkedScopes.has(scope)} onChange={handleScopeChange} />}
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
        <ApproveButton loading={loading} sx={{ m: 1 }} />
        <DeclineButton loading={false} sx={{ m: 1 }} />
      </Box>

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

// TODO What to do when User decides to decline the request?
const DeclineButton = ({
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
      Decline
    </Button>
  );
};

export { Form };