# QA Automation Test Suite

Java-based test automation portfolio covering REST API (Restful Booker), GraphQL API (Hygraph Video schema), and UI (DemoQA) testing. Built with REST Assured, Playwright for Java, JUnit 5, and Allure reporting.

---

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| Java | 11+ | `java -version` to verify |
| Maven | 3.6+ | `mvn -version` to verify |
| Chromium / Chrome | any | Playwright manages its own browser binaries |
| Allure CLI | 2.27+ | Required only for `allure serve` outside Maven — see below |

### Install Allure CLI (optional — only needed outside Maven)

The Maven plugin (`allure-maven`) runs reports and serves them without any external tool. Install the CLI only if you want to run `allure serve` from the command line directly against the results folder.

**macOS (Homebrew):**
```bash
   brew install allure
```

**Windows (Scoop):**
```bash
   scoop install allure
```

**npm (cross-platform):**
```bash
   npm install -g allure-commandline
```

### Install Playwright browser binaries

Playwright downloads its own Chromium/Firefox/WebKit — it does **not** use your system browser. Run this once after cloning:

```bash
   mvn test-compile exec:java \
     -Dexec.mainClass=com.microsoft.playwright.CLI \
     -Dexec.args="install chromium"
```

To install all supported browsers:
```bash
   mvn test-compile exec:java \
     -Dexec.mainClass=com.microsoft.playwright.CLI \
     -Dexec.args="install"
```

---

## How to Run

### Run all tests
```bash
   mvn clean test
```

### Run only API tests
```bash
   mvn test -Dgroups="api"
```

### Run only UI tests
```bash
   mvn test -Dgroups="ui"
```

### Run a single test class
```bash
   mvn test -Dtest=PostBookingTest
   mvn test -Dtest=PracticeFormTest
```

### Run with browser visible (headed mode)
```bash
   mvn test -Dgroups="ui" -Dui.headless=false
```

### Switch browser engine (chromium | firefox | webkit)
```bash
   mvn test -Dgroups="ui" -Dui.browser=firefox
```

### Generate Allure report (HTML, opens after `test` phase automatically)
```bash
   mvn clean test allure:report
# Report written to: target/site/allure-maven-plugin/index.html
```

### Serve Allure report in browser (via Maven plugin — no CLI needed)
```bash
   mvn allure:serve
# Opens http://127.0.0.1:<random-port> in your default browser
```

### Serve Allure report via CLI (if you have the CLI installed)
```bash
   allure serve target/allure-results
```

> **Note:** `allure:serve` requires the results from a previous test run. Always run `mvn clean test` first, then `mvn allure:serve`.

---

## CI / GitHub Actions

The pipeline is defined in `.github/workflows/ci.yml` and runs automatically on every push and pull request to `main`.

### What it does

| Step | Detail |
|------|--------|
| **Checkout** | Checks out the repository |
| **Java 17** | Sets up Temurin JDK 17; Maven dependency cache is restored automatically |
| **Allure CLI cache** | Caches `~/.allure/allure-2.27.0` across runs to skip re-download |
| **Playwright system deps** | Installs OS-level libraries required by Chromium on Linux (`install-deps`) |
| **Playwright browser** | Downloads the Chromium binary used by UI tests |
| **`mvn clean test`** | Runs the full test suite; Allure report is generated automatically via the bound `test` phase |
| **Upload report** | Allure HTML report uploaded as a workflow artifact (14-day retention) |
| **Upload results** | Raw Allure JSON results uploaded as an artifact (7-day retention) |
| **Publish to Pages** | On push to `main` only — deploys the Allure report to GitHub Pages |

### Viewing results

- **Artifacts** — download the `allure-report` zip from any completed Actions run
- **GitHub Pages** — live Allure report published automatically after each successful push to `main` (enable once under *Settings → Pages → Source: GitHub Actions*)

### One-time GitHub Pages setup

1. Go to your repo → **Settings → Pages**
2. Under *Source*, select **GitHub Actions**
3. The next push to `main` will publish the report and print the URL in the `publish-report` job output

---

## Project Structure

