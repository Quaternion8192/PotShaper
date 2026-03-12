# Contributing to PotShaper

Thank you for improving PotShaper.
This guide keeps contributions consistent and easy to review.

## Prerequisites

- JDK 17 (required by `pom.xml`)
- Git
- Internet access for Maven dependency download

This project uses Maven Wrapper (`mvnw`, `mvnw.cmd`) and currently points to Maven 3.9.10.
You do not need to install Maven globally.

## Project Layout

- `src/main/java` - library source code
- `src/test/java` - unit tests
- `docs/` - project documentation
- `target/` - generated build output (do not commit)

## Build and Test

### Windows (PowerShell)

```powershell
.\mvnw.cmd clean test
```

```powershell
.\mvnw.cmd clean package
```

### macOS/Linux

```bash
./mvnw clean test
```

```bash
./mvnw clean package
```

## Coding Style

- Follow `.editorconfig` settings.
- Use 4 spaces for Java indentation.
- Keep files UTF-8 encoded.
- Add or update tests when behavior changes.
- Keep public API changes documented in Javadoc.

## Commit Messages

Use clear, focused commit messages.
A recommended format is:

`type(scope): short description`

Examples:

- `feat(shape): add smart text box alignment`
- `fix(parser): handle empty slide relationship`
- `test(api): cover merged cell edge case`

## Pull Request Checklist

Before opening a PR, make sure:

- [ ] The branch builds successfully.
- [ ] Tests pass locally.
- [ ] New behavior is covered by tests.
- [ ] Public API updates include Javadoc updates.
- [ ] No generated files are committed from `target/`.

## License

By submitting a contribution, you agree that your work is licensed under the Apache License 2.0 used by this project.

