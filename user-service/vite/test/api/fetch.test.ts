import {fetchApi} from "../../src/api/fetch.ts";
import {afterAll, afterEach, beforeAll, expect, Mock} from "vitest";
import {settings} from "../../src/settings/settings.ts";
import Cookies from "universal-cookie";

beforeAll(() => {
  vi.stubGlobal("fetch", vi.fn());
});

afterEach(() => {
  (fetch as Mock).mockClear();
});

afterAll(() => {
  vi.unstubAllGlobals();
});

describe("fetchApi function", () => {
  test("fetch error response, throw error", async () => {
    // Given
    const givenResponse = {
      ok: false,
      status: 404,
      statusText: "Not Found",
    };

    (fetch as Mock).mockResolvedValue(givenResponse);

    // When, then
    await expect(() => fetchApi({ endpoint: "/invalid-path" }))
      .rejects.toThrowError("API error, http status 404");
  });
  test.each([
    [ "", "same-origin" ],
    [ "http://localhost:8008", "include" ],
  ])("fetch when userServiceUrl=%s, return the response", async (userServiceUrl, expectedCredentials) => {
    // Given
    vi.spyOn(settings, "userServiceUrl", "get").mockReturnValue(userServiceUrl);

    const givenResponse = {
      ok: true,
      json: () => Promise.resolve({ id: 1 }),
    };
    const fetchMock = (fetch as Mock);
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

  test("fetch with xsrf token", async () => {
    // Given
    const cookies = new Cookies();
    cookies.set("XSRF-TOKEN", "123xyz");

    const givenResponse = {
      ok: true,
      json: () => Promise.resolve({ id: 1 }),
    };
    const fetchMock = (fetch as Mock);
    fetchMock.mockResolvedValue(givenResponse);

    // When
    const response: { id: number } = await fetchApi({ endpoint: "/user/1" });

    // Then
    expect(response).toBeDefined();
    expect(fetchMock).toHaveBeenCalledWith(
      expect.anything(),
      expect.objectContaining({
        headers: expect.objectContaining({
          "X-XSRF-TOKEN": "123xyz"
        })
      })
    );
  })
})