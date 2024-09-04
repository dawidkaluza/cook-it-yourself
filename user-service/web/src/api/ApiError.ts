

export class ApiError<T> extends Error {
  private readonly _status: number;
  private readonly _body: T;

  constructor(status: number, body: T) {
    super("API error, http status " + status);
    this.name = "ApiError";
    this._status = status;
    this._body = body;
  }

  get status(): number {
    return this._status;
  }

  get body(): T {
    return this._body;
  }
}