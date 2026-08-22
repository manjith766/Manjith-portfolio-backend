# Portfolio Backend — Projects Module

This is Module 1 of the backend (see architecture doc for the full 7-phase
plan). It is a **complete, independently runnable** Spring Boot application
covering the `projects` aggregate end to end. Skills/Categories CRUD,
Experience, Education, Blog, Auth, and the rest will be added as their own
modules on top of this same codebase — nothing here will need to be
rewritten when that happens.

## Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 15+ running locally (or update `DB_URL` env var)

## Running locally

```bash
createdb portfolio_db
export DB_URL=jdbc:postgresql://localhost:5432/portfolio_db
export DB_USERNAME=portfolio_user
export DB_PASSWORD=changeme
mvn spring-boot:run
```

Flyway runs `V1__initial_schema.sql` and `V2__seed_data.sql` automatically
on startup. **Before running against anything but a throwaway local DB**,
replace the placeholder bcrypt hash in `V2__seed_data.sql` with a real one:

```java
new BCryptPasswordEncoder().encode("yourRealPassword")
```

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Running tests

```bash
mvn test
```

## Sample requests / responses

### Create a project — `POST /api/admin/projects`

Request:

```json
{
  "title": "Multi-Vendor E-Commerce Platform",
  "description": "A production-grade multi-vendor e-commerce backend with Customer/Seller/Admin RBAC, a full cart-to-order workflow, and a coupon engine handling real edge cases.",
  "shortDescription": "Multi-vendor e-commerce backend with RBAC and transactional order workflow",
  "categoryId": 1,
  "githubUrl": "https://github.com/manjith766/ecommerce-platform",
  "liveDemoUrl": null,
  "coverImageUrl": "https://res.cloudinary.com/demo/image/upload/v1/cover.png",
  "isFeatured": true,
  "displayOrder": 1,
  "skillIds": [1, 2, 3],
  "features": [
    "20+ production-grade REST APIs",
    "Customer/Seller/Admin RBAC",
    "Transactional cart-to-order workflow",
    "AES-encrypted image moderation"
  ],
  "images": [
    { "imageUrl": "https://res.cloudinary.com/demo/image/upload/v1/screen1.png", "altText": "Dashboard", "displayOrder": 0 }
  ]
}
```

Response — `201 Created`, `Location: /api/projects/multi-vendor-e-commerce-platform`:

```json
{
  "id": 1,
  "title": "Multi-Vendor E-Commerce Platform",
  "slug": "multi-vendor-e-commerce-platform",
  "description": "A production-grade multi-vendor e-commerce backend...",
  "shortDescription": "Multi-vendor e-commerce backend with RBAC and transactional order workflow",
  "category": { "id": 1, "name": "Backend", "slug": "backend" },
  "githubUrl": "https://github.com/manjith766/ecommerce-platform",
  "liveDemoUrl": null,
  "coverImageUrl": "https://res.cloudinary.com/demo/image/upload/v1/cover.png",
  "isFeatured": true,
  "displayOrder": 1,
  "viewCount": 0,
  "createdAt": "2026-07-25T10:30:00Z",
  "updatedAt": "2026-07-25T10:30:00Z",
  "images": [
    { "id": 1, "imageUrl": "https://res.cloudinary.com/demo/image/upload/v1/screen1.png", "altText": "Dashboard", "displayOrder": 0 }
  ],
  "features": [
    "20+ production-grade REST APIs",
    "Customer/Seller/Admin RBAC",
    "Transactional cart-to-order workflow",
    "AES-encrypted image moderation"
  ],
  "skills": [
    { "id": 1, "name": "Java", "iconUrl": null },
    { "id": 2, "name": "Spring Boot", "iconUrl": null },
    { "id": 3, "name": "PostgreSQL", "iconUrl": null }
  ]
}
```

### List projects — `GET /api/projects?featured=true&page=0&size=12`

