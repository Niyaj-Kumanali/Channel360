# Channel360 — Enterprise API & Envelope Response Standards

This document establishes standard patterns for endpoint design, success wrappers, error mapping payloads, and pagination schemas.

---

## 1. Unified HTTP Response Envelopes

### A. Standard Objects / Single Resources (`ApiResponse<T>`)
Every singular data output must wrap responses uniformly using this payload scheme:
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
B. Paginated Collections (PageResponse<T>)
Paginated data structures are delivered directly to the client without an exterior ApiResponse container:

JSON
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
2. Error Resolution & Validation Payload (ErrorResponse)
When request validations fail or standard operations run into exceptions, endpoints must issue a standard 400 or 422 payload:

JSON
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