```
src/test/java/com/flamingo/qa/
├── configs/
│   ├── ConfigLoader.java          # Reads project.properties (+ -D overrides)
│   ├── ApiConfig.java             # Typed constants for API base URLs / auth
│   └── UiConfig.java              # Typed constants for UI URLs / browser settings
│
├── models/
│   ├── booking/                   # Lombok models: Booking, BookingDates, BookingId, etc.
│   ├── graphql/                   # GraphQLRequest (builder pattern)
│   └── ui/                        # EmployeeRecord, PracticeFormRecord (Lombok @Data @Builder)
│
├── testdata/
│   ├── BookingTestData.java       # Random Booking factory (JavaFaker)
│   ├── EmployeeTestData.java      # Random EmployeeRecord factory
│   └── PracticeFormData.java      # Full / required-only PracticeFormRecord factories
│
├── services/
│   ├── BookingService.java        # REST Assured wrapper for /booking endpoints
│   └── GraphQLService.java        # REST Assured wrapper for GraphQL POST
│
├── pages/                         # Page Object Model (Playwright)
│   ├── PracticeFormPage.java
│   ├── ConfirmationModal.java
│   ├── WebTablesPage.java
│   └── RegistrationFormModal.java
│
├── extensions/
│   ├── ScreenshotOnFailureExtension.java   # AfterTestExecutionCallback → Allure attachment
│   └── TestLoggingExtension.java
│
├── utils/
│   ├── CredentialsDecoder.java    # Base64 decode for credentials in project.properties
│   └── ServiceHealthChecker.java
│
└── tests/
    ├── api/
    │   ├── base/                  # BaseApiTest, BaseBookerTest, BaseGraphQLTest
    │   ├── rest/booker/           # PostBookingTest, GetBookingByIdTest, PutBookingByIdTest,
    │   │                          # PatchBookingByIdTest, DeleteBookingByIdTest, GetBookingIdsTest
    │   └── graphql/               # GraphQLPositiveTest, GraphQLNegativeTest
    └── ui/
        ├── base/BaseUITest.java   # Playwright lifecycle (@BeforeAll/@AfterAll/@BeforeEach/@AfterEach)
        ├── PracticeFormTest.java
        └── WebTablesTest.java

src/test/resources/
├── project.properties             # All URLs, credentials (Base64), browser settings
├── junit-platform.properties      # Parallel execution config
└── files/picture.jpg              # Fixture file for upload test
```

---

## Test Suite Overview

### API — Restful Booker (`@Tag("api")`)

Target: `https://restful-booker.herokuapp.com`

| Class | Coverage |
|-------|----------|
| `PostBookingTest` | `POST /booking` — creates a booking, verifies full response round-trip via recursive comparison |
| `GetBookingByIdTest` | `GET /booking/{id}` — retrieves a freshly created booking, asserts all fields |
| `GetBookingIdsTest` | `GET /booking` — verifies the created booking ID appears in the list; optional filter params |
| `PutBookingByIdTest` | `PUT /booking/{id}` — full replacement, verifies updated fields |
| `PatchBookingByIdTest` | `PATCH /booking/{id}` — partial update, verifies changed fields; unchanged fields intact |
| `DeleteBookingByIdTest` | `DELETE /booking/{id}` — deletes and confirms 404 on subsequent GET |

Auth token is obtained once per test class via `POST /auth` and reused across all requests in that class.

### API — GraphQL / Hygraph Video schema (`@Tag("api")`)

Target: `https://us-east-1-shared-usea1-02.cdn.hygraph.com/content/clpvcopq3aavs01usft1idkgj/master`

| Class | Coverage |
|-------|----------|
| `GraphQLPositiveTest` | Movie list with pagination via `moviesConnection`; single movie by ID; query with named `$slug` variable; nested fields across Movie → Asset (moviePoster) types |
| `GraphQLNegativeTest` | Non-existent ID returns HTTP 200 with `data: null`; malformed query (syntax error) returns HTTP 400 with `errors[]`; non-existent field triggers HTTP 400 schema validation error |

All requests go to a single `POST` endpoint. Because GraphQL always responds with HTTP 200 for application-level errors, tests assert the `data` and `errors` fields rather than HTTP status codes (except for parse/validation errors where Hygraph returns 400).

### UI — Practice Form (`@Tag("ui")`)

Target: `https://demoqa.com/automation-practice-form`

| Test | Type | Coverage |
|------|------|----------|
| `shouldSubmitFormWithRequiredFieldsOnly` | Happy path | Fills mandatory fields only (name, gender, mobile); confirms modal |
| `shouldSubmitFormWithAllFields` | Happy path | Every field including date picker, subject autocomplete, file upload, state/city dropdowns |
| `shouldAcceptEachGenderOption` | Parameterized | Male / Female / Other radio options each submit correctly |
| `shouldSelectMultipleHobbies` | Happy path | All three hobby checkboxes selected simultaneously |
| `shouldHaveNoCityOptionsBeforeStateIsSelected` | State-City dependency | City dropdown is disabled before any state is chosen |
| `shouldShowOnlyCitiesForSelectedState` | Parameterized | Exact city list verified for NCR, Uttar Pradesh, Haryana, Rajasthan |
| `shouldNotSubmitWithInvalidInput` | Parameterized negative | 10 cases: empty form, 5 invalid email formats, 2 short mobile numbers, mobile with letters, mobile with symbols |

### UI — Web Tables (`@Tag("ui")`)

Target: `https://demoqa.com/webtables`

| Test | Coverage |
|------|----------|
| `shouldDisplayDefaultRecords` | Pre-populated rows are all present on fresh page load |
| `shouldAddNewRecord` | Add row; verify all 6 columns via `containsExactly` |
| `shouldEditExistingRecord` | Edit first name and salary; verify updates with `satisfies` |
| `shouldDeleteRecord` | Add then delete; confirm row is gone |
| `shouldFilterByAnyColumn` | Search by each of the 6 column values for the same employee; verify count = 1 each time |
| `shouldShowNoResultsForNonMatchingSearch` | Non-matching term returns zero rows |
| `shouldSortByAge` | Click Age header → assert ascending; click again → assert descending |
| `shouldRestoreAllRowsAfterClearingSearch` | Filter then clear; row count restored |

