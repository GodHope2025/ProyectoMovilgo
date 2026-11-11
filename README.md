# MovilGo Perú — Sistema web de alquiler y venta de vehículos

Proyecto del **Grupo 7** — UD Implementación de Soluciones para Plataformas Web (CERTUS, 2026).

Aplicación web para alquilar camionetas 4x4, buses, autos y scooters a empresas (incluidas mineras) y personas, y vender unidades seminuevas.

## Tecnologías

| Componente | Versión |
| --- | --- |
| Java (OpenJDK) | 21 LTS |
| Spring Boot (MVC, Data JPA, Security, Validation, Cache) | 4.1.x |
| Hibernate ORM | 7.x (gestionada por Spring Boot) |
| Thymeleaf + Bootstrap | 3.1 / 5.3 |
| MySQL | 8.x (o MySQL/MariaDB de XAMPP) |
| Maven | 3.9 |
| IDE | Eclipse IDE |

Arquitectura: MVC por capas en un monolito modular con API REST (`/api/v1/**`).

## Estructura

```
src/main/java/pe/movilgo/movilgo/
├── config/          SecurityConfig, datos iniciales
├── controller/      controladores web (@Controller)
│   └── api/         API REST (@RestController)
├── service/         reglas de negocio (@Service)
│   └── tarifa/      estrategias de tarifa (Strategy)
├── repository/      interfaces JpaRepository
├── model/           entidades JPA (Vehiculo con @Version)
├── dto/             objetos de transferencia
├── event/           alertas programadas (Observer, @Scheduled)
└── exception/       manejo global de errores
src/main/resources/templates/   vistas Thymeleaf
```

## Integrantes

- Florcita Ivon Quinde Umbo
- Fernando Alex Carmac Pintado
- Gustavo Alonso Condor Llanos (Product Owner)
- Edy Winder Gutierrez
- Renato Sebastián Torres Rodríguez (Scrum Master)
- Christopher Benjamín Vega Yanac

## Cómo ejecutar el proyecto

1. Clonar el repositorio y abrirlo en Eclipse: *File → Import → Maven → Existing Maven Projects*.
2. Encender MySQL (por ejemplo, desde el XAMPP Control Panel). La base `movilgo_db` se crea sola al iniciar.
3. Revisar usuario y contraseña de MySQL en `src/main/resources/application.properties` (por defecto `root` sin contraseña, como en XAMPP).
4. Ejecutar `MovilgoApplication` con *Run → Run As → Java Application*.
5. Abrir `http://localhost:8080` (catálogo) o `http://localhost:8080/api/v1/vehiculos` (API REST).

## Flujo de trabajo

Ramas `main`, `develop` y `feature/<historia>`; commits con Conventional Commits; cada cambio entra a `develop` mediante un pull request revisado por otro integrante. Tablero del sprint en GitHub Projects.
