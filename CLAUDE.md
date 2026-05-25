# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Java QA automation portfolio project with two test suites:
- **API tests** (REST Assured + JUnit 5) targeting the Restful Booker REST API and a Hygraph GraphQL API
- **UI tests** (Playwright for Java) targeting DemoQA

## Commands

```bash
# Run all tests
mvn clean test

# Run only API tests
mvn test -Dgroups="api"

# Run only UI tests
mvn test -Dgroups="ui"

# Run a single test class
mvn test -Dtest=ClassName

# Generate Allure report (if configured)
mvn allure:serve
```

## Tech Stack

- Java 11+, Maven 3.6+
- JUnit 5 for test runner and grouping (`@Tag("api")` / `@Tag("ui")`)
- REST Assured for HTTP/REST and GraphQL assertions
- Playwright for Java for browser automation
- AssertJ for fluent assertions
- Jackson for JSON (de)serialization
- Lombok to reduce boilerplate
- Allure for reporting (bonus)

## Architecture

### API Tests
- `src/test/java/.../api/` — REST Assured test classes
- Base class holds `RequestSpecification` setup (base URI, content-type, logging)
- Auth token obtained once via `POST /auth` and reused across CRUD tests
- GraphQL tests send queries via `POST` with a JSON body `{"query": "...", "variables": {...}}`; responses are always HTTP 200 so assertions check `data` / `errors` fields

### UI Tests — Page Object Model
- `src/test/java/.../ui/pages/` — one class per page/component
- `src/test/java/.../ui/tests/` — test classes extend a `BaseUITest` that opens/closes the browser
- `BaseUITest` creates a `Playwright` + `Browser` instance (Chromium by default), takes screenshots on failure via a JUnit 5 extension

### Target URLs
| Suite | URL |
|---|---|
| REST API | https://restful-booker.herokuapp.com |
| GraphQL | https://hygraph.com/graphql-playground (pick Video, Ecommerce, or Marketing schema) |
| UI – Form | https://demoqa.com/automation-practice-form |
| UI – Tables | https://demoqa.com/webtables |

### Key Conventions
- JUnit 5 `@Tag` annotations (`api`, `ui`) drive selective test execution via `-Dgroups`
- Restful Booker resets data periodically — tests must create their own data rather than assume pre-existing records
- Dynamic waits only (no `Thread.sleep`) in Playwright tests
- Screenshots saved on test failure, not on every step