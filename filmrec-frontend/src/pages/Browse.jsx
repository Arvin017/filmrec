import { useEffect, useState } from 'react'
import api from '../api/axios'
import MovieCard from '../components/MovieCard'

export default function Browse() {
  const [movies, setMovies] = useState([])
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const timeout = setTimeout(() => {
      fetchMovies(search)
    }, 300) // debounce search input
    return () => clearTimeout(timeout)
  }, [search])

  async function fetchMovies(query) {
    setLoading(true)
    setError('')
    try {
      const { data } = await api.get('/movies', { params: query ? { search: query } : {} })
      setMovies(data)
    } catch (err) {
      setError('Could not load movies. Is the backend running and seeded?')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>Browse films</h1>
        <input
          className="search-input"
          type="search"
          placeholder="Search by title, genre, director, or actor…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {error && <p className="form-error">{error}</p>}
      {loading && <p className="muted">Loading…</p>}

      {!loading && movies.length === 0 && !error && (
        <p className="muted">No movies found. If this is a fresh setup, seed the catalog first via the /api/admin/seed endpoint.</p>
      )}

      <div className="movie-grid">
        {movies.map((movie) => (
          <MovieCard key={movie.id} movie={movie} />
        ))}
      </div>
    </div>
  )
}
