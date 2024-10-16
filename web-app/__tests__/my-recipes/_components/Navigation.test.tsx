import {render, screen} from "@testing-library/react";
import {usePathname} from "next/navigation";
import {Mock} from "vitest";
import {Navigation} from "@/app/my-recipes/_components/Navigation";

vi.mock("next/navigation", () => {
  return {
    usePathname: vi.fn(),
  };
});

describe("Navigation component", () => {
  test.each([
    {
      pathname: "/my-recipes",
      expectedNavItems: [
        {
          name: "My recipes",
          path: "/my-recipes",
          active: true,
        }
      ]
    },
    {
      pathname: "/my-recipes/1",
      expectedNavItems: [
        {
          name: "My recipes",
          path: "/my-recipes",
          active: false,
        },
        {
          name: "Review",
          path: "/my-recipes/1",
          active: true,
        }
      ]
    },
    {
      pathname: "/my-recipes/1/edit",
      expectedNavItems: [
        {
          name: "My recipes",
          path: "/my-recipes",
          active: false,
        },
        {
          name: "Review",
          path: "/my-recipes/1",
          active: false,
        },
        {
          name: "Edit",
          path: "/my-recipes/1/edit",
          active: true,
        }
      ]
    },
  ])("renders $pathname", ({ pathname, expectedNavItems }) => {
    // Given
    (usePathname as Mock).mockImplementation(() => pathname);

    // When
    const navigation = render(<Navigation />);

    // Then
    for (const navItem of expectedNavItems) {
      const navItemElement = screen.getByText(navItem.name);
      expect(navItemElement).not.toBeNull();
      if (navItem.active) {
        expect(navItemElement?.getAttribute("href")).toBeNull();
      } else {
        expect(navItemElement?.getAttribute("href")).toBe(navItem.path);
      }
    }

    expect(navigation.container).toMatchSnapshot();
  });
});