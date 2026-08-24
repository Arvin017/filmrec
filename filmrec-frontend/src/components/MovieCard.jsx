import { Link } from 'react-router-dom'

export default function MovieCard({ movie, reason }) {
  return (
    <Link to={`/movies/${movie.movieId ?? movie.id}`} className="movie-card">
      <div className="movie-card-poster">
        {movie.posterUrl ? (
          <img src={movie.posterUrl} alt={movie.title} loading="lazy" />
        ) : (
          <div className="movie-card-poster-placeholder">No poster</div>
        )}
      </div>
      <div className="movie-card-body">
        <h3 className="movie-card-title">{movie.title}</h3>
        <p className="movie-card-meta">
          {movie.releaseYear ?? '—'}
          {movie.tmdbRating != null && <span> · ★ {movie.tmdbRating.toFixed(1)}</span>}
          {movie.userRating != null && <span className="movie-card-userrating"> · Your rating: {movie.userRating}/5</span>}
        </p>
        {reason && <p className="movie-card-reason">{reason}</p>}
      </div>
    </Link>
  )
}
