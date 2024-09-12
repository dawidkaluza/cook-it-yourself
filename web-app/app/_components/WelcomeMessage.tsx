"use client";

import {useAuth} from "@/app/_api/hooks";

const WelcomeMessage = () => {
  const {isSignedIn, name} = useAuth();

  if (isSignedIn) {
    return (
      <>
        <p className="text-center">
          Welcome, {name}!
        </p>
        <p className="text-center">
          Use the navigation above to start your cooking journey.
        </p>
      </>
    );
  }

  return (
    <>
      <p className="text-center">
        Manage recipes, rate dishes, plan diet, plan grocery shopping and more.
      </p>
      <p className="text-center">
        Want to try it out? <a href="/sign-in">Sign in</a>.
      </p>
    </>
  );
};

export {WelcomeMessage}