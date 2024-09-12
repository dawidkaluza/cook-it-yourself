import {SignIn} from "@/app/sign-in/_components/SignIn";
import {redirect} from "next/navigation";

const Page = ({ searchParams }: { searchParams: { [key: string]: string | string[] | undefined } }) => {
  if (Object.keys(searchParams).length === 0) {
    redirect(process.env.NEXT_PUBLIC_API_GATEWAY_CLIENT_URL + "/login");
  }

  return (
    <SignIn />
  );
};

export default Page;