```json
{
  "content": [
    {
      "id": 1,
      "title": "Multi-Vendor E-Commerce Platform",
      "slug": "multi-vendor-e-commerce-platform",
      "shortDescription": "Multi-vendor e-commerce backend with RBAC and transactional order workflow",
      "coverImageUrl": "https://res.cloudinary.com/demo/image/upload/v1/cover.png",
      "categoryName": "Backend",
      "isFeatured": true,
      "viewCount": 0,
      "skillNames": ["Java", "Spring Boot", "PostgreSQL"]
    }
  ],
  "pageNumber": 0,
  "pageSize": 12,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

### Error shape (all endpoints)

```json
{
  "apiPath": "/api/admin/projects",
  "errorCode": "PRJ-409",
  "errorMessage": "A project resolving to slug 'multi-vendor-e-commerce-platform' already exists. Use a different title.",
  "timestamp": "2026-07-25T10:31:12.123Z"
}
```

## Query strategy (why there's no N+1 here)

Full breakdown lives in Javadoc on `ProjectRepository` and
`ProjectServiceImpl`, summarized:

| Endpoint | Queries | Why |
|---|---|---|
| `GET /api/projects` (list) | 2 fixed | 1 page query with `@EntityGraph(category)` + 1 batched query for skills across the whole page (`hibernate.default_batch_fetch_size: 20`) |
| `GET /api/projects/{slug}` (detail) | 4 fixed | 1 for project+category, +1 each for images/features/skills — kept separate deliberately to avoid a multi-collection `JOIN FETCH` Cartesian product / `MultipleBagFetchException` |
| `POST /api/admin/projects` | 4 fixed | slug check, category lookup, skills batch lookup, insert (cascades) |
| `PATCH /api/projects/{slug}/view` | 1 | atomic `UPDATE ... SET view_count = view_count + 1`, no read-modify-write race |

None of these scale with the number of images/features/skills on a
project, or with the page size beyond the two fixed queries above — that's
the N+1 guarantee, not just an aspiration.

## Recommended additional indexes (not yet in the migration)

If full-text project search (`?search=`) becomes slow at scale, add:

```sql
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_projects_title_trgm ON projects USING gin (title gin_trgm_ops);
CREATE INDEX idx_projects_description_trgm ON projects USING gin (description gin_trgm_ops);
```

Not added by default since `pg_trgm` requires superuser/extension
privileges some managed Postgres hosts (Render's free tier included)
restrict — confirm before adding to a migration that runs on deploy.

## Skills module (Module 3)

Unlike Projects, skills are a small bounded collection (dozens, not
thousands over a career) — so `GET /api/skills` returns a plain list, no
pagination, sorted by `display_order`. Category validation mirrors
Projects: a skill's `categoryId` must resolve to a category whose `type`
is `SKILL` (not `PROJECT` or `BLOG`), enforced in `SkillServiceImpl`.

`SkillResponseDTO.projectTitles` shows which projects use a given skill —
the "Projects used" field from the master spec — via the inverse side of
the existing `project_skills` join table, batch-fetched the same way as
everything else (no N+1; see `SkillServiceImpl` Javadoc for the exact
query count per endpoint).

Refactor note: `CategorySummaryResponseDTO`/`SkillSummaryResponseDTO`
mapping was extracted into a new `CommonMapper`, used by both
`ProjectMapper` and `SkillMapper` — that mapping is now defined exactly
once instead of duplicated per module, and future modules (Experience)
will reuse it too.

## Modules built so far

| Module | Status | Public endpoints | Admin endpoints |
|---|---|---|---|
| Auth | ✅ | `POST /api/auth/login`, `/refresh`, `/logout` | — |
| Projects | ✅ | `GET /api/projects`, `GET /api/projects/{slug}`, `PATCH /api/projects/{slug}/view` | `POST/PUT/DELETE /api/admin/projects` |
| Skills | ✅ | `GET /api/skills?categorySlug=`, `GET /api/skills/{id}` | `POST/PUT/DELETE /api/admin/skills` |
| Experience | ✅ | `GET /api/experience`, `GET /api/experience/{id}` | `POST/PUT/DELETE /api/admin/experience` |
| Education | ✅ | `GET /api/education`, `GET /api/education/{id}` | `POST/PUT/DELETE /api/admin/education` |
| Certifications | ✅ | `GET /api/certifications`, `GET /api/certifications/{id}` | `POST/PUT/DELETE /api/admin/certifications` |
| Blog | ✅ | `GET /api/blog`, `GET /api/blog/{slug}`, `PATCH .../view`, `PATCH .../like`, `GET/POST /api/blog/{slug}/comments` | `GET/POST/PUT/DELETE /api/admin/blog`, `GET /api/admin/blog/comments/pending`, `PATCH .../approve`, `DELETE .../{id}` |
| Messages | ✅ | `POST /api/messages` | `GET /api/admin/messages`, `GET .../unread-count`, `PATCH .../{id}/read`, `DELETE .../{id}` |
| Resume | ✅ | `GET /api/resume`, `GET /api/resume/download` | `POST /api/admin/resume` (multipart), `GET .../versions`, `DELETE .../{id}` |
| Settings | ✅ | `GET /api/settings` | `GET/PUT/DELETE /api/admin/settings` |
| SEO | ✅ | `GET /api/seo?pagePath=` | `GET/PUT/DELETE /api/admin/seo` |
| Social Links | ✅ | `GET /api/social-links` | `POST/PUT/DELETE /api/admin/social-links` |
| Analytics/Visitor Logs | ✅ | `POST /api/analytics/track` | `GET /api/admin/analytics/visitor-logs`, `/daily`, `/summary` |

**All backend modules from the original spec are now built.** Auth (JWT + roles), Projects, Skills, Experience, Education, Certifications, Blog, Messages, Resume, Settings/SEO/Social Links, and Analytics — 11 modules, 193 Java files, 3 Flyway migrations.

### Resume — design decisions worth knowing

- **New migration required**: `V3__add_resume_cloudinary_public_id.sql` adds a `cloudinary_public_id` column. The original schema only stored `file_url`, but deleting a file from Cloudinary requires its public ID, not just the URL — without this column there'd be no way to ever clean up a superseded file.
- **Cloudinary is behind an interface** (`FileStorageService`), not called directly from `ResumeServiceImpl`. `CloudinaryFileStorageService` is the *only* class in the codebase that imports the Cloudinary SDK — swapping providers later means writing a new implementation, not touching business logic.
- **Every upload creates a new row**, never overwrites one — `version` increments, the old row is deactivated via a single bulk `UPDATE` (not load-then-save) in the same transaction as the new insert, so the DB's partial unique index (`uq_resume_single_active`, one active row at a time) is never briefly violated.
- **Old files are NOT auto-deleted from Cloudinary** on new upload — they stay downloadable via `GET /api/admin/resume/versions` until an admin explicitly deletes that version. Deliberate "keep history" choice; flip it in `ResumeServiceImpl` if you'd rather old files vanish automatically.
- **The active resume cannot be deleted directly** — `DELETE /api/admin/resume/{id}` on the active version returns 409; you have to upload a replacement first (which demotes it), then delete the old one. Prevents ever being left with zero active resumes.
- **`GET /api/resume/download` is a 302 redirect** to the Cloudinary URL, not a JSON response — so it works as a plain `<a href="/api/resume/download">Download Resume</a>` with no JS required.
- **Cloudinary delete failures don't fail the surrounding request** — an orphaned file in storage is a cleanup nuisance, not a correctness problem; failing the whole delete-version call over it would be worse. Logged at ERROR for manual follow-up.

Required environment variables: `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` — all default to `changeme` in `application.yml`, which will fail authentication against Cloudinary's API until set for real.

### Skills — business rules worth knowing

- `categoryId` on a skill must reference a category of `type=SKILL` — same `CATEGORY_INVALID_TYPE` pattern as Projects, just checked against a different `CategoryType`.
- Skill names are unique (`uq_skills_name`); duplicate create/rename throws `DUPLICATE_SKILL_NAME` (409).
- No pagination on `GET /api/skills` — a career's skill list is bounded (dozens, not thousands), unlike Projects which can grow indefinitely. If that assumption stops holding, add pagination the same way Projects does it.
- `SkillResponseDTO.projectTitles` shows which projects use a given skill — powered by the inverse `Skill.projects` collection, batch-fetched the same way as everywhere else (no N+1).
- Deleting a skill cascades to remove it from `project_skills` and `experience_skills` (DB-level `ON DELETE CASCADE`) — the skill silently disappears from any project/experience card that referenced it. No confirmation step exists for this; add one at the frontend/admin-UX level if that's a concern, since blocking it at the API level wasn't requested and would need a "force" flag to be usable at all.

### Experience — business rules worth knowing

- Three-way validation between `isCurrent`/`endDate`/`startDate` that a single Jakarta annotation can't express, enforced in the service layer:
  - `isCurrent=true` and a non-null `endDate` → `CURRENT_ROLE_WITH_END_DATE` (400)
  - `isCurrent=false` and a null `endDate` → `PAST_ROLE_MISSING_END_DATE` (400)
  - `endDate` earlier than `startDate` → `INVALID_DATE_RANGE` (400)
- These mirror the DB's `chk_experience_dates` check constraint, but catching them in the service gives a clean error code instead of a raw constraint-violation 500.

### Education — business rules worth knowing

- No `isCurrent` flag exists on this table (unlike Experience) — a null `endDate` simply means "ongoing," so only one rule applies: `endDate` (if present) must not be before `startDate` → `EDUCATION_INVALID_DATE_RANGE` (400).
- `cgpa` is optional and validated 0–10 at both the DTO layer (`@DecimalMin`/`@DecimalMax`, fail fast) and the DB layer (`chk_education_cgpa`, defense in depth).

### Certifications — business rules worth knowing

- The flattest module in the codebase — no child tables, no ManyToOne/ManyToMany relations, no cross-field date rules. Every service method is exactly 1 query (2 for update: fetch + flush).
- `issueDate` seed values in `V2__seed_data.sql` for your JPMorganChase/Coursera/AWS certifications are approximate (month/year only, since that's all the source profile had) — replace with exact dates via the admin UI once known.

### Blog — business rules worth knowing

- Public endpoints (`BlogController`, `BlogCommentController`) **hardcode** `status=PUBLISHED` server-side — no query parameter can ever expose a draft. Verified by `getPublishedBlogBySlug_throwsResourceNotFoundException_whenPostIsDraft`: the row exists, the public endpoint still 404s.
- Same guarantee for comments: `resolvePublishedBlog` in `BlogCommentServiceImpl` means a comment-attempt on a nonexistent slug and one on a draft slug are **indistinguishable** (both 404) — no slug-guessing oracle for unpublished content.
- Comments are **never auto-published** — every submission lands in a moderation queue (`isApproved=false`); it only becomes publicly visible after `PATCH /api/admin/blog/comments/{id}/approve`.
- `BlogCommentResponseDTO` (public) omits the commenter's email; `AdminBlogCommentResponseDTO` (moderation queue) includes it — deliberate, not an inconsistency.
- Tags are find-or-create by name on every create/update, one query per unique tag name rather than batched — tag creation is rare on a single-admin site. Revisit if this becomes multi-author.
- `publishedAt` is set once, the first time a post transitions to `PUBLISHED`, and never overwritten afterward — editing or archiving-then-republishing does not reset the original publish date.
- `category_id` on a blog must reference a category of `type=BLOG` — same pattern as Projects/Skills, different `CategoryType`. **Fixed this pass:** `V2__seed_data.sql` had zero `BLOG`-type and zero `PROJECT`-type categories — creating any project or blog post with a `categoryId` would have 404'd against an empty table. Added `java-spring`/`system-design`/`career` (BLOG) and `backend-project`/`full-stack`/`security` (PROJECT).
- Reading time is computed server-side from word count (200 wpm) on every create/update — can't be gamed or left stale by a client-supplied value.

### Settings/SEO/Social Links — design decisions worth knowing

- **Settings** is a flexible key-value store (`GET /api/settings` returns a flat `Map<String,String>` for frontend bootstrap) rather than a fixed schema of named fields — the DB table itself is generic (`setting_key`/`setting_value`), so the API mirrors that. Keys are validated to alphanumeric/dot/underscore/hyphen only, to keep typos from silently creating garbage keys the frontend will never read.
- **SEO is upserted by `pagePath`**, not by numeric id — an admin editing "the /about page's SEO" thinks in terms of the page, not a row id. `PUT /api/admin/seo` takes `pagePath` in the body and finds-or-creates.
- **Social link URLs accept `mailto:` and `tel:`** in addition to `http(s)://` — the seed data includes `mailto:manjith9989@gmail.com` and `tel:+919398303933`, so a strict URL-only pattern would have rejected the actual seeded data.

