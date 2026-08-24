# FilmRec — Film Recommendation Engine

A content-based movie recommendation engine. Users rate films they've watched; the app
scores unwatched films by weighted similarity (shared director > genre > actor) to those
ratings and explains *why* each recommendation was made.

## Stack

- **Backend:** Spring Boot 3.3, Java 17, Spring Data JPA + MySQL, Spring Data MongoDB,
  Spring Security + JWT, Lombok, Bean Validation, springdoc-openapi (Swagger UI)
- **Frontend:** React 18 + Vite, react-router-dom, axios
- **Data source:** TMDB API — used once, at seed time, to populate your own MySQL DB.
  The running app never calls TMDB directly.

## How the recommendation algorithm works

For every movie you haven't rated, `RecommendationService` compares it against every movie
you *have* rated. Each shared attribute contributes `weight × (yourRating / 5.0)` to that
candidate's score:

| Shared attribute | Weight |
|---|---|
| Director | 3.0 |
| Genre | 2.0 |
| Actor | 1.0 |

So a film sharing a director with something you rated 5/5 contributes far more than one
sharing an actor with something you rated 2/5. Candidates are sorted by total score, and
the single strongest contribution becomes the human-readable "why" reason (e.g. *"Recommended
because it shares director Denis Villeneuve with 'Dune', which you rated 5/5"*). Every
generated recommendation batch is also logged to MongoDB (`RecommendationLog`) for history/audit.

## Prerequisites

- Java 17+
- Maven (or use the included `mvnw` if you add one)
- MySQL running locally (or a free hosted instance — PlanetScale, Railway, Aiven all have free tiers)
- MongoDB running locally or Atlas free tier
- Node 18+ for the frontend
- A free [TMDB API key](https://www.themoviedb.org/settings/api) (only needed for seeding)

## Backend setup

1. Create a MySQL database (or let `createDatabaseIfNotExist=true` handle it — see `application.yml`).
2. Set environment variables (or edit `application.yml` defaults directly):

   ```bash
   export MYSQL_URL="jdbc:mysql://localhost:3306/filmrec?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC"
   export MYSQL_USERNAME=root
   export MYSQL_PASSWORD=yourpassword
   export MONGO_URI="mongodb://localhost:27017/filmrec"
   export JWT_SECRET="a-long-random-secret-at-least-32-chars"
   export TMDB_API_KEY="your-tmdb-key"
   export CORS_ALLOWED_ORIGINS="http://localhost:5173"
   ```

3. Run it:

   ```bash
   cd filmrec-backend
   mvn spring-boot:run
   ```

4. API docs live at `http://localhost:8080/swagger-ui.html`.

## Seeding the catalog

The app never calls TMDB at request time — you seed once (or re-run any time to pull more
movies; already-seeded movies are skipped by TMDB id):

1. Register a user: `POST /api/auth/register`
2. Copy the returned `token`
3. Call the seed endpoint with it:

   ```bash
   curl -X POST "http://localhost:8080/api/admin/seed?pages=5" \
     -H "Authorization: Bearer <token>"
   ```

   `pages` is optional (defaults to `TMDB_SEED_PAGES`, default 5 — TMDB returns ~20 movies
   per page, so 5 pages ≈ 100 movies). Each movie triggers 2 extra TMDB calls (detail +
   credits), so keep `pages` modest to stay well under TMDB's rate limits.

## Frontend setup

```bash
cd filmrec-frontend
cp .env.example .env   # point VITE_API_BASE_URL at your backend
npm install
npm run dev
```

Visit `http://localhost:5173`.

## Deployment (same pattern as your other projects)

- **Backend → Render:** add a `Dockerfile` (multi-stage Maven build), set the env vars above
  in Render's dashboard, use a free-tier MySQL host (PlanetScale/Railway/Aiven) since Render's
  own MySQL isn't free.
- **Frontend → Vercel:** set `VITE_API_BASE_URL` to your Render backend URL in Vercel's
  environment variables, framework preset "Vite".
- Remember to update `CORS_ALLOWED_ORIGINS` on the backend to your deployed Vercel URL.

## API summary

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | — | Create account, returns JWT |
| POST | `/api/auth/login` | — | Log in, returns JWT |
| GET | `/api/movies?search=` | optional | Browse/search catalog |
| GET | `/api/movies/{id}` | optional | Movie detail |
| POST | `/api/ratings` | required | Rate (or re-rate) a movie |
| GET | `/api/ratings/me` | required | Your ratings |
| DELETE | `/api/ratings/{movieId}` | required | Remove a rating |
| GET | `/api/recommendations?limit=` | required | Personalized recommendations + reasons |
| POST | `/api/admin/seed?pages=` | required | Seed catalog from TMDB |

## Project structure notes

- Entities use JPA `@ManyToMany` with a mapping owner side (`Movie`) and inverse side
  (`Genre`/`Director`/`Actor`) — good practice for join-table modeling.
- `Genre`/`Director`/`Actor` override `equals`/`hashCode` by id so `Set.contains()` works
  correctly when comparing entities loaded in different queries within the same transaction.
- `RecommendationService` and `MovieService` are `@Transactional(readOnly = true)` because
  the many-to-many collections are lazily loaded — without an open transaction, accessing
  `movie.getGenres()` outside the initial query would throw `LazyInitializationException`.
- JWT auth is stateless (`SessionCreationPolicy.STATELESS`); `JwtAuthFilter` runs before
  Spring Security's default filter and populates the `SecurityContext` from the token.
