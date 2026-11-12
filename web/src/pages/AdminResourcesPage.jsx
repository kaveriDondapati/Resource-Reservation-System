import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { useAuth } from '../auth.jsx'
import Banner from '../components/Banner.jsx'
import { RESOURCE_TYPES, typeLabel } from '../format.js'

const emptyForm = {
  name: '',
  type: 'MEETING_ROOM',
  location: '',
  capacity: 1,
  description: '',
  active: true
}

export default function AdminResourcesPage() {
  const { session } = useAuth()
  const [rows, setRows] = useState([])
  const [form, setForm] = useState(emptyForm)
  const [editingId, setEditingId] = useState(null)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [loading, setLoading] = useState(true)

  function load() {
    setLoading(true)
    return api('/api/resources?includeInactive=true', { token: session.token })
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

  function update(event) {
    const { name, value, type, checked } = event.target
    setForm((current) => ({
      ...current,
      [name]: type === 'checkbox' ? checked : name === 'capacity' ? Number(value) : value
    }))
  }

  function edit(resource) {
    setEditingId(resource.id)
    setForm({
      name: resource.name,
      type: resource.type,
      location: resource.location,
      capacity: resource.capacity,
      description: resource.description || '',
      active: resource.active
    })
    setNotice('')
    setError('')
  }

  function reset() {
    setEditingId(null)
    setForm(emptyForm)
  }

  async function submit(event) {
    event.preventDefault()
    setError('')
    setNotice('')
    const body = {
      name: form.name.trim(),
      type: form.type,
      location: form.location.trim(),
      capacity: Number(form.capacity),
      description: form.description,
      active: form.active
    }
    try {
      if (editingId) {
        await api(`/api/resources/${editingId}`, { method: 'PUT', token: session.token, body })
        setNotice('Resource updated.')
      } else {
        await api('/api/resources', { method: 'POST', token: session.token, body })
        setNotice('Resource added.')
      }
      reset()
      await load()
    } catch (err) {
      setError(err.message)
    }
  }

  async function deactivate(id) {
    setError('')
    try {
      await api(`/api/resources/${id}`, { method: 'DELETE', token: session.token })
      await load()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <section className="split">
      <div>
        <header className="page-head">
          <p className="eyebrow">Admin</p>
          <h1>Resources</h1>
        </header>
        <Banner>{error}</Banner>
        {loading && <p className="muted">Loading resources…</p>}
        <ul className="list rich">
          {rows.map((resource) => (
            <li key={resource.id}>
              <div>
                <strong>{resource.name}</strong>
                <span>{typeLabel(resource.type)} · {resource.location} · {resource.capacity}</span>
              </div>
              <div className="row-actions">
                <span className={resource.active ? 'pill active-status' : 'pill muted'}>{resource.active ? 'Active' : 'Inactive'}</span>
                <button type="button" className="ghost" onClick={() => edit(resource)}>Edit</button>
                {resource.active && (
                  <button type="button" className="ghost" onClick={() => deactivate(resource.id)}>Deactivate</button>
                )}
              </div>
            </li>
          ))}
        </ul>
      </div>
      <aside className="panel">
        <h2>{editingId ? 'Edit resource' : 'Add resource'}</h2>
        <form onSubmit={submit} className="stack">
          <label>
            Name
            <input name="name" value={form.name} onChange={update} required maxLength={120} />
          </label>
          <label>
            Type
            <select name="type" value={form.type} onChange={update}>
              {RESOURCE_TYPES.map((type) => (
                <option key={type.value} value={type.value}>{type.label}</option>
              ))}
            </select>
          </label>
          <label>
            Location
            <input name="location" value={form.location} onChange={update} required maxLength={200} />
          </label>
          <label>
            Capacity
            <input name="capacity" type="number" min="1" value={form.capacity} onChange={update} required />
          </label>
          <label>
            Description
            <textarea name="description" value={form.description} onChange={update} maxLength={1000} rows={3} />
          </label>
          {editingId && (
            <label className="check">
              <input name="active" type="checkbox" checked={form.active} onChange={update} />
              Active
            </label>
          )}
          <Banner kind="ok">{notice}</Banner>
          <div className="row-actions">
            <button type="submit" className="button">{editingId ? 'Save changes' : 'Add resource'}</button>
            {editingId && <button type="button" className="ghost" onClick={reset}>Cancel</button>}
          </div>
        </form>
      </aside>
    </section>
  )
}
