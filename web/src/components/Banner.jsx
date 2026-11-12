export default function Banner({ kind = 'error', children }) {
  if (!children) {
    return null
  }
  return <p className={`banner ${kind}`} role={kind === 'error' ? 'alert' : 'status'}>{children}</p>
}
