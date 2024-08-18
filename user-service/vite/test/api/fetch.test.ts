import {fetchApi} from "../../src/api/fetch.ts";
import {afterEach, expect} from "vitest";

const fetchMock = vi.fn();
globalThis.fetch = fetchMock;

const userServiceUrlMock = vi.fn();
vi.mock("../../src/settings/settings.ts", () => {
  return {
    publicPath: "",
    userServiceUrl: userServiceUrlMock,
  };
});

afterEach(() => {
  fetchMock.mockClear();
  userServiceUrlMock.mockClear();
});

describe("fetchApi function", () => {
  test("fetch error response, throw error", async () => {
    // Given
    const givenResponse = {
      ok: false,
      status: 404,
      statusText: "Not Found",
    };

    fetchMock.mockResolvedValue(givenResponse);

    // When, then
    await expect(() => fetchApi({ endpoint: "/invalid-path" }))
      .rejects.toThrowError("API error, http status 404");
  });

  test.each([
    [ "", "same-origin" ],
    [ "http://localhost:8008", "include" ],
  ])("fetch when userServiceUrl=%s, return the response", async (userServiceUrl, expectedCredentials) => {
    // Given
    userServiceUrlMock.mockReturnValue(userServiceUrl);

    const givenResponse = {
      ok: true,
      json: () => Promise.resolve({ id: 1 }),
    };
    fetchMock.mockResolvedValue(givenResponse);

    // When
    const response: { id: number } = await fetchApi({
      endpoint: "/sign-up",
      method: "POST",
      body: JSON.stringify({ name: "Dawid" }),
      headers: {
        "Custom-Header": "Custom-Header-Value",
      }
    });

    // Then
    expect(response).toBeDefined();
    expect(response.id).toBe(1);

    expect(fetchMock).toHaveBeenCalledWith(userServiceUrl + "/sign-up", {
      method: "POST",
      body: JSON.stringify({ name: "Dawid" }),
      credentials: expectedCredentials,
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json",
        "Custom-Header": "Custom-Header-Value",
      }
    });
  });
})