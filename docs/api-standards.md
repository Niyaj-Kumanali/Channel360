# Channel360 — Enterprise API Standards

This document establishes standard patterns for endpoint design, success wrappers, error mapping payloads, and pagination schemas.

> See [Architecture](architecture.md) for backend folder structure and stored procedure conventions.

---

## 1. Unified HTTP Response Envelopes

### A. Standard Objects / Single Resources (`ApiResponse<T>`)

Every singular data output must wrap responses uniformly:

```json
{
  "success": true,
  "message": "Resource retrieved successfully.",
  "data": {
    "id": 4120,
    "name": "Southern Distribution Hub"
  },
  "timestamp": 1782212400000,
  "statusCode": 200
}
```

### B. Paginated Collections (`PageResponse<T>`)

Paginated data is delivered directly without an exterior `ApiResponse` container:

```json
{
  "content": [
    { "id": 1, "name": "Item Alpha" },
    { "id": 2, "name": "Item Beta" }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 142,
  "totalPages": 8
}
```

## 2. Error Response (`ErrorResponse`)

When request validations fail or standard operations encounter exceptions:

```json
{
  "success": false,
  "message": "Validation constraints failed.",
  "errors": [
    {
      "field": "email",
      "rejectedValue": "invalid-email@",
      "message": "Must be a valid enterprise email format."
    }
  ],
  "timestamp": 1782212405000,
  "statusCode": 400
}
```

## 3. Summary

| Envelope | Use Case | Returned As |
|----------|----------|-------------|
| `ApiResponse<T>` | Single resource | Wrapped response body |
| `PageResponse<T>` | Paginated list | Direct response body (no wrapper) |
| `ErrorResponse` | Validation / error | Thrown by `GlobalExceptionHandler` |
