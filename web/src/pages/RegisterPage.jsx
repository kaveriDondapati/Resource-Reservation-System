import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth.jsx'
import Banner from '../components/Banner.jsx'
import PasswordField from '../components/PasswordField.jsx'

export default function RegisterPage() {
  const { register, session } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  if (session) {
    return <Navigate to="/" replace />
  }

  async function submit(event) {
    event.preventDefault()
    setSaving(true)
    setError('')
    try {
      await register(username.trim(), password)
      navigate('/', { replace: true })
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="auth-screen">
      <section className="auth-card">
      <p className="eyebrow">Join the desk</p>
      <h1>Create an account</h1>
      <form onSubmit={submit} className="stack">
        <label>
          Username
          <input value={username} onChange={(event) => setUsername(event.target.value)} autoComplete="username" required minLength={3} maxLength={50} />
        </label>
        <PasswordField
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          autoComplete="new-password"
          required
          minLength={8}
        />
        <p className="hint">Password needs 8 or more characters, including a capital letterr, a number, and one of @ $ ! % * ? &.</p>
        <Banner>{error}</Banner>
        <button type="submit" className="button" disabled={saving}>{saving ? 'Creating…' : 'Create account'}</button>
      </form>
      <p>Already registered? <Link to="/login">Sign in</Link></p>
      </section>
    </div>
  )
}
