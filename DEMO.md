# Guion De Demo - Medicore System

## Objetivo

Demostrar como Medicore System centraliza la operacion diaria de una clinica pequena: pacientes, medicos, agenda, citas, historia clinica y dashboard por rol.

## Preparacion Del Entorno

1. Copiar variables de entorno:

```bash
cp .env.example .env
```

2. Confirmar perfil demo:

```env
SPRING_PROFILES_ACTIVE=prod,demo
```

3. Resetear y levantar:

```bash
docker compose down -v
docker compose up --build
```

4. Abrir:
- Frontend: http://localhost:5173
- Swagger: http://localhost:8080/swagger-ui/index.html

## Credenciales

| Rol | Email | Password |
| --- | --- | --- |
| ADMIN | `admin@medicore.com` | `Admin12345` |
| DOCTOR | `laura.doctor@medicore.com` | `Doctor12345` |
| USER | `user@medicore.com` | `User12345` |

## Flujo Paso A Paso

### 1. Abrir Frontend

Pantalla: Login.

Accion: abrir http://localhost:5173.

Valor: mostrar que es una aplicacion web lista para operar desde navegador.

### 2. Login Como ADMIN

Usuario: `admin@medicore.com`.

Accion: iniciar sesion.

Valor: el administrador ve una vision global de la clinica.

### 3. Mostrar Dashboard Global

Pantalla: Dashboard.

Explicar:
- Pacientes activos.
- Medicos activos.
- Especialidades.
- Citas de hoy.
- Historias clinicas.
- Alertas operativas.

Valor: la direccion de la clinica puede entender actividad y pendientes en segundos.

### 4. Mostrar Especialidades, Medicos Y Pacientes

Pantallas: Especialidades, Medicos, Pacientes.

Accion: navegar por los modulos.

Valor: el sistema centraliza la informacion base de la operacion clinica.

### 5. Mostrar Disponibilidad Medica

Pantalla: Disponibilidad Medica.

Accion: mostrar horarios de la Dra. Laura Rojas y otros medicos.

Valor: las citas no se crean en horarios arbitrarios; dependen de disponibilidad real.

### 6. Mostrar Bloqueos De Agenda

Pantalla: Bloqueos de Agenda.

Accion: mostrar capacitaciones, reuniones y bloqueos inactivos.

Valor: la clinica puede bloquear horarios sin eliminar historial.

### 7. Consultar Agenda

Pantalla: Agenda.

Accion:
- Seleccionar medico.
- Seleccionar fecha futura con disponibilidad.
- Elegir duracion de 30 minutos.
- Consultar horarios disponibles.

Valor: el sistema evita solapamientos y respeta disponibilidad/bloqueos.

### 8. Crear Cita

Pantalla: Citas > Nueva cita.

Accion:
- Seleccionar paciente.
- Seleccionar medico.
- Consultar slots disponibles.
- Crear cita.

Valor: se programa una cita con reglas clinicas y operativas.

### 9. Confirmar Cita

Pantalla: Citas.

Accion: cambiar estado de PROGRAMADA a CONFIRMADA.

Valor: el estado permite gestionar el ciclo de atencion.

### 10. Cerrar Sesion E Ingresar Como DOCTOR

Usuario: `laura.doctor@medicore.com`.

Valor: mostrar separacion por rol y ownership.

### 11. Mostrar Dashboard Del Doctor

Pantalla: Dashboard.

Explicar:
- Mis citas de hoy.
- Mis proximas citas.
- Pendientes de historia clinica.
- Historias recientes.

Valor: cada medico ve solo su operacion clinica.

### 12. Ver Citas Propias

Pantalla: Citas.

Accion: listar citas como DOCTOR.

Valor: ownership evita ver o gestionar citas de otros medicos.

### 13. Crear Historia Clinica

Pantalla: Citas > Crear historia clinica.

Accion:
- Usar una cita confirmada.
- Registrar sintomas, diagnostico y tratamiento.

Valor: la atencion queda documentada y asociada a cita, paciente y medico.

### 14. Volver Al Dashboard

Pantalla: Dashboard del DOCTOR.

Valor: mostrar que metricas y pendientes se actualizan con la operacion.

### 15. Mostrar Restricciones De Acceso

Accion: intentar acceder a una ruta administrativa como DOCTOR, por ejemplo Medicos o Especialidades.

Valor: los permisos reducen exposicion de informacion y errores operativos.

## Cierre Comercial

Mensaje sugerido:

Medicore System permite a una clinica pequena digitalizar su operacion base sin depender de agendas manuales: controla disponibilidad, evita choques de horario, documenta historias clinicas y entrega visibilidad operativa por rol.
