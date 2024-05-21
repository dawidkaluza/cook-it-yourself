import Link from "next/link";

const Page = () => {
  return (
    <p className="text-center">
      Welcome my new cook! Would you like to start the journey with the app?
      {" "} <Link href="#">Sign in</Link> first.
    </p>
  );
};

export default Page;
