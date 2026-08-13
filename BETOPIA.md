# OSMS (Online Schedule Management System) — Agent Guide

## Project Type

Java EE 10 (Jakarta EE 5.0) web app — Eclipse Dynamic Web Project, deploys to **Apache Tomcat 10.1**, runs on **Java 21**. No Maven/Gradle; managed entirely through Eclipse `.project` / `.classpath` / `.settings`.

---

## Essential Commands

There are no build scripts or CI configs. Development happens in Eclipse with Tomcat 10.1 as the target runtime (see `.classpath`). Common workflow:

| Task | How |
|------|-----|
| Build | Eclipse auto-builds to `build/classes` (see `.classpath` output path) |
| Run | Deploy WAR to Tomcat 10.1 from Eclipse Run Configurations |
| Debug | Eclipse debug configuration pointing to Tomcat 10.1 |

The app connects to **OracleXE** (`localhost:1521/xe`, user `c##osms`) via JDBC. The connection is lazily instantiated in `helper/ConnectionProvider.java`.

---

## Architecture

### Layered Request Flow

```
Browser ──► JSP pages ──► Servlets (@WebServlet) ──► Filter (AuthFilter)
                                                    │
                              ┌─────────────────────┼─────────────────────┐
                              ▼                     ▼                     ▼
                       Entity models         Algorithm (CSP)      *Data loaders
                      (entity.*)              (algorithm.*)        (local_db.*)
                              │                     ▲
                              ▼                     │
                       CSV exporters          CSPSolver (static)
                   (helper/CSV*.java)           Main.state (static)
```

### Package Map

| Package | Responsibility |
|---------|---------------|
| `entity.*` | Domain models — `Teacher`, `Course`, `Section`, `Room`, enums (`CourseType`, `LabType`, `RoomType`) |
| `algorithm.*` | CSP-based timetable solver — variables, domains, backtracking with forward checking, heuristics, soft-constraint scoring |
| `local_db.*` | Oracle data loaders — `CourseData`, `TeacherData`, `SectionData`, `RoomData` — all use static caches |
| `servlets.*` | Controllers — CRUD for courses/rooms/teachers, schedule generation, progress polling, CSV/AI-prompt downloads |
| `helper.*` | `ConnectionProvider` (DB singleton), `CSVRoutineExporter`, `CSVTeacherScheduleExporter` |
| `filters.*` | `AuthFilter` — gates `/dashboard/*` by `dept_type` session attribute |
| `ai_prompt.*` | AI prompt generator for manual scheduling assistance on unassigned courses |

### Data Flow: Schedule Generation

1. User hits `GenerateRoutineServlet` → reads `efficiency` param ("low"/"medium"/"high"/default)
2. Loads all entities from Oracle via `*Data` classes (static caches)
3. `Main.main()` builds `CSPState` with variables (one per course-section pair) and domains
4. `CSPSolver.solve()` runs backtracking search in a **new Thread**
5. Progress polled via `GetRoutineProgressServlet` (AJAX)
6. Best solution restored to `Main.state`
7. Export via CSV or AI prompt download

---

## Core Conventions

### Time Representation

- **28 time slots per day**, 30 minutes each, starting at 09:00 (slot 0 = 09:00–09:30)
- Days: Saturday(0) through Friday(6)
- Occupations stored as `long[]` (7 longs, one per day) — bitmask where bit N = slot N
- Slots 6–7 are **hard-blocked lunch hours** in domain generation

### Entity Patterns

- `Teacher`: `ArrayList<Boolean>` availability (not `boolean[]`), index 0=Saturday
- `Room`: `long[]` availability bitmask (not `ArrayList`), `LabType` nullable for non-lab rooms
- `Course`: references sections/teachers by `Set<String>` / `Set<Integer>` IDs, not object references
- `Variable.id` format: `{courseId}_{sectionId}_{1|2|LAB}`

### Constraint Architecture

- **Hard constraints** → enforced in `CSPSolver.consistent()` (teacher availability, room capacity, time overlap, room type, lab type)
- **Soft constraints** → scored in `SoftConstraints.score()` (teacher load balance, section day spread, max hours, preferred teacher)
- **`teacher.maxSlotHours`** is a **soft constraint** (penalty of 60), NOT a hard constraint
- Variables that cannot be assigned are marked **TBA (skipped)** for partial solutions
- Best solution tracking: static `bestAssignment` / `bestScore` / `bestSkipped` in `CSPSolver`

### CSP Solver Details

- MRV + degree heuristic via `Heuristics.selectMRVDegree()`
- Forward checking via `ForwardChecker.prune()` / `restore()`
- `MAX_NODES` varies by efficiency: low=1M, medium=1M, high=3M, default=5M
- Progress increments by 10% every `divisor` nodes (not continuous 0–100)

### Servlet Patterns

- All use `@WebServlet` annotation (no `web.xml` mappings)
- CRUD operations follow: Add → Edit → Delete pattern per entity
- `doGet()` is used everywhere (no `doPost()`)

### JSP Structure

- Dashboard includes: `nav_bar.jsp` (top), `footer.jsp` (bottom) — use `<jsp:include>`
- Dashboard JSPs live in `src/main/webapp/dashboard/`
- Error fallback: `NullPointerException` and `500` errors → `no_solution.jsp`

---

## Critical Gotchas

### Thread Safety (DO NOT BREAK)

Everything is **single-threaded by assumption**:
- `CSPSolver` fields are **static and NOT volatile** — `isSolverRunning` can race
- `Main.state` is static — overwrites on each generation call
- `ConnectionProvider.getCon()` has a non-atomic null check
- The solver runs in a raw `new Thread()` — no thread pool, no executor

**Adding concurrency requires rewriting the solver state into instance fields.**

### SQL Injection in Data Loaders

`CourseData.getCourses()` concatenates `dept_type` directly into SQL. The CRUD servlets use parameterized `PreparedStatement` (safe), but data loading does not.

### Login

- `LogInServlet` validates against `admins` table with **plain-text passwords**
- Auth is session-attribute-based (`dept_type`), not session-expiry-based
- `AuthFilter` only checks if `dept_type` exists in session — no expiration check

### Empty Lib Directory

`src/main/webapp/WEB-INF/lib/` is **empty**. Dependencies (Jakarta Servlet API, Oracle JDBC) are provided by the Tomcat runtime, not packaged in the WAR. Do not expect a `pom.xml` or `build.gradle`.

### Soft Constraint Caveat

`SoftConstraints.preferredTeacherPenalty()` is **all commented out** (returns 0). Preferred teachers are handled as a **hard filter** in domain generation (`Main.generateDomains()`), not as a soft constraint score.

---

## Adding a New Entity or CRUD Operation

1. Create entity class in `entity/` with constructor (no getters/setters — fields are package-private or accessed directly)
2. Create `local_db/EntityData.java` with:
   - `static Map<String, Entity>` or `static Map<Integer, Entity>` cache
   - `getEntities(HttpSession)` method that queries `entities` table filtered by `dept_type`
3. Create servlets: `AddEntityServlet`, `EditEntityServlet`, `DeleteEntityServlet`
4. Create JSP pages in `dashboard/` for listing and CRUD forms
5. Register servlets with `@WebServlet` annotation

---

## Modifying the CSP Solver

- `CSPSolver.consistent()` → hard constraint checks (bitmask overlap)
- `SoftConstraints.score()` → penalty scoring for solution ranking
- `ForwardChecker.prune()` → domain reduction during search
- `Heuristics.selectMRVDegree()` → variable ordering
- **Never** modify static fields without considering the single-threaded assumption

---

## File Locations Quick Reference

| What | Where |
|------|-------|
| Domain models | `src/main/java/entity/` |
| Solver core | `src/main/java/algorithm/` |
| DB loaders | `src/main/java/local_db/` |
| Servlets | `src/main/java/servlets/` |
| Auth filter | `src/main/java/filters/` |
| Helpers (DB, CSV) | `src/main/java/helper/` |
| AI prompt system | `src/main/java/ai_prompt/` |
| JSP pages | `src/main/webapp/` and `src/main/webapp/dashboard/` |
| Web descriptor | `src/main/webapp/WEB-INF/web.xml` |
| Build output | `build/classes/` |
