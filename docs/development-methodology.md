# Software development method & lifecycle

**Instructions:** Choose **one** primary method below and customize with your team’s real practices (sprint length, ceremonies, tools). This is a template for the report section required by the course.

---

## Option A — Scrum (common for course projects)

**Method:** Scrum is an iterative Agile framework with fixed-length **sprints**, a prioritized **product backlog**, and regular **inspect-and-adapt** cycles.

**Roles (mapped to a student team):**

- *Product Owner proxy:* often a team lead or rotating member aligning with instructor requirements.
- *Scrum Master:* facilitates standups and removes blockers (e.g. deployment issues).
- *Developers:* all members contribute to design, implementation, tests, documentation.

**Ceremonies (example):**

| Ceremony | Frequency | Purpose |
|----------|-----------|---------|
| Sprint planning | Start of sprint | Select backlog items for the sprint |
| Daily standup | Daily / 2–3× weekly | Progress, blockers |
| Sprint review | End of sprint | Demo to stakeholders / instructor |
| Retrospective | End of sprint | What went well / improve |

**Lifecycle:** Requirements → Sprint backlog → Design & implement → Test → Integrate (GitHub) → Deploy (Vercel/Render) → Review → Next sprint.

**Artifacts:** User stories, GitHub issues/projects, pull requests, CI runs.

---

## Option B — Lightweight Kanban / iterative

**Method:** Continuous flow from backlog → Doing → Done with WIP limits; less ceremony than full Scrum.

**Use if:** Your team did not run formal sprints but used a **GitHub Project board** and **issues**.

---

## Option C — Extreme Programming (XP) practices (partial)

You can cite **specific practices** without claiming full XP:

- **Test-first / high test coverage** — JUnit + Jest + CI.
- **Continuous integration** — GitHub Actions on every push.
- **Small releases** — frequent merges to `main`.
- **Collective ownership** — shared repo, code review via PRs.

---

## What to write in the report (checklist)

1. **Name** the method (e.g. “We used Scrum with two-week sprints”).
2. **Describe** 2–3 ceremonies or habits you actually did (standups, GitHub issues, PR reviews).
3. **Connect** to tools: GitHub (version control), Actions (CI), IDE (IntelliJ / VS Code / Cursor).
4. **One diagram:** simple timeline or sprint calendar image (optional).

---

*Disclaimer: Adapt dates, sprint count, and team size to your real experience. Plagiarism policies apply — write in your own words.*
