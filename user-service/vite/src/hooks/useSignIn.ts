
type SignInProps = {
  signIn: () => void;
  loading: boolean,
  error: boolean;
  success: boolean;
};

export const useSignIn = (): SignInProps => {
  return {
    signIn: () => { console.log("Not implemented yet"); },
    loading: false,
    error: false,
    success: false,
  }
};