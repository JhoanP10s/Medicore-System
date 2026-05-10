# Checklist De Demo - Medicore System

## Backend

- [ ] Ejecutar pruebas:

```bash
./mvnw clean test
```

- [ ] Empaquetar backend:

```bash
./mvnw clean -DskipTests package
```

## Frontend

- [ ] Build con Docker:

```bash
cd frontend
docker build --no-cache -t medicore-frontend-demo .
```

- [ ] O build local si hay Node/npm:

```bash
cd frontend
npm install
npm run build
```

## Docker Compose

- [ ] Validar configuracion:

```bash
docker compose config
```

- [ ] Resetear datos demo:

```bash
docker compose down -v
```

- [ ] Levantar todo:

```bash
docker compose up --build
```

- [ ] Validar URLs:
  - [ ] Frontend: http://localhost:5173
  - [ ] Backend: http://localhost:8080
  - [ ] Swagger: http://localhost:8080/swagger-ui/index.html

## Credenciales

- [ ] ADMIN: `admin@medicore.com / Admin12345`
- [ ] DOCTOR: `laura.doctor@medicore.com / Doctor12345`
- [ ] USER: `user@medicore.com / User12345`

## Flujos Funcionales

- [ ] Login ADMIN.
- [ ] Dashboard ADMIN carga metricas globales.
- [ ] Ver especialidades.
- [ ] Ver medicos.
- [ ] Ver pacientes.
- [ ] Ver disponibilidad medica.
- [ ] Ver bloqueos de agenda.
- [ ] Consultar agenda y horarios disponibles.
- [ ] Crear cita.
- [ ] Confirmar cita.
- [ ] Login DOCTOR.
- [ ] Dashboard DOCTOR carga metricas propias.
- [ ] Ver citas propias.
- [ ] Crear historia clinica desde cita confirmada o completada.
- [ ] Ver historia clinica creada.
- [ ] Ver acceso denegado en rutas no permitidas.

## Observaciones Para Presentacion

- [ ] Explicar que las credenciales son solo demo/desarrollo.
- [ ] Explicar que `USER` aun no tiene portal paciente completo.
- [ ] Explicar que produccion requiere migraciones, auditoria y hardening de seguridad.
