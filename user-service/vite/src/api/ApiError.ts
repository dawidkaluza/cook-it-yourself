

export class ApiError extends Error {
  private _response: Response;

  constructor(response: Response) {
    super("API error, http status " + response.status);
    this.name = "ApiError";
    this._response = response;
  }

  get response(): Response {
    return this._response;
  }

  set response(value: Response) {
    this._response = value;
  }
}