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