### Analytics — design decisions worth knowing

- **`page_views` is a `jsonb` column mapped with `@JdbcTypeCode(SqlTypes.JSON)`** on a `String` field — I initially wrote this entity with a plain `String` column and no type code, which looks like it should work but actually fails at the JDBC level (`column is of type jsonb but expression is of type character varying`). Caught and fixed it before it shipped; the `@JdbcTypeCode` annotation is what tells the PostgreSQL driver to send the value as JSON rather than varchar.
- **Map↔JSON conversion happens explicitly in `AnalyticsServiceImpl` via Jackson**, not automatically through Hibernate — kept visible and unit-tested (see `AnalyticsServiceImplTest`) rather than hidden inside a type descriptor.
- **`POST /api/analytics/track` does not accept `ipAddress` or `userAgent` from the request body** — the controller extracts both from the actual HTTP request (`X-Forwarded-For` header, falling back to `getRemoteAddr()`; `User-Agent` header) so a caller can't spoof analytics data by putting an arbitrary IP in JSON.
- **`country`/`city` are always null** — no GeoIP provider (MaxMind, ipapi, etc.) is wired up. Flagging this as an explicit scope decision: fabricating fake geo data would be worse than admitting it's unimplemented.
- **The rollup scheduler processes "yesterday," never "today"** — a cron job at 00:05 UTC summarizing the current day would capture a partial day. Waiting one day guarantees the data being rolled up is complete.
- **The per-page breakdown is one `GROUP BY` query**, not a loop of per-page `COUNT` calls.

