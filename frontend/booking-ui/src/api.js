const BASE_URL = "http://localhost:8080";

export async function getAvailability(resourceId, date) {
  const url = `${BASE_URL}/api/resources/${resourceId}/availability?date=${date}`;
  const res = await fetch(url);
  if (!res.ok) throw new Error(`Error ${res.status}`);
  return res.json();
}

export async function createReservation(body) {
  const res = await fetch(`${BASE_URL}/api/reservations`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  const text = await res.text();
  let data = null;
  try { data = text ? JSON.parse(text) : null; } catch {}

  if (!res.ok) {
    const msg = data?.message || `Error ${res.status}`;
    const err = new Error(msg);
    err.status = res.status;
    err.body = data;
    throw err;
  }

  return data;
}
