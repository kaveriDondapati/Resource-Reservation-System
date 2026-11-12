export const RESOURCE_TYPES = [
  { value: 'MEETING_ROOM', label: 'Meeting room' },
  { value: 'DESK', label: 'Desk' },
  { value: 'EQUIPMENT', label: 'Equipment' },
  { value: 'VEHICLE', label: 'Vehicle' }
]

export function typeLabel(value) {
  return RESOURCE_TYPES.find((type) => type.value === value)?.label ?? value
}

export function formatWhen(value) {
  if (!value) {
    return ''
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return value
  }
  return new Intl.DateTimeFormat(undefined, {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: '2-digit'
  }).format(date)
}

export function toDateTimeInput(date) {
  const pad = (part) => String(part).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`
}

export function nextHourRange() {
  const start = new Date()
  start.setMinutes(0, 0, 0)
  start.setHours(start.getHours() + 1)
  const end = new Date(start.getTime() + 60 * 60 * 1000)
  return { start: toDateTimeInput(start), end: toDateTimeInput(end) }
}

export function toApiDateTime(value) {
  if (!value) {
    return value
  }
  return value.length === 16 ? `${value}:00` : value
}
