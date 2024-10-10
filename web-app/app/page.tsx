import {isSignedIn} from "@/app/_api/auth";
import {redirect} from "next/navigation";

const Page = () => {
  const signedIn = isSignedIn();
  if (signedIn) {
    redirect("/my-recipes");
  }

  return (
    <>
      <p className="text-center">
        Simple application in which you can organize your recipes.
      </p>
      <p className="text-center">
        Want to try it out? <a href="/sign-in">Sign in</a>.
      </p>
    </>
  );
};

export default Page;
