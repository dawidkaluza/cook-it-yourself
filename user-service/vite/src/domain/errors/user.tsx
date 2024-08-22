export class InvalidCredentialsError extends Error {
  constructor(message: string) {
    super(message);
    this.name = "InvalidCredentialsError";
  }
}

export class InvalidFieldsError extends Error {
  private readonly _fields: Record<string, string>;

  constructor(fields: Record<string, string>) {
    super("Invalid fields: " + fields);
    this.name = "InvalidFieldsError";
    this._fields = fields;
  }


  get fields(): Record<string, string> {
    return this._fields;
  }
}