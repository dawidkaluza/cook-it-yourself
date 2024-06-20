import { expect, test } from 'vitest'
import {render, screen} from "@testing-library/react";
import Page from "@/app/page";

test('Page has welcome message', () => {
  render(<Page />);

  const welcomeText = screen.queryByText(/Welcome my cook/);
  expect(welcomeText).toBeDefined();
});

test('Page matches existing snapshot', () => {
  const page = render(<Page />);
  expect(page.container).toMatchSnapshot();
})