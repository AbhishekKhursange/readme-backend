# ReadMe — Backend

Spring Boot backend for the ReadMe books reading website.

## Stack

Spring Boot 4.x (Java 21) · Spring Data JPA · Spring Security + JWT ·
Neon Postgres · Cloudinary · Redis (refresh tokens)

## Setup

Open this folder in STS as an existing Maven project, then set these
environment variables (Run Configurations → your app → **Environment** tab),
or hardcode them directly in `application.properties` for local dev only —
never commit real values to git either way:

```
# Neon Postgres — from Neon dashboard → Connection Details → Java/JDBC
DB_URL=jdbc:postgresql://<host>/<db>?sslmode=require
DB_USERNAME=...
DB_PASSWORD=...

# Cloudinary — from dashboard → API Keys
CLOUDINARY_CLOUD_NAME=...
CLOUDINARY_API_KEY=...
CLOUDINARY_API_SECRET=...

# CORS — must match your frontend's actual origin (Vite default shown)
CORS_ORIGINS=http://localhost:5173

# JWT — any long random string, 32+ characters
JWT_SECRET=...

# Redis — local Docker or Upstash free tier (see below)
REDIS_HOST=...
REDIS_PORT=...
REDIS_PASSWORD=...
REDIS_SSL=true
```

Run `ReadMeApplication` (or whatever your main class is named). Hibernate
creates all tables on first boot (`ddl-auto=update`).

## Redis

Login won't work without Redis reachable — refresh tokens are written there
on every login.

**Local (if you have Docker):**
```
docker run -d --name redis -p 6379:6379 redis:7
```
Leave `REDIS_HOST=localhost`, `REDIS_PORT=6379`, `REDIS_PASSWORD` empty,
`REDIS_SSL=false`.

**Free cloud (no Docker):** [Upstash](https://upstash.com) → create a
database → copy Endpoint/Port/Password from its Details tab → set
`REDIS_SSL=true`.

## Auth endpoints

- `POST /api/auth/register` — `{ fullName, email, password, favoriteGenre }`
- `POST /api/auth/login` — `{ email, password }` → `{ token, refreshToken, user }`
- `POST /api/auth/refresh` — `{ refreshToken }` → new `{ token, refreshToken }` (old one is rotated out)
- `POST /api/auth/logout` — `{ refreshToken }` → deletes it from Redis

Access tokens are stateless JWTs (`jwt.access-expiration-ms`, default 7
days). Refresh tokens are opaque UUIDs stored in Redis
(`jwt.refresh-expiration-ms`, default 7 days) — that's what makes logout
and revocation actually work.

## Access rules (`config/SecurityConfig.java`)

| Endpoint | Access |
|---|---|
| `GET /api/books/**`, `GET /api/categories/**`, `/api/auth/**` | public |
| `POST/DELETE /api/books/**`, `POST /api/categories`, `/api/upload/**`, `/api/users/**` | `ROLE_ADMIN` only |
| everything else | any authenticated user |

New users default to `admin = false`. To make one an admin, run directly
against Neon (SQL Editor or any Postgres client):
```sql
UPDATE users SET admin = true WHERE email = 'your@email.com';
```
Then log out and back in — the JWT is only refreshed with the new role on
a fresh login.

## Image uploads (Cloudinary)

`POST /api/upload` (multipart, admin-only) takes `file` + `folder`. The
admin panel in the frontend auto-derives the folder from the book title:
- Cover images → `readMe/covers`
- Page images → `readMe/pages/<book-slug>`, one subfolder per book

The folder is just for organizing Cloudinary's dashboard — the actual link
between a cover, its pages, and the book record lives in Postgres
(`books`/`pages` tables, joined by `book_id`), not in Cloudinary itself.

## Frontend

The React frontend (separate project) expects this backend at
`http://localhost:8080/api` by default — see its own README for setup.
