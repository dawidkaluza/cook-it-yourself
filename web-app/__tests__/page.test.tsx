import { expect, test } from 'vitest'
import {render, screen} from "@testing-library/react";
import Page from "@/app/page";

test('Page', () => {
  render(<Page />);

  const welcomeText = screen.queryByText(/Welcome my cook/);
  expect(welcomeText).toBeDefined();
});