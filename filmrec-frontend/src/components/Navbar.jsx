import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { user, isAuthenticated, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <header className="navbar">
      <Link to="/" className="navbar-brand">FilmRec</Link>
      <nav className="navbar-links">
        <Link to="/">Browse</Link>
        {isAuthenticated && <Link to="/recommendations">Recommendations</Link>}
        {isAuthenticated ? (
          <>
            <span className="navbar-user">{user.username}</span>
            <button className="btn-link" onClick={handleLogout}>Log out</button>
          </>
        ) : (
          <>
            <Link to="/login">Log in</Link>
            <Link to="/signup">Sign up</Link>
          </>
        )}
      </nav>
    </header>
  )
}
