import { createContext, useContext, useMemo, useState } from 'react'
import { api } from './api.js'

const AuthContext = createContext(null)
const STORAGE_KEY = 'reserve.session'

export function AuthProvider({ children }) {
  const [session, setSession] = useState(readSession)

  function commit(next) {
    if (next) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(next))
    } else {
      localStorage.removeItem(STORAGE_KEY)
    }
    setSession(next)
  }

  async function login(username, password) {
    const data = await api('/api/auth/login', {
      method: 'POST',
      body: { username, password }
    })
    const next = {
      token: data.token,
      username: data.username,
      role: data.role,
      userId: data.userId
    }
    commit(next)
    return next
  }

  async function register(username, password) {
    await api('/api/auth/register', {
      method: 'POST',
      body: { username, password }
    })
    return login(username, password)
  }

  function logout() {
    commit(null)
  }

  const value = useMemo(() => ({
    session,
    isAdmin: session?.role === 'ADMIN',
    login,
    register,
    logout
  }), [session])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const value = useContext(AuthContext)
  if (!value) {
    throw new Error('useAuth must be used inside AuthProvider')
  }
  return value
}

function readSession() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}
