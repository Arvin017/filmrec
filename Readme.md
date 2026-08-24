# 🎬 FilmRec — Movie Recommendation Engine

A full-stack **movie recommendation platform** built with **Spring Boot, React, MongoDB, TiDB Cloud, JWT Authentication, and TMDB API**.

FilmRec allows users to browse movies, create an account, rate films, and receive personalized movie recommendations based on their ratings.

## 🚀 Live Demo

### Frontend
https://filmrec-six.vercel.app

### Backend API
https://filmrec-o0xp.onrender.com

### Swagger API Documentation
https://filmrec-o0xp.onrender.com/swagger-ui/index.html

> Note: the backend is hosted on Render's free tier, which spins down after inactivity.
> A keep-alive ping (UptimeRobot) is used to minimize cold-start delays.

---

## ✨ Features

- 🎬 Browse movies
- 🔎 Search movies by title, genre, director, or actor
- ⭐ Rate movies
- 🤖 Personalized movie recommendations
- 🧠 Content-based recommendation engine
- 🔐 JWT-based authentication
- 👤 User registration and login
- 🛡️ Protected API endpoints
- 🌐 CORS configured for production
- 🎞️ TMDB movie data integration
- 📚 Swagger/OpenAPI API documentation
- ☁️ Cloud deployment
- 📱 Responsive React frontend

---

## 🏗️ Architecture

```text
                    ┌─────────────────────┐
                    │     React / Vite    │
                    │      Frontend       │
                    │      Vercel         │
                    └──────────┬──────────┘
                               │
                               │ REST API
                               ▼
                    ┌─────────────────────┐
                    │    Spring Boot      │
                    │      Backend        │
                    │       Render        │
                    └───────┬─────┬───────┘
                            │     │
              ┌─────────────┘     └─────────────┐
              ▼                                 ▼
      ┌────────────────┐                ┌────────────────┐
      │  TiDB Cloud    │                │ MongoDB Atlas  │
      │    MySQL       │                │   Database     │
      └────────────────┘                └────────────────┘
                            │
                            ▼
                    ┌─────────────────┐
                    │    TMDB API     │
                    │  Movie Metadata │
                    └─────────────────┘
```

---

## 🧠 How Recommendations Work

For every unwatched movie, the engine compares it against every movie you've rated,
scoring shared attributes by weight:

| Shared with a rated movie | Weight |
|---|---|
| Director | 3.0 |
| Genre | 2.0 |
| Actor | 1.0 |

Each match is also scaled by how highly you rated that movie (score ÷ 5), so a film
sharing a director with something you rated 5★ pulls harder than one sharing an actor
with something you rated 2★. Candidates are ranked by total score, and the single
strongest contributing match becomes the human-readable "why" shown with each
recommendation — e.g. *"Recommended because it shares director Denis Villeneuve with
Dune, which you rated 5/5."*

---

## 🔐 Authentication

- JWT-based authentication
- User registration and login
- Protected recommendation and rating endpoints
- Per-user ratings and recommendations

## 🛠️ Tech Stack

### Backend
- Java
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- MySQL / TiDB Cloud
- MongoDB
- Maven

### Frontend
- React
- Vite
- Axios
- React Router

### APIs & Deployment
- TMDB API
- Swagger / OpenAPI
- Render
- Vercel

## 👨‍💻 Author

**Arvin**
GitHub: https://github.com/Arvin017