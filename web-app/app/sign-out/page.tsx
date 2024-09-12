import {redirect} from "next/navigation";

const Page = () => {
  redirect(process.env.NEXT_PUBLIC_API_GATEWAY_CLIENT_URL + "/logout");
};

export default Page;