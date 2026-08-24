import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import api from '../api/axios'
import StarRating from '../components/StarRating'
import { useAuth } from '../context/AuthContext'

export default function MovieDetail() {
  const { id } = useParams()
  const { isAuthenticated } = useAuth()
  const [movie, setMovie] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    fetchMovie()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id])

  async function fetchMovie() {
    setLoading(true)
    setError('')
    try {
      const { data } = await api.get(`/movies/${id}`)
      setMovie(data)
    } catch (err) {
      setError('Could not load this movie.')
    } finally {
      setLoading(false)
    }
  }

  async function handleRate(score) {
    if (!isAuthenticated) return
    setSaving(true)
    try {
      await api.post('/ratings', { movieId: Number(id), score })
      setMovie((prev) => ({ ...prev, userRating: score }))
    } catch (err) {
      setError('Could not save your rating.')
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <div className="page"><p className="muted">Loading…</p></div>
  if (error) return <div className="page"><p className="form-error">{error}</p></div>
  if (!movie) return null

  return (
    <div className="page movie-detail">
      <Link to="/" className="back-link">← Back to browse</Link>
      <div className="movie-detail-layout">
        <div className="movie-detail-poster">
          {movie.posterUrl ? (
            <img src={movie.posterUrl} alt={movie.title} />
          ) : (
            <div className="movie-card-poster-placeholder">No poster</div>
          )}
        </div>
        <div className="movie-detail-info">
          <h1>{movie.title} {movie.releaseYear && <span className="muted">({movie.releaseYear})</span>}</h1>
          <p className="movie-detail-meta">
            {movie.runtimeMinutes && <span>{movie.runtimeMinutes} min · </span>}
            {movie.tmdbRating != null && <span>★ {movie.tmdbRating.toFixed(1)} TMDB</span>}
          </p>
          {movie.genres?.length > 0 && <p className="tag-row">{movie.genres.join(' · ')}</p>}
          <p className="movie-detail-overview">{movie.overview}</p>

          {movie.directors?.length > 0 && (
            <p><strong>Director:</strong> {movie.directors.join(', ')}</p>
          )}
          {movie.actors?.length > 0 && (
            <p><strong>Cast:</strong> {movie.actors.join(', ')}</p>
          )}

          <div className="rate-section">
            {isAuthenticated ? (
              <>
                <p><strong>Your rating</strong>{saving && <span className="muted"> (saving…)</span>}</p>
                <StarRating value={movie.userRating || 0} onChange={handleRate} disabled={saving} />
              </>
            ) : (
              <p className="muted"><Link to="/login">Log in</Link> to rate this movie and get recommendations.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
