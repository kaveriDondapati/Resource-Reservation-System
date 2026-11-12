import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api.js'
import Banner from '../components/Banner.jsx'
import { RESOURCE_TYPES, typeLabel } from '../format.js'

export default function CatalogPage() {
  const [resources, setResources] = useState([])
  const [type, setType] = useState('ALL')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let ignore = false
    setLoading(true)
    api('/api/resources')
      .then((data) => {
        if (!ignore) {
          setResources(data)
          setError('')
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
  }, [])

  const visible = type === 'ALL' ? resources : resources.filter((item) => item.type === type)

  return (
    <section className="catalog">
      <header className="page-head">
        <p className="eyebrow">Available now</p>
        <h1>Book a place or a thing.</h1>
        <p className="lede">Meeting rooms, desks, equipment, and vehicles. Pick one, choose a time, and it is held immediately.</p>
      </header>
      <div className="filters" role="tablist" aria-label="Resource type">
        <button type="button" className={type === 'ALL' ? 'chip on' : 'chip'} onClick={() => setType('ALL')}>All</button>
        {RESOURCE_TYPES.map((item) => (
          <button
            key={item.value}
            type="button"
            className={type === item.value ? 'chip on' : 'chip'}
            onClick={() => setType(item.value)}
          >
            {item.label}
          </button>
        ))}
      </div>
      <Banner>{error}</Banner>
      {loading && <p className="muted">Loading the catalog…</p>}
      {!loading && !error && visible.length === 0 && (
        <p className="empty">Nothing in this category yet.</p>
      )}
      <div className="cards">
        {visible.map((resource) => (
          <Link key={resource.id} to={`/resources/${resource.id}`} className="card">
            <span className="pill">{typeLabel(resource.type)}</span>
            <h2>{resource.name}</h2>
            <p className="meta">{resource.location} · {resource.capacity} {resource.capacity === 1 ? 'seat' : 'seats'}</p>
            <p className="desc">{resource.description || 'No description yet.'}</p>
            <span className="card-action">Reserve</span>
          </Link>
        ))}
      </div>
    </section>
  )
}
