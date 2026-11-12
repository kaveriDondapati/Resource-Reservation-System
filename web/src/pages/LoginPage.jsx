import { useState } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth.jsx'
import Banner from '../components/Banner.jsx'
import PasswordField from '../components/PasswordField.jsx'

export default function LoginPage() {
  const { login, session } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)
  const next = location.state?.from || '/'

  if (session) {
    return <Navigate to={next} replace />
  }

  async function submit(event) {
    event.preventDefault()
    setSaving(true)
    setError('')
    try {
      await login(username.trim(), password)
      navigate(next, { replace: true })
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="auth-screen">
    <section className="auth-card">
      <p className="eyebrow">Welcome back</p>
      <h1>Sign in</h1>
      <form onSubmit={submit} className="stack">
        <label>
          Username
          <input value={username} onChange={(event) => setUsername(event.target.value)} autoComplete="username" required />
        </label>
        <PasswordField
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          autoComplete="current-password"
          required
        />
        <Banner>{error}</Banner>
        <button type="submit" className="button" disabled={saving}>{saving ? 'Signing in…' : 'Sign in'}</button>
      </form>
      <p className="hint">Seeded admin: admin / Admin@123</p>
      <p>New here? <Link to="/register">Create an account</Link></p>
    </section>
    </div>
  )
}
