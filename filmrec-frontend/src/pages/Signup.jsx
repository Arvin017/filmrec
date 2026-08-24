import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Signup() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await register(username, email, password)
      navigate('/')
    } catch (err) {
      setError(err.response?.data?.message || 'Signup failed. Try a different username/email.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-form" onSubmit={handleSubmit}>
        <h1>Sign up</h1>
        {error && <p className="form-error">{error}</p>}
        <label>
          Username
          <input value={username} onChange={(e) => setUsername(e.target.value)} required autoFocus minLength={3} />
        </label>
        <label>
          Email
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        <label>
          Password
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required minLength={6} />
        </label>
        <button type="submit" disabled={loading}>{loading ? 'Creating account…' : 'Sign up'}</button>
        <p className="auth-switch">Already have an account? <Link to="/login">Log in</Link></p>
      </form>
    </div>
  )
}