---

## Configuration

All settings live in `src/test/resources/project.properties`. Any property can be overridden at runtime with a `-D` flag:

```properties
# API
booker.base.url=https://restful-booker.herokuapp.com
booker.username=YWRtaW4=        # Base64("admin")
booker.password=cGFzc3dvcmQxMjM=  # Base64("password123")

graphql.base.url=https://us-east-1-shared-usea1-02.cdn.hygraph.com
graphql.path=/content/clpvcopq3aavs01usft1idkgj/master

# UI
ui.practice.form.url=https://demoqa.com/automation-practice-form
ui.web.tables.url=https://demoqa.com/webtables
ui.headless=true                # Override: -Dui.headless=false
ui.browser=chromium             # Override: -Dui.browser=firefox
ui.viewport.width=1280
ui.viewport.height=800
```

Credentials are stored as Base64 to avoid plaintext secrets in source control; `CredentialsDecoder` decodes them at runtime.

### Parallel execution

Configured in `junit-platform.properties`:
- Test **classes** run concurrently (each gets its own `Playwright` + `Browser` instance).
- Test **methods within a class** run sequentially (they share `@BeforeEach` page state).
- Factor: `dynamic × 2` — scales with available CPU cores.

---

## Test Strategy

### What I prioritised

**API layer first.** REST APIs are fast, deterministic, and give the highest signal per line of test code. Full CRUD coverage for Restful Booker ensures the service contract is verified end-to-end before a single browser is opened.

**GraphQL contract testing.** Beyond the happy path, I deliberately included negative scenarios that probe the API's error handling (Hygraph returns HTTP 400 for syntax and schema-validation errors rather than the spec default of HTTP 200). Tests document this behaviour explicitly so future engineers know it is intentional.

**Page Object Model for all UI.** Each page and modal is a separate class with `@Step`-annotated methods, making Allure reports readable as a prose description of the test. Page objects return `this` for fluent chaining; they never contain assertions.

**Test data isolation.** Every test that writes data creates its own random records via JavaFaker factories (`BookingTestData`, `EmployeeTestData`, `PracticeFormData`). No test depends on pre-existing server state, so they are safe to run in any order, in parallel, or repeatedly against a reset environment.

**Positive and negative coverage in every suite.** Each feature has both a happy path and at least one validation/error path. The parameterized negative test for the Practice Form covers 10 distinct invalid-input scenarios with a single test method, keeping the structure DRY.

---

## Challenges & Solutions

**1. Screenshots not appearing in Allure**
`TestWatcher.testFailed()` fires _after_ `@AfterEach`, which closes the Playwright `Page`. By then the screenshot call throws because the context is already closed. Switching to `AfterTestExecutionCallback` — which fires _before_ `@AfterEach` — solved it cleanly.

**2. Allure serve binding to IPv6 instead of localhost**
On macOS, `localhost` resolves to `::1` (IPv6) but the Allure server was binding to `127.0.0.1` (IPv4), causing the browser to get a connection refused. Fixed by adding `<allure.serve.host>127.0.0.1</allure.serve.host>` to Maven `<properties>`.

**3. CSS validation highlight timing in Playwright**
After clicking Submit with invalid input, the browser applies the red border asynchronously. A plain `isFieldInvalid()` check ran before the style was applied and returned `false`. Replaced with an Awaitility fluent wait (`pollInterval 1s / atMost 5s / pollInSameThread`). `pollInSameThread()` is required because Playwright's `Page` is not thread-safe.

**4. DemoQA ads overlaying the Submit button**
Banner ads at the bottom of the page covered the Submit button, causing Playwright click interception errors. Fixed by calling `scrollIntoViewIfNeeded()` before every submit click.

**5. Restful Booker periodic data reset**
The public API resets all bookings every 10 minutes. Tests that assumed pre-existing records were fragile. All tests now create their own booking in the test body and reference it by the returned ID.

**6. City dropdown `disabled` detection**
The DemoQA React Select component does not use the native HTML `disabled` attribute. It sets `aria-disabled="true"` on an inner `div[class*='-control']`. Using the correct attribute selector fixed the flaky `isCityDisabled()` check.

---

## What I Would Add With More Time

**Tests**
- Booking date edge cases: past check-in, check-out before check-in, same-day in/out
- `GET /booking` with filter params (`firstname`, `lastname`, `checkin`, `checkout`)
- GraphQL mutation testing (if a writable GraphQL API were available)
- Pagination in Web Tables (change rows-per-page to 5, add 6 rows, verify page controls appear)
- Cross-browser matrix for UI tests (Firefox, WebKit) via parameterized `BaseUITest`
- Accessibility assertions (`aria-label` presence, keyboard navigation through the form)

**Infrastructure**
- GitHub Actions workflow: `mvn clean test` on push + PR, Allure report published to GitHub Pages
- Docker `Dockerfile` for hermetic runs (no host Java/Maven required in CI)
- Test retry extension (`@RetryingTest`) for the handful of DemoQA tests that are sensitive to ad-network latency
