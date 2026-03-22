# Software testing — plan, strategy, and mapping to the codebase

This section supports the **course rubric**: unit & component tests, functional/acceptance tests, **JUnit 5**, **GitHub Actions**, and documentation with **results + screenshots**.

## 1. Test strategy overview

| Level | Goal | Tools |
|-------|------|--------|
| **Unit** | Isolated logic and services | JUnit 5, Spring Boot test slice / `@SpringBootTest` with H2 |
| **Component** (API) | Controllers + HTTP contract | MockMvc, `@SpringBootTest` |
| **Integration / functional** | Full API stack, real port | `@SpringBootTest(webEnvironment = RANDOM_PORT)`, `RestTemplate` |
| **Frontend unit / component** | UI behavior, API client | Jest, React Testing Library |
| **System / acceptance** | Deployed app (manual or E2E) | Manual test script + screenshots; optional Playwright/Cypress |

## 2. Backend test inventory (JUnit 5)

*Paths are under `project/src/test/java/...`*

| Test class | Type | What it covers |
|------------|------|----------------|
| `ProjectApplicationTests` | Smoke | Spring context loads |
| `UserServiceTest` | Unit / service | User service behavior |
| `EventServiceTest` | Unit / service | Past → PASSED, FILLED when 0 tickets, etc. |
| `UserControllerTest` | Component | Login/register HTTP responses |
| `EventControllerTest` | Component | `GET /api/events`, `GET /api/events/{id}` |
| `ReservationControllerTest` | Component | Zero tickets, past event, success path |
| `ApiIntegrationTest` | Functional / integration | Register → login → GET events; full reserve flow |

**Database in tests:** H2 in-memory (`src/test/resources/application.properties`) — no live Neon required for CI.

## 3. Frontend test inventory (Jest + RTL)

*Paths under `frontend/src/__tests__/...`*

| Area | Files (examples) |
|------|------------------|
| App / Welcome | `App.test.js`, `Welcome.test.js` |
| Auth | `SignIn.test.js`, `SignUp.test.js` |
| Events / reserve | `EventsList.test.js`, `EventDetail.test.js`, `Reserve.test.js` |
| Admin / reservations | `AdminEvents.test.js`, `MyReservations.test.js` |
| API client | `services/api.test.js` |

## 4. CI/CD (GitHub Actions)

Workflow: `.github/workflows/ci.yml`

- Job **backend-tests:** JDK 17 → `mvn test` in `project/`
- Job **frontend-tests:** Node 20 → `npm install` → `npm run test:ci` in `frontend/`

**Report screenshots to capture:**

1. Green workflow run on GitHub (Actions tab).
2. Local: `mvn test` summary (BUILD SUCCESS).
3. Local: `npm run test:ci` (all tests passed).

## 5. Sample test case table (for report appendix)

| ID | Objective | Steps | Expected | Actual / Screenshot |
|----|-----------|-------|----------|---------------------|
| TC-01 | Customer can register | POST /api/users/register with valid JSON | 201 | *(paste)* |
| TC-02 | Cannot reserve past event | POST reservation for past `startDateTime` | 409 | *(paste)* |
| TC-03 | CI pipeline passes | Push to `main` | Both jobs green | *(GitHub screenshot)* |

Fill **Actual** after each sprint or release.

## 6. Functional vs acceptance (wording for the report)

- **Functional tests (automated):** API integration tests exercising multiple endpoints and persistence (`ApiIntegrationTest`).
- **Acceptance tests:** Validate “the system does what the user needs” — often demonstrated with **manual scripts** on the **deployed** Vercel + Render URLs, with **screenshots** (login, browse, reserve, admin). If the rubric requires browser automation, add a note that Cypress/Playwright can be a future work item.

---

## 7. How this maps to course wording

| Course asks for | Where it is |
|-----------------|-------------|
| Unit & component tests | JUnit: `*ServiceTest`, `*ControllerTest`; Jest: components |
| Functional & acceptance tests | `ApiIntegrationTest` + manual E2E on production |
| JUnit 5 | All `*.java` tests use JUnit 5 |
| GitHub Actions | `.github/workflows/ci.yml` |
