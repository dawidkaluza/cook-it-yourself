import {WelcomeMessage} from "@/app/_components/WelcomeMessage";
import {isSignedIn} from "@/app/_api/auth";
import {redirect} from "next/navigation";

const Page = () => {
  const signedIn = isSignedIn();
  if (signedIn) {
    redirect("/my-recipes");
  }

  return (
    <WelcomeMessage />
  );
};

export default Page;
