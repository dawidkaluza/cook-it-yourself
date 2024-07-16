
type Props = {
  id: number;
};

const ReviewRecipe = ({ id } : Props) => {
  return (
    <p>Recipe id is: {id}</p>
  );
};

export { ReviewRecipe };