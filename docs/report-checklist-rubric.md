# Report checklist (alignment with common SOEN project rubrics)

Use this as a **Table of Contents** guide and **self-audit** before submission. Adjust section numbers to your instructor’s template.

## Professional report structure (≈30 marks quality)

| Item | Done? | Notes |
|------|-------|--------|
| Title page, course code, team members, date | ☐ | |
| Table of contents | ☐ | |
| List of figures / tables | ☐ | If you have many diagrams |
| Page numbers | ☐ | |
| References (APA/IEEE per course) | ☐ | Spring, React, Render, Neon docs |
| Consistent headings | ☐ | |

## Requirements analysis & design (typical 15 marks area)

| Item | Done? | Where in repo / docs |
|------|-------|----------------------|
| Functional requirements listed & justified | ☐ | `docs/requirements.md` |
| Non-functional requirements | ☐ | Same + architecture |
| Architecture diagram | ☐ | `docs/architecture.md` (Mermaid → PNG) |
| Database design | ☐ | `docs/database-design.md` |
| UML: **use case** | ☐ | `docs/uml-diagrams.md` |
| UML: **class** | ☐ | Same |
| UML: **sequence** | ☐ | Same (2 diagrams provided) |

## Implementation & quality (functional / non-functional)

| Item | Done? | Evidence |
|------|-------|----------|
| Cloud deployment described | ☐ | Screenshots Vercel + Render + Neon |
| Link to live app | ☐ | URL in report |
| Security / config (HTTPS, env vars) | ☐ | Short subsection |

## Software testing & QA (often 40 marks)

| Item | Done? | Evidence |
|------|-------|----------|
| **Test plan & strategy** (unit, integration, system) | ☐ | `docs/testing-strategy.md` |
| **Test case documentation** + **results** | ☐ | Tables + pass/fail |
| **Screenshots** of tests / CI | ☐ | GitHub Actions green; `mvn test` / `npm run test:ci` |
| **JUnit 5** mentioned explicitly | ☐ | Backend section |
| **CI/CD** (GitHub Actions) | ☐ | Screenshot + link to workflow file |
| **Version control** (GitHub) | ☐ | Repo URL, branch strategy in 1 paragraph |

## Project report mandatory sections (course PDF)

| Section | Done? |
|---------|-------|
| **Software development method** (Scrum / XP / Lean + SDLC) | ☐ | `docs/development-methodology.md` |
| **Software testing method** (unit/component + functional/acceptance + **results**) | ☐ | `docs/testing-strategy.md` + screenshots |
| **Tools:** CI/CD, GitHub, JUnit, IDE | ☐ | Integrate into methodology + testing chapters |

## Suggested report outline (copy to Word)

1. Introduction & objectives  
2. Requirements (functional / non-functional)  
3. System design (architecture + database + UML)  
4. Implementation overview (stack, deployment)  
5. Testing (strategy, cases, tools, results, CI)  
6. Methodology & team process  
7. Conclusion & future work  
8. References  
9. Appendices (extra screenshots, long tables)

---

**Team:** Split writing: one person drafts diagrams, one drafts testing, one drafts deployment screenshots, one merges and formats.
