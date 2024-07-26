import {Mock} from "vitest";
import {SignIn} from "@/app/sign-in/_components/SignIn";
import {render} from "@testing-library/react";
import {useRouter, useSearchParams} from "next/navigation";
import {useAuth} from "@/app/_api/hooks";

vi.mock("next/navigation", () => {
  return {
    useRouter: vi.fn(),
    useSearchParams: vi.fn(),
  };
});

vi.mock("@/app/_api/hooks", () => {
  return {
    useAuth: vi.fn(),
  };
});

let hasMock: Mock;
let pushMock: Mock;
let signInMock: Mock;
let signOutMock: Mock;

describe("SignIn component", () => {
  beforeEach(() => {
    hasMock = vi.fn();
    pushMock = vi.fn();
    signInMock = vi.fn();
    signOutMock = vi.fn();

    (useSearchParams as Mock).mockImplementation(() => {
      return {
        has: hasMock
      };
    });
    (useRouter as Mock).mockImplementation(() => {
      return {
        push: pushMock
      };
    });
    (useAuth as Mock).mockImplementation(() => {
      return {
        signIn: signInMock,
        signOut: signOutMock
      };
    });

    return () => {
      vi.restoreAllMocks();
    };
  });

  test("redirects on success", () => {
    // Given
    hasMock.mockImplementation((name : string ) => {
      return name === "success";
    });

    // When
    render(<SignIn />);

    // Then
    expect(signInMock).toHaveBeenCalled();
    expect(signOutMock).not.toHaveBeenCalled();
    expect(pushMock).toHaveBeenCalledWith("/");
  });

  test.each([
    ["sign-out"],
    ["error"],
  ])("redirects on %s", (searchParams) => {
    // Given
    hasMock.mockImplementation((name : string ) => {
      return name === searchParams;
    });

    // When
    render(<SignIn />);

    // Then
    expect(signOutMock).toHaveBeenCalled();
    expect(signInMock).not.toHaveBeenCalled();
    expect(pushMock).toHaveBeenCalledWith("/")
  });

  test("redirects when no params", () => {
    // Given
    hasMock.mockImplementation(() => false);

    // When
    render(<SignIn />);

    // Then
    expect(signInMock).not.toHaveBeenCalled();
    expect(signOutMock).not.toHaveBeenCalled();
    expect(pushMock).toHaveBeenCalledWith(process.env.NEXT_PUBLIC_API_GATEWAY_CLIENT_URL + "/oauth2/authorization/ciy");
  });
})