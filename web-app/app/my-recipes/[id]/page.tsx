import {ReviewRecipe} from "@/app/my-recipes/[id]/_components/ReviewRecipe";

const Page = ({ params } : { params: { id: number }}) => {
  const { id } = params;
  return (
    <ReviewRecipe id={id} />
  );
};

export default Page;