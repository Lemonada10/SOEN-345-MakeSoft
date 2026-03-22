# Requirements (functional & non-functional)

Use this as a checklist. Mark **Implemented / Partial / N/A** in your report and tie each item to tests or screenshots.

## 1. Functional requirements

| ID | Requirement | Notes |
|----|-------------|--------|
| FR-1 | **User registration** with email, phone, password, role (customer vs admin) | |
| FR-2 | **Email confirmation** after sign-up (when mail/Resend is configured) | Optional path if not configured |
| FR-3 | **User login** with email or phone + password | |
| FR-4 | **List events** with optional filters (date, location, category, status) | |
| FR-5 | **View event details** | |
| FR-6 | **Reserve** tickets for a future event with available inventory | |
| FR-7 | **Reject reservation** for past events or insufficient tickets | |
| FR-8 | **Update event status** to FILLED when tickets reach zero; PASSED when event date is in the past | |
| FR-9 | **Cancel reservation** (customer) | |
| FR-10 | **Admin/Instructor:** create, edit, delete events | |
| FR-11 | **REST API** consumed by SPA (`/api/users`, `/api/events`, `/api/reservations`) | |

## 2. Non-functional requirements

| ID | Category | Requirement | How addressed (fill in report) |
|----|----------|-------------|--------------------------------|
| NFR-1 | **Deployment** | System runs on cloud (e.g. Vercel + Render + Neon) | |
| NFR-2 | **Availability** | Service usable after cold start; graceful messaging | Retries, wake-up ping, user messages |
| NFR-3 | **Performance** | Reasonable response under free-tier limits | Document limitations of free tier |
| NFR-4 | **Security** | HTTPS, secrets in env vars, no secrets in repo | |
| NFR-5 | **Maintainability** | Layered architecture, tests, CI | GitHub Actions, JUnit, Jest |
| NFR-6 | **Usability** | Clear flows for sign-in, browse, reserve | Screenshots in report |

## 3. Traceability (example table for the report)

| Requirement | Test / evidence |
|-------------|-----------------|
| FR-6, FR-7 | `ReservationControllerTest`, `ApiIntegrationTest`, manual screenshot |
| FR-8 | `EventServiceTest`, UI showing PASSED / FILLED |
| NFR-5 | Link to CI workflow + test summary screenshot |

---

**Team action:** Replace notes with your own acceptance criteria from the official project PDF if it differs.
