import { useState } from 'react'

export default function PasswordField({ label = 'Password', value, onChange, autoComplete, required, minLength }) {
  const [visible, setVisible] = useState(false)

  return (
    <label>
      {label}
      <span className="password-field">
        <input
          type={visible ? 'text' : 'password'}
          value={value}
          onChange={onChange}
          autoComplete={autoComplete}
          required={required}
          minLength={minLength}
        />
        <button
          type="button"
          className="password-toggle"
          aria-label={visible ? 'Hide password' : 'Show password'}
          aria-pressed={visible}
          onClick={() => setVisible((current) => !current)}
        >
          {visible ? <EyeOffIcon /> : <EyeIcon />}
        </button>
      </span>
    </label>
  )
}

function EyeIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6S2 12 2 12z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  )
}

function EyeOffIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <path d="M3 3l18 18" />
      <path d="M10.6 6.2A10.7 10.7 0 0 1 12 6c6.5 0 10 6 10 6a18.4 18.4 0 0 1-4.1 4.6" />
      <path d="M6.1 6.7C3.7 8.4 2 12 2 12s3.5 6 10 6c1.4 0 2.7-.3 3.8-.8" />
      <path d="M9.9 9.9a3 3 0 0 0 4.2 4.2" />
    </svg>
  )
}
