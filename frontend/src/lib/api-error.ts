export class ApiError extends Error {
  errors?: string[];
  statusCode?: number;

  constructor(message: string, options?: { errors?: string[]; statusCode?: number }) {
    super(message);
    this.name = 'ApiError';
    this.errors = options?.errors;
    this.statusCode = options?.statusCode;
  }
}
