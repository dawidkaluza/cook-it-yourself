import {render, screen} from "@testing-library/react";
import {usePathname} from "next/navigation";
import {Mock} from "vitest";
import {Navigation} from "@/app/_components/Navigation";


vi.mock("next/navigation", () => {
  return {
    usePathname: vi.fn(),
  };
});

describe("Navigation component", () => {
  test.each([
    {
      pathname: "/",
      expectedNavItems: [
        {
          name: "Home",
          path: "/",
          active: true,
        }
      ]
    },
    {
      pathname: "/recipes/1",
      expectedNavItems: [
        {
          name: "Home",
          path: "/",
          active: false,
        },
        {
          name: "Recipes",
          path: "/recipes",
          active: false,
        },
        {
          name: "View",
          path: "/recipes/1",
          active: true,
        }
      ]
    },
    {
      pathname: "/recipes/1/edit",
      expectedNavItems: [
        {
          name: "Home",
          path: "/",
          active: false,
        },
        {
          name: "Recipes",
          path: "/recipes",
          active: false,
        },
        {
          name: "View",
          path: "/recipes/1",
          active: false,
        },
        {
          name: "Edit",
          path: "/recipes/1/edit",
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