## Auth module (Module 2)

Spring Security + stateless JWT. Access tokens are short-lived signed JWTs
(15 min default); refresh tokens are opaque random strings persisted in
`refresh_tokens` so they can be **revoked server-side** — a self-contained
refresh JWT can't be un-issued before its expiry without a denylist, which
is unnecessary complexity here. Every refresh call **rotates** the token
(old one is marked `revoked`, a new one issued), so a leaked refresh token
has at most one reuse window before the legitimate client's next refresh
implicitly invalidates it.

### `POST /api/auth/login`

Request:
```json
{ "usernameOrEmail": "admin", "password": "yourRealPassword" }
```

Response — `200 OK`:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "Xk3mP...==",
  "tokenType": "Bearer",
  "expiresInSeconds": 900,
  "username": "admin",
  "role": "ADMIN"
}
```

### `POST /api/auth/refresh`

Request: `{ "refreshToken": "Xk3mP...==" }` → same response shape as login, with a **new** access token and a **new, rotated** refresh token. The old refresh token is revoked as part of this call.

### `POST /api/auth/logout`

Request: `{ "refreshToken": "Xk3mP...==" }` → `204 No Content`. Idempotent — calling it twice, or with an already-invalid token, is not an error (logout must never leak whether a token existed).

### Required environment variables before deploying

| Variable | Purpose | Dev default (DO NOT use in prod) |
|---|---|---|
| `JWT_SECRET` | HMAC signing key, ≥256 bits | insecure placeholder in `application.yml` |
| `JWT_ACCESS_EXP_MS` | Access token lifetime | `900000` (15 min) |
| `JWT_REFRESH_EXP_MS` | Refresh token lifetime | `604800000` (7 days) |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed frontend origins | `localhost:5173,localhost:3000` |

### Not implemented in this pass — flagging rather than half-building

- **Rate limiting** on `/api/auth/login` (brute-force protection). The master spec lists this; adding a real implementation (e.g. Bucket4j) is a self-contained unit of work I'd rather do properly next than bolt on partially now.
- **CSRF** is disabled by design (stateless Bearer-token API has no ambient cookie credential to forge), not omitted by oversight.

## Self-review checklist (per architecture rules)

- ✅ No controller logic — both controllers are pure pass-through to `ProjectService`
- ✅ No controller → repository or entity access
- ✅ Controller depends on `ProjectService` interface, not `ProjectServiceImpl`
- ✅ DTOs used everywhere at the API boundary; no entity ever serialized
- ✅ `RequestDTO` fully validated (Jakarta annotations); `ResponseDTO` separate from entity
- ✅ Null/empty/invalid-ID/invalid-type/negative-value validation in service layer
- ✅ Custom exceptions + `ErrorCode` enum + `GlobalExceptionHandler`, no generic `Exception` thrown for business logic
- ✅ INFO on entry/exit, DEBUG on internal values, WARN on recoverable failures, ERROR on unhandled exceptions
- ✅ `viewCount` can never go negative (`chk_projects_view_count` + atomic increment-only update)
- ✅ `@Transactional` on every write method; full-replace updates via `orphanRemoval=true` collections, atomic
- ✅ No N+1 — see table above; `@EntityGraph` + Hibernate batch fetching
- ✅ Mapping lives only in `ProjectMapper`, never in service or controller
