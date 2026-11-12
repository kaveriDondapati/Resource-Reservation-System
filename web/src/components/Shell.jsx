import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../auth.jsx'

export default function Shell() {
  const { session, isAdmin, logout } = useAuth()

  return (
    <div className="shell">
      <aside className="rail">
        <NavLink to="/" className="brand">
          <span className="brand-mark">R</span>
          <span>
            <strong>Reserve</strong>
            <small>Rooms, desks, and kit</small>
          </span>
        </NavLink>
        <nav>
          <NavLink to="/" end>Catalog</NavLink>
          {session && <NavLink to="/bookings">My bookings</NavLink>}
          {isAdmin && <NavLink to="/admin/resources">Resources</NavLink>}
          {isAdmin && <NavLink to="/admin/reservations">All bookings</NavLink>}
        </nav>
        <div className="rail-foot">
          {session ? (
            <>
              <p>
                <strong>{session.username}</strong>
                <span>{session.role === 'ADMIN' ? 'Admin' : 'Member'}</span>
              </p>
              <button type="button" className="ghost" onClick={logout}>Sign out</button>
            </>
          ) : (
            <NavLink to="/login" className="button">Sign in</NavLink>
          )}
        </div>
      </aside>
      <main className="stage">
        <Outlet />
      </main>
    </div>
  )
}
