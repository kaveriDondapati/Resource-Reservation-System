import { Navigate, Outlet, Route, Routes, useLocation } from 'react-router-dom'
import { useAuth } from './auth.jsx'
import Shell from './components/Shell.jsx'
import CatalogPage from './pages/CatalogPage.jsx'
import ResourcePage from './pages/ResourcePage.jsx'
import BookingsPage from './pages/BookingsPage.jsx'
import LoginPage from './pages/LoginPage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import AdminResourcesPage from './pages/AdminResourcesPage.jsx'
import AdminReservationsPage from './pages/AdminReservationsPage.jsx'

export default function App() {
  return (
    <Routes>
      <Route element={<Shell />}>
        <Route index element={<CatalogPage />} />
        <Route path="resources/:id" element={<ResourcePage />} />
        <Route path="login" element={<LoginPage />} />
        <Route path="register" element={<RegisterPage />} />
        <Route element={<RequireAuth />}>
          <Route path="bookings" element={<BookingsPage />} />
        </Route>
        <Route element={<RequireAuth admin />}>
          <Route path="admin/resources" element={<AdminResourcesPage />} />
          <Route path="admin/reservations" element={<AdminReservationsPage />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  )
}

function RequireAuth({ admin = false }) {
  const { session, isAdmin } = useAuth()
  const location = useLocation()
  if (!session) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />
  }
  if (admin && !isAdmin) {
    return <Navigate to="/" replace />
  }
  return <Outlet />
}
