import {Navigation} from "@/app/my-recipes/_components/Navigation";

const Layout = ({
  children
}: Readonly<{
  children: React.ReactNode
}>) => {
  return (
    <>
      <Navigation />
      <div className="container">
        <div className="row">
          <div className="col">
            {children}
          </div>
        </div>
      </div>
    </>
  );
};

export default Layout;