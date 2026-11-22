# MovilGo Perú · Grupo 7

Plataforma web para alquilar camionetas 4x4, buses de personal, minivans, autos y scooters a empresas mineras,
empresas y personas, y vender los seminuevos de la flota.

Curso: **Implementación de Soluciones para Plataformas Web** · Certus Arequipa · Docente: Jonathan Uriel Chavez Ramirez

[![CI - integración continua](https://github.com/GodHope2025/ProyectoMovilgo/actions/workflows/ci.yml/badge.svg)](https://github.com/GodHope2025/ProyectoMovilgo/actions/workflows/ci.yml)
[![CD - despliegue en la nube](https://github.com/GodHope2025/ProyectoMovilgo/actions/workflows/cd.yml/badge.svg)](https://github.com/GodHope2025/ProyectoMovilgo/actions/workflows/cd.yml)

## Stack

| Capa | Tecnología |
|---|---|
| Backend | Spring Boot 4.1 · Java 21 · Spring MVC · Spring Security 7 |
| Vistas | Thymeleaf 3.1 · Bootstrap 5.3 |
| Persistencia | Spring Data JPA · Hibernate 7 · MySQL 8 · HikariCP |
| Calidad | JUnit 6 · Mockito · AssertJ · JaCoCo · Checkstyle |
| Integración y entrega | Git · GitHub · GitHub Actions · Docker · Render |

## Componentes y responsables (Sprint 3)

| Componente | Rama | Responsable |
|---|---|---|
| Integración continua, entrega continua y estándares | `feature/HU-09-ci-cd-estandares` | Gustavo Alonso Condor Llanos |
| Usuarios, roles y seguridad (BCrypt, bloqueo por intentos) | `feature/HU-10-usuarios-seguridad` | Christopher Benjamín Vega Yanac |
| Registro de clientes con DNI/RUC y licencia | `feature/HU-11-registro-clientes` | Florcita Ivon Quinde Umbo |
| Reservas sin cruces de fechas (`@Version`, 15 minutos) | `feature/HU-12-reservas` | Fernando Alex Carmac Pintado |
| Pagos y tarifas por tipo de cliente (Strategy) | `feature/HU-13-pagos-tarifas` | Renato Sebastián Torres Rodríguez |
| Entrega, devolución y alerta de no devolución (`@Scheduled`) | `feature/HU-14-alquileres-alertas` | Edy Winder Gutierrez |

## Flujo de integración

```
feature/HU-XX  --pull request + revisión + CI verde-->  develop  --pull request de release-->  main  --CD-->  Render
```

- **CI** (`.github/workflows/ci.yml`): en cada push y pull request revisa los estándares con Checkstyle,
  compila, ejecuta las pruebas JUnit y mide la cobertura con JaCoCo.
- **CD** (`.github/workflows/cd.yml`): al integrar en `main` vuelve a probar todo y, solo si pasa,
  despliega en Render y verifica que la aplicación responda en `/actuator/health`.
- Las reglas del equipo están en [CONTRIBUTING.md](CONTRIBUTING.md).

## Ejecutar en local (Eclipse + XAMPP)

1. Inicia MySQL en XAMPP (usuario `root` sin contraseña). La base `movilgo_db` se crea sola.
2. En Eclipse: *File > Import > Existing Maven Projects* y elige esta carpeta.
3. Ejecuta `MovilgoApplication` (*Run As > Spring Boot App* o *Java Application*).
4. Abre http://localhost:8080

Por consola: `mvn spring-boot:run` · Pruebas: `mvn verify`

### Usuarios de prueba

| Rol | Correo |
|---|---|
| Administrador | admin@movilgo.pe |
| Vendedor | vendedor@movilgo.pe |
| Cliente persona (DNI) | cliente@movilgo.pe |
| Cliente empresa (RUC) | minera@movilgo.pe |

La contraseña de prueba se define con la variable `MOVILGO_DEMO_CLAVE` (en local, `MovilGo2026*`).

## Variables de entorno en la nube

| Variable | Uso |
|---|---|
| `DB_URL` | URL JDBC de MySQL |
| `DB_USER` / `DB_PASSWORD` | Credenciales de la base de datos |
| `MOVILGO_DEMO_CLAVE` | Contraseña de los usuarios de prueba |
| `PORT` | Puerto asignado por Render |
