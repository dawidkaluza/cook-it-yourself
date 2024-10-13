import {MyRecipes} from "@/app/my-recipes/_components/MyRecipes";

const Layout = ({
  children
}: Readonly<{
  children: React.ReactNode
}>) => {
  return (
    <div className="container">
      <div className="row">
        <div className="col">
          {children}
        </div>
      </div>
    </div>
  );
};

export default Layout;