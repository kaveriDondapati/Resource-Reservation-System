import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../api.js'
import { useAuth } from '../auth.jsx'
import Banner from '../components/Banner.jsx'
import { formatWhen, nextHourRange, toApiDateTime, toDateTimeInput, typeLabel } from '../format.js'

export default function ResourcePage() {
  const { id } = useParams()
  const { session } = useAuth()
  const [resource, setResource] = useState(null)
  const [schedule, setSchedule] = useState([])
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [loading, setLoading] = useState(true)
  const range = nextHourRange()
  const [form, setForm] = useState({
    startTime: range.start,
    endTime: range.end,
    purpose: ''
  })
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    let ignore = false
    setLoading(true)
    setError('')
    api(`/api/resources/${id}`)
      .then((data) => {
        if (!ignore) {
          setResource(data)
        }
      })
      .catch((err) => {
        if (!ignore) {
          setError(err.message)
        }
      })
      .finally(() => {
        if (!ignore) {
          setLoading(false)
        }
      })
    return () => {
      ignore = true
    }
  }, [id])

  useEffect(() => {
    if (!session) {
      setSchedule([])
      return undefined
    }
    let ignore = false
    const from = toApiDateTime(toDateTimeInput(new Date()))
    api(`/api/reservations/resource/${id}?from=${encodeURIComponent(from)}`, { token: session.token })
      .then((data) => {
        if (!ignore) {
          setSchedule(data)
        }
      })
      .catch(() => {
        if (!ignore) {
          setSchedule([])
        }
      })
    return () => {
      ignore = true
    }
  }, [id, session, notice])

  function update(event) {
    const { name, value } = event.target
    setForm((current) => ({ ...current, [name]: value }))
  }

  async function submit(event) {
    event.preventDefault()
    setSaving(true)
    setError('')
    setNotice('')
    try {
      await api('/api/reservations', {
        method: 'POST',
        token: session.token,
        body: {
          resourceId: Number(id),
          startTime: toApiDateTime(form.startTime),
          endTime: toApiDateTime(form.endTime),
          purpose: form.purpose
        }
      })
      setNotice('Booked. It is confirmed.')
      setForm((current) => ({ ...current, purpose: '' }))
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return <p className="muted">Loading resource…</p>
  }
  if (!resource) {
    return <Banner>{error || 'Resource not found'}</Banner>
  }

  return (
    <section className="split">
      <div>
        <Link to="/" className="back">Catalog</Link>
        <p className="eyebrow">{typeLabel(resource.type)}</p>
        <h1>{resource.name}</h1>
        <p className="meta">{resource.location} · capacity {resource.capacity}</p>
        <p className="lede">{resource.description || 'No description yet.'}</p>
        {!resource.active && <Banner>This resource is inactive and cannot be booked.</Banner>}
        <h2 className="section-title">Upcoming</h2>
        {!session && <p className="muted">Sign in to see who has it and to reserve a slot.</p>}
        {session && schedule.length === 0 && <p className="muted">No upcoming confirmed bookings.</p>}
        <ul className="list">
          {schedule.map((item) => (
            <li key={item.id}>
              <strong>{formatWhen(item.startTime)} – {formatWhen(item.endTime)}</strong>
              <span>{item.username ? `${item.username}${item.purpose ? ` · ${item.purpose}` : ''}` : 'Booked'}</span>
            </li>
          ))}
        </ul>
      </div>
      <aside className="panel">
        <h2>Reserve</h2>
        {!session && (
          <p>Sign in to hold this resource. <Link to="/login" state={{ from: `/resources/${id}` }}>Sign in</Link></p>
        )}
        {session && (
          <form onSubmit={submit} className="stack">
            <label>
              Starts
              <input type="datetime-local" name="startTime" value={form.startTime} onChange={update} required />
            </label>
            <label>
              Ends
              <input type="datetime-local" name="endTime" value={form.endTime} onChange={update} required />
            </label>
            <label>
              Purpose
              <input name="purpose" value={form.purpose} onChange={update} maxLength={500} placeholder="Sprint planning" />
            </label>
            <p className="hint">End must be after the start, the slot cannot be in the past, and the booking can last at most 24 hours.</p>
            <Banner>{error}</Banner>
            <Banner kind="ok">{notice}</Banner>
            <button type="submit" className="button" disabled={saving || !resource.active}>
              {saving ? 'Booking…' : 'Confirm booking'}
            </button>
          </form>
        )}
      </aside>
    </section>
  )
}
