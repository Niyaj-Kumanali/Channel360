const MESSAGE_TO_FIELD: [RegExp, string][] = [
  [/email/i, 'email'],
  [/password.*6/i, 'newPassword'],
  [/new password/i, 'newPassword'],
  [/password/i, 'password'],
  [/token/i, 'token'],
];

export function mapErrorToField(message: string): string | null {
  for (const [pattern, field] of MESSAGE_TO_FIELD) {
    if (pattern.test(message)) return field;
  }
  return null;
}
