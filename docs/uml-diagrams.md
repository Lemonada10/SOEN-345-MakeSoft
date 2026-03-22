# UML diagrams (MakeSoft)

Render these with **Mermaid** ([mermaid.live](https://mermaid.live)) or a VS Code extension, then **export to PNG/SVG** for the report.

---

## 1. Use case diagram

High-level actors: **Guest**, **Customer**, **Admin/Instructor**, **System** (email).

```mermaid
flowchart LR
  subgraph actors [Actors]
    G[Guest]
    C[Customer]
    A[Admin or Instructor]
  end
  subgraph system [MakeSoft System]
    UC1[Browse events]
    UC2[Filter search events]
    UC3[Sign up]
    UC4[Sign in]
    UC5[Reserve tickets]
    UC6[View or cancel reservations]
    UC7[CRUD events]
    UC8[Receive email confirmation]
  end
  G --> UC1
  G --> UC2
  G --> UC3
  G --> UC4
  C --> UC1
  C --> UC2
  C --> UC4
  C --> UC5
  C --> UC6
  A --> UC7
  UC3 --> UC8
```

*For a classic UML use case oval diagram, redraw in Draw.io / Lucidchart using the same use cases.*

---

## 2. Class diagram (simplified domain + API)

Focus on main domain classes and controllers (adjust names to match your packages).

```mermaid
classDiagram
  class User {
    -Long id
    -String email
    -String role
  }
  class Event {
    -Long id
    -String name
    -Date startDateTime
    -String status
    -String ticketRemaining
  }
  class Reservation {
    -Long reservationId
    -int quantity
    -String status
  }
  User "1" --> "*" Reservation : makes
  Event "1" --> "*" Reservation : bookedFor
  class UserController
  class EventController
  class ReservationController
  class UserService
  class EventService
  class ReservationService
  UserController --> UserService
  EventController --> EventService
  ReservationController --> ReservationService
```

---

## 3. Sequence diagram — Reserve tickets (happy path)

```mermaid
sequenceDiagram
  participant U as Customer browser
  participant API as ReservationController
  participant S as ReservationService
  participant ER as EventRepository
  participant RR as ReservationRepository
  U->>API: POST /api/reservations JSON
  API->>ER: load Event by id
  ER-->>API: Event
  API->>API: validate date tickets
  API->>ER: save updated ticket count
  API->>S: addReservation(...)
  S->>RR: save Reservation
  RR-->>S: ok
  S-->>API: ok
  API-->>U: 201 Created
```

---

## 4. Sequence diagram — Sign up + confirmation email

```mermaid
sequenceDiagram
  participant U as User browser
  participant UC as UserController
  participant US as UserService
  participant NS as NotificationService
  participant Mail as Email provider
  U->>UC: POST /api/users/register
  UC->>US: register user
  US-->>UC: User created
  UC->>NS: sendSignUpConfirmation
  NS->>Mail: send email optional
  Mail-->>NS: ok or skipped
  UC-->>U: 201 Created
```

---

**Report tip:** Add **one paragraph** under each figure explaining what the diagram shows and how it supports the requirements.
