import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { useAuth } from '../auth.jsx'
import Banner from '../components/Banner.jsx'
import { formatWhen } from '../format.js'

export default function AdminReservationsPage() {
  const { session } = useAuth()
  const [rows, setRows] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  function load() {
    setLoading(true)
    return api('/api/reservations', { token: session.token })
      .then((data) => {
        setRows(data)
        setError('')
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    load()
  }, [session.token])

  async function cancel(id) {
    setError('')
    try {
      await api(`/api/reservations/${id}/cancel`, { method: 'POST', token: session.token })
      await load()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <section>
      <header className="page-head">
        <p className="eyebrow">Admin</p>
        <h1>All bookings</h1>
      </header>
      <Banner>{error}</Banner>
      {loading && <p className="muted">Loading bookings…</p>}
      {!loading && rows.length === 0 && <p className="empty">No bookings yet.</p>}
      <ul className="list rich">
        {rows.map((item) => (
          <li key={item.id}>
            <div>
              <strong>{item.resourceName}</strong>
              <span>{item.username} · {formatWhen(item.startTime)} – {formatWhen(item.endTime)}</span>
              {item.purpose && <span>{item.purpose}</span>}
            </div>
            <div className="row-actions">
              <span className={item.status === 'CONFIRMED' ? 'pill confirmed' : 'pill muted'}>{item.status === 'CONFIRMED' ? 'Confirmed' : 'Cancelled'}</span>
              {item.status === 'CONFIRMED' && (
                <button type="button" className="ghost" onClick={() => cancel(item.id)}>Cancel</button>
              )}
            </div>
          </li>
        ))}
      </ul>
    </section>
  )
}
