import { createContext, useContext, useState, useCallback } from 'react'
import api from '../api/axios'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('filmrec_user')
    return stored ? JSON.parse(stored) : null
  })

  const login = useCallback(async (username, password) => {
    const { data } = await api.post('/auth/login', { username, password })
    persistSession(data)
    return data
  }, [])

  const register = useCallback(async (username, email, password) => {
    const { data } = await api.post('/auth/register', { username, email, password })
    persistSession(data)
    return data
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('filmrec_token')
    localStorage.removeItem('filmrec_user')
    setUser(null)
  }, [])

  function persistSession(data) {
    localStorage.setItem('filmrec_token', data.token)
    const userInfo = { id: data.userId, username: data.username }
    localStorage.setItem('filmrec_user', JSON.stringify(userInfo))
    setUser(userInfo)
  }

  return (
    <AuthContext.Provider value={{ user, login, register, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider')
  return ctx
}
