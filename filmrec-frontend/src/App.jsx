import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import Navbar from './components/Navbar'
import ProtectedRoute from './components/ProtectedRoute'
import Browse from './pages/Browse'
import MovieDetail from './pages/MovieDetail'
import Recommendations from './pages/Recommendations'
import Login from './pages/Login'
import Signup from './pages/Signup'
import './styles/App.css'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Navbar />
        <main>
          <Routes>
            <Route path="/" element={<Browse />} />
            <Route path="/movies/:id" element={<MovieDetail />} />
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />
            <Route
              path="/recommendations"
              element={
                <ProtectedRoute>
                  <Recommendations />
                </ProtectedRoute>
              }
            />
          </Routes>
        </main>
      </BrowserRouter>
    </AuthProvider>
  )
}
