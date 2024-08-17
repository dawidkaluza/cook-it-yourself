import {fetchApi} from "../../src/api/fetch.ts";

const fetchMock = vi.fn();
globalThis.fetch = fetchMock;

const userServiceUrlMock = vi.fn();
vi.mock("../settings/settings.ts", () => {
  return {
    publicPath: "",
    userServiceUrl: userServiceUrlMock,
  };
})

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
    [ "" ],
    [ "http://localhost:8008" ],
  ])("fetch when userServiceUrl=%s, return the response", async (userServiceUrl) => {
    // Given
    userServiceUrlMock.mockReturnValue(userServiceUrl);

    const givenResponse = {
      ok: true,
      json: () => Promise.resolve({ id: 1 }),
    };
    fetchMock.mockResolvedValue(givenResponse);

    // When
    const response = await fetchApi({
      endpoint: "/sign-up",
      method: "POST",
      body: JSON.stringify({ name: "Dawid" }),
      headers: {
        "Custom-Header": "Custom-Header-Value",
      }
    });

    // Then
    expect(response).toBeDefined();
    // TODO write more assertions here
  });
})