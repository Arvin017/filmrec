import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/axios'
import MovieCard from '../components/MovieCard'

export default function Recommendations() {
  const [recs, setRecs] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    fetchRecommendations()
  }, [])

  async function fetchRecommendations() {
    setLoading(true)
    setError('')
    try {
      const { data } = await api.get('/recommendations')
      setRecs(data)
    } catch (err) {
      setError('Could not load recommendations.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>Recommended for you</h1>
      </div>

      {loading && <p className="muted">Crunching your ratings…</p>}
      {error && <p className="form-error">{error}</p>}

      {!loading && !error && recs.length === 0 && (
        <p className="muted">
          Rate a few movies on <Link to="/">the browse page</Link> and your personalized recommendations
          will show up here, along with a reason for each pick.
        </p>
      )}

      <div className="movie-grid">
        {recs.map((rec) => (
          <MovieCard key={rec.movieId} movie={rec} reason={rec.reason} />
        ))}
      </div>
    </div>
  )
}
