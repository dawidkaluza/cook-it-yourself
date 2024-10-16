import {useFormState} from "react-dom";
import {Mock} from "vitest";
import {render, screen} from "@testing-library/react";
import Page from "@/app/my-recipes/[id]/delete/page";

vi.mock("react-dom", () => {
  return {
    useFormState: vi.fn(),
  };
});

describe("page component", () => {
  test.each([
    [ "" ],
    [ "The recipe does not exist." ],
  ])("render", (errorMessage) => {
    // Given
    const recipeId = 1;
    (useFormState as Mock).mockReturnValue([
      errorMessage,
      () => {}
    ]);

    // When
    const formComponent = render(<Page params={{ id: 1 }} />);

    // Then
    const questionText = screen.getByText(/are you sure/i);
    expect(questionText).not.toBeNull();

    const deleteButton = screen.getByRole("button", { name: /delete/i });
    expect(deleteButton).not.toBeNull();

    const cancelButton  = screen.getByRole("link", { name: /cancel/i });
    expect(cancelButton).not.toBeNull();

    if (errorMessage) {
      const errorText = screen.getByText(errorMessage);
      expect(errorText).not.toBeNull();
    }

    expect(formComponent.container).toMatchSnapshot();
  });
});