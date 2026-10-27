# MovilGo Perú — Sistema web de alquiler y venta de vehículos

Proyecto del **Grupo 7** — UD Implementación de Soluciones para Plataformas Web (CERTUS, 2026).

Aplicación web de inventario y punto de venta (POS) para alquilar autos, camionetas 4x4, vans, buses y scooters a empresas y personas, y vender unidades seminuevas.

## Tecnologías

| Componente | Versión |
| --- | --- |
| Java | 17 (LTS) |
| Spring Boot | 3.x (Spring MVC, Spring Data JPA, Spring Security) |
| Thymeleaf + Bootstrap | 3.1 / 5.3 |
| MySQL | 8.x |
| Maven | 3.9 |
| IDE | Eclipse con Spring Tools |

## Integrantes

- Florcita Ivon Quinde Umbo
- Fernando Alex Carmac Pintado
- Gustavo Alonso Condor Llanos
- Edy
- Renato
- Benjamin

## Cómo ejecutar el proyecto

1. Clonar el repositorio:
   ```
   git clone <URL-DEL-REPOSITORIO>
   ```
2. Crear la base de datos en MySQL:
   ```sql
   CREATE DATABASE movilgo_db;
   ```
3. Copiar `src/main/resources/application-local.properties.example` como `application-local.properties` y poner tu usuario y contraseña de MySQL. Ese archivo **no se sube** al repositorio.
4. En Eclipse: *File → Import → Maven → Existing Maven Projects* y seleccionar la carpeta.
5. Ejecutar la clase principal (`MovilgoApplication`) con *Run As → Spring Boot App*.
6. Abrir `http://localhost:8080` en el navegador.

## Ramas

| Rama | Uso |
| --- | --- |
| `main` | Versión estable que se presenta en cada evidencia. Protegida. |
| `develop` | Integración del trabajo del equipo. |
| `feature/<modulo>` | Una rama por funcionalidad (ej. `feature/reservas`). Se crea desde `develop`. |
| `hotfix/<descripcion>` | Correcciones urgentes sobre `main`. |

**Flujo:** crear tu rama desde `develop` → trabajar → abrir un *pull request* hacia `develop` → otro integrante lo revisa → se integra.

## Convención de commits

```
feat: nueva funcionalidad          (feat: validar solapamiento de reservas)
fix: corrección de un error        (fix: cálculo de recargo por atraso)
docs: documentación                (docs: actualizar README)
refactor: mejora sin cambiar lógica
test: pruebas
```

## Estructura del proyecto

```
src/main/
 ├── java/pe/movilgo/
 │   ├── config/        seguridad y caché
 │   ├── controller/    controladores MVC
 │   ├── service/       reglas de negocio
 │   │   └── tarifa/    estrategias de cálculo (Strategy)
 │   ├── repository/    repositorios Spring Data JPA
 │   ├── model/         entidades JPA
 │   ├── dto/           objetos de transferencia
 │   └── event/         eventos y alertas (Observer)
 └── resources/
     ├── templates/     vistas Thymeleaf
     ├── static/        CSS, JavaScript e imágenes
     └── application.properties
```
