# Greenwood Eco School LMS — Security/UI Verification

## First-login password flow

- Login success checks `must_change_password` and routes a forced first login directly to `/change-password?firstLogin=true`.
- The first-login filter remains as a server-side enforcement layer for protected routes.
- `/change-password`, authentication endpoints, and static assets (`/css/**`, `/js/**`, `/images/**`, `/webjars/**`) are exempt from the first-login redirect so the password page keeps its styling and assets.
- The first-login page clearly explains that the temporary password must be replaced before continuing.
- First-login users do not receive a misleading Dashboard/Cancel action.
- Current/temporary password is verified server-side.
- New password must be 8–128 characters, contain uppercase, lowercase and a number, match the confirmation, and differ from the current password.
- Successful password changes clear `must_change_password`, reset login-failure/lock state, and audit the event.
- Account creation and password reset require strong temporary passwords and confirmation.
- Temporary/reset passwords are never displayed or stored in plaintext.

## UI consistency

The password page follows the same shared dashboard workspace system: sidebar, topbar, page heading, panel, rounded fields, badges, responsive layout, shared typography and buttons.

## Static verification

A repeatable static verification suite was run 20 times after the final changes.

- 20/20 passes
- No duplicate HTML IDs detected
- No duplicate controller mappings detected
- Java brace-balance checks passed
- Shared stylesheet checks passed
- First-login security invariants passed
- Marks `BigDecimal` fields verified
- Transcript GPA `BigDecimal` verified
- No duplicate archive paths detected

## Runtime note

A full Maven/Spring Boot/MySQL runtime test could not be executed in this environment because Maven is not installed. On Windows, run `VERIFY-LOCAL.bat` or:

```text
mvn clean test
mvn spring-boot:run
```
