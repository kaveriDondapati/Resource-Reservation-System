export async function api(path, { method = 'GET', body, token } = {}) {
  const headers = { Accept: 'application/json' }
  if (body !== undefined) {
    headers['Content-Type'] = 'application/json'
  }
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  let response
  try {
    response = await fetch(path, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body)
    })
  } catch {
    throw new Error('Cannot reach the API gateway. Start it on port 8080, then try again.')
  }

  if (response.status === 204) {
    return null
  }

  const text = await response.text()
  let data = null
  if (text) {
    try {
      data = JSON.parse(text)
    } catch {
      data = null
    }
  }

  if (!response.ok) {
    const fieldText = data?.fields ? Object.values(data.fields).filter(Boolean).join(' ') : ''
    const message = fieldText
      || data?.message
      || (response.status === 404
        ? 'The reservation API is not responding on port 8080. Start the API gateway, then refresh.'
        : `Request failed (${response.status})`)
    const error = new Error(message)
    error.status = response.status
    throw error
  }

  return data
}
