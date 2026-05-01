# Frontend Medicore System

Aplicacion React + Vite + TypeScript para consumir la API Spring Boot del sistema medico.

## Requisitos

- Node.js 18 o superior
- Backend ejecutandose en `http://localhost:8080`

## Comandos

```bash
npm install
npm run dev
```

Build de produccion:

```bash
npm run build
```

Vite expone la aplicacion en `http://localhost:5173` y proxifica las rutas de la API hacia el backend.

Si el backend usa otra URL, define:

```bash
VITE_API_URL=http://localhost:8080 npm run dev
```
