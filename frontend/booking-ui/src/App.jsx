import { useMemo, useState } from "react";
import { createReservation, getAvailability } from "./api";
import "./App.css";

function isoFromDateAndTime(date, time) {
  // date: YYYY-MM-DD
  // time: HH:mm
  // lo mandamos en UTC con Z para que el backend lo reciba como Instant
  return `${date}T${time}:00Z`;
}

export default function App() {
  const [resourceId, setResourceId] = useState(1);
  const [userId, setUserId] = useState(1);
  const [date, setDate] = useState("2026-01-05");

  const [slots, setSlots] = useState([]);
  const [loadingSlots, setLoadingSlots] = useState(false);
  const [slotsError, setSlotsError] = useState("");

  const [startTime, setStartTime] = useState("10:00");
  const [endTime, setEndTime] = useState("11:00");
  const [notes, setNotes] = useState("desde frontend");

  const [creating, setCreating] = useState(false);
  const [createMsg, setCreateMsg] = useState("");

  const startIso = useMemo(() => isoFromDateAndTime(date, startTime), [date, startTime]);
  const endIso = useMemo(() => isoFromDateAndTime(date, endTime), [date, endTime]);

  async function handleLoadSlots() {
    setSlotsError("");
    setCreateMsg("");
    setLoadingSlots(true);
    try {
      const data = await getAvailability(Number(resourceId), date);
      setSlots(data);
    } catch (e) {
      setSlots([]);
      setSlotsError(e.message || "No pude cargar slots");
    } finally {
      setLoadingSlots(false);
    }
  }

  async function handleCreate() {
    setCreateMsg("");
    setSlotsError("");
    setCreating(true);

    try {
      const body = {
        userId: Number(userId),
        resourceId: Number(resourceId),
        startTime: startIso,
        endTime: endIso,
        notes,
      };

      const created = await createReservation(body);
      setCreateMsg(`Reserva creada. id=${created.id}`);
      await handleLoadSlots();
    } catch (e) {
      if (e.status === 409) {
        setCreateMsg(`Conflicto 409. ${e.message}`);
      } else if (e.status === 400) {
        setCreateMsg(`Bad request 400. ${e.message}`);
      } else {
        setCreateMsg(e.message || "Error creando reserva");
      }
    } finally {
      setCreating(false);
    }
  }

  return (
    <div className="container">
      <h1>Gestor de Reservas UI</h1>

      <div className="card">
        <h2>Parámetros</h2>

        <div className="row">
          <label>
            ResourceId
            <input value={resourceId} onChange={(e) => setResourceId(e.target.value)} />
          </label>

          <label>
            UserId
            <input value={userId} onChange={(e) => setUserId(e.target.value)} />
          </label>

          <label>
            Fecha
            <input type="date" value={date} onChange={(e) => setDate(e.target.value)} />
          </label>

          <button onClick={handleLoadSlots} disabled={loadingSlots}>
            {loadingSlots ? "Cargando..." : "Ver disponibilidad"}
          </button>
        </div>

        {slotsError ? <p className="error">{slotsError}</p> : null}

        <div className="slots">
          {slots.length === 0 && !loadingSlots ? (
            <p className="muted">Sin slots cargados o sin disponibilidad</p>
          ) : (
            slots.map((s, idx) => (
              <div key={idx} className="slot">
                <div className="slotTime">
                  <span>{new Date(s.startTime).toISOString().slice(11, 16)}</span>
                  <span> - </span>
                  <span>{new Date(s.endTime).toISOString().slice(11, 16)}</span>
                </div>
                <button
                  onClick={() => {
                    setStartTime(new Date(s.startTime).toISOString().slice(11, 16));
                    setEndTime(new Date(s.endTime).toISOString().slice(11, 16));
                  }}
                >
                  Usar
                </button>
              </div>
            ))
          )}
        </div>
      </div>

      <div className="card">
        <h2>Crear reserva</h2>

        <div className="row">
          <label>
            Inicio
            <input type="time" value={startTime} onChange={(e) => setStartTime(e.target.value)} />
          </label>

          <label>
            Fin
            <input type="time" value={endTime} onChange={(e) => setEndTime(e.target.value)} />
          </label>

          <label className="grow">
            Notas
            <input value={notes} onChange={(e) => setNotes(e.target.value)} />
          </label>

          <button onClick={handleCreate} disabled={creating}>
            {creating ? "Creando..." : "Crear"}
          </button>
        </div>

        <p className="muted">Se envía como UTC con Z. start: {startIso} end: {endIso}</p>

        {createMsg ? <p className="info">{createMsg}</p> : null}
      </div>

      <footer className="muted">
        Backend en 8080. Frontend en 5173.
      </footer>
    </div>
  );
}
