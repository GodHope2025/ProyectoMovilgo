# Estándares de trabajo del Grupo 7 · MovilGo Perú

Estas reglas las acordamos todos los integrantes. **Checkstyle** las verifica automáticamente en cada `push` y
pull request: si no se cumplen, GitHub Actions marca el cambio en rojo y no se puede integrar.

## 1. Ramas (Git Flow simplificado)

| Rama | Uso |
|---|---|
| `main` | Versión estable. Cada integración dispara el despliegue en Render (CD). |
| `develop` | Integración del sprint. Aquí se unen los componentes de todos. |
| `feature/HU-XX-descripcion` | Una rama por historia de usuario o componente. |

Nadie sube cambios directo a `main` ni a `develop`: todo entra por **pull request**.

## 2. Commits (Conventional Commits)

```
feat(reservas): valida que las fechas no se crucen
fix(pagos): rechaza pagos fuera de los 15 minutos
test(alquiler): prueba de alerta de no devolución
ci: agrega despliegue continuo a Render
docs: actualiza el README
```

## 3. Código Java

- Clases en `PascalCase`, métodos y variables en `camelCase`, constantes en `MAYUSCULAS_CON_GUION`.
- Sufijos por capa: `XxxController`, `XxxService`, `XxxRepository`, `XxxForm` (DTO), `XxxJob` (tareas programadas).
- Las **reglas de negocio solo van en la capa de servicios**. Los controladores solo reciben y responden.
- Las validaciones de entrada van en los DTO con **Bean Validation** (`@NotBlank`, `@Pattern`, `@Email`).
- Los errores de negocio se lanzan con `ReglaNegocioException` y se muestran con `ManejadorGlobal`.
- Inyección por constructor (sin `@Autowired` en atributos).
- La hora se obtiene del `Clock` del proyecto (zona America/Lima) para poder probarla.
- Sin importaciones con `*` ni importaciones sin usar; siempre llaves en `if`, `for` y `while`; líneas de hasta 140 caracteres.
- Cada regla de negocio nueva lleva su **prueba unitaria** (JUnit 6 + Mockito + AssertJ).

## 4. Vistas (Thymeleaf + Bootstrap)

- Todas las páginas usan los fragmentos de `templates/fragmentos/layout.html` (cabecera, menú y pie).
- Solo componentes de Bootstrap 5.3 y la paleta MovilGo (`#0F766E`, `#134E4A`).
- Diseño mobile-first: se revisa en 375, 768 y 1280 px.

## 5. Pull request

1. Crea tu rama desde `develop` y haz commits pequeños.
2. Abre el pull request hacia `develop` y completa la lista de verificación de la plantilla.
3. Espera el **check verde** de GitHub Actions (estándares + compilación + pruebas).
4. **Otro integrante** revisa el código, deja comentarios si hace falta y aprueba.
5. Se integra con *merge* y se borra la rama.
