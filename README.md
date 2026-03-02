# API Productos

API REST para gestión de productos con **Spring Boot 4.0.3**, **Azure SQL Server** e integración con **ImgBB**.

---

##  Características

- ✅ CRUD completo (Crear, Leer, Actualizar, Eliminar)
- ✅ Integración con ImgBB para almacenamiento de imágenes
- ✅ Base de datos Azure SQL Server
- ✅ Documentación automática con Swagger/OpenAPI
- ✅ Spring Data JPA + Lombok
---

##  Estructura

```
apiProductos/
├── controller/          → ProductoController.java
├── service/             → ProductoService.java
├── model/               → Producto.java
├── repository/          → ProductoRepository.java
├── integration/         → ImgBBService.java
├── config/              → SwaggerConfig.java
└── resources/           → application.properties
```

---


##  Endpoints API

**Base URL:** `http://localhost:8080/api/productos`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Obtener todos los productos |
| GET | `/{id}` | Obtener producto por ID |
| POST | `/` | Crear nuevo producto |
| PUT | `/{id}` | Actualizar producto |
| DELETE | `/{id}` | Eliminar producto |

##  Swagger/OpenAPI

**Acceso:** `http://localhost:8080/swagger-ui.html`

**JSON Schema:** `http://localhost:8080/v3/api-docs`

Todos los endpoints están documentados y son probables desde Swagger UI.

---
