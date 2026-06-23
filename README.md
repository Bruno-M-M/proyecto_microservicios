# Sistema de E-Commerce — Arquitectura de Microservicios

Proyecto semestral de la asignatura **Desarrollo FullStack 1 (DSY1103)** — Duoc UC.  
Sistema de comercio electrónico distribuido implementado con Spring Boot, Spring Cloud y MySQL.

---

## Integrantes del equipo

| Nombre | Rol principal |
|---|---|
| Bruno Mateluna | Desarrollo backend, integración de microservicios |
| Benjamin Martinez | Desarrollo backend, integración de microservicios |

---

## Descripción del dominio

El sistema modela un flujo completo de compra en línea:

- **Clientes** se registran, inician sesión y gestionan su perfil.
- **Inventario** administra el catálogo de productos disponibles.
- **Carrito** permite a los clientes agregar productos y gestionar pedidos.
- **Pago** procesa la emisión de boletas consolidando datos de cliente y carrito.
- **Eureka Server** actúa como registro central de servicios para el descubrimiento automático.
- **API Gateway** centraliza el enrutamiento de todas las solicitudes hacia los microservicios.

---

## Microservicios implementados

| Microservicio | Puerto | Nombre en Eureka | Descripción |
|---|---|---|---|
| Eureka Server | 8761 | — | Registro y descubrimiento de servicios |
| API Gateway | 8080 | api-gateway | Enrutador central de solicitudes |
| ClienteProyecto | 8082 | cliente | Gestión de clientes y autenticación |
| Inventario_Proyecto | 8081 | inventario | Catálogo y stock de productos |
| CarritoProyecto | 8083 | carrito-compras | Gestión de carritos y pedidos |
| Pago_Proyecto | 8084 | metodo-de-pago | Emisión de boletas y procesamiento de pagos |

---

## Rutas principales del API Gateway

El Gateway corre en `http://localhost:8080` y redirige según el prefijo de la ruta:

| Ruta Gateway | Microservicio destino | Descripción |
|---|---|---|
| `/api/clientes/**` | ClienteProyecto (8082) | CRUD de clientes, login |
| `/api/products/**` | Inventario_Proyecto (8081) | CRUD de productos |
| `/api/v1/Carrito/**` | CarritoProyecto (8083) | CRUD de carritos y pedidos |
| `/api/v1/boletas/**` | Pago_Proyecto (8084) | Emisión y consulta de boletas |

---

## Documentación Swagger / OpenAPI

Cada microservicio expone su documentación en `/doc/swagger-ui.html`:

| Microservicio | Swagger local |
|---|---|
| ClienteProyecto | http://localhost:8082/doc/swagger-ui.html |
| Inventario_Proyecto | http://localhost:8081/doc/swagger-ui.html |
| CarritoProyecto | http://localhost:8083/doc/swagger-ui.html |
| Pago_Proyecto | http://localhost:8084/doc/swagger-ui.html |

---

## Instrucciones de ejecución

### Prerrequisitos

- Java 21
- Maven 3.9+
- MySQL 8 (para ejecución local)
- Docker Desktop (para ejecución en contenedores)

### Ejecución local (desde el IDE)

Levantar los servicios en este orden:

**1. Eureka Server**
```bash
cd Eureka_Server
mvn spring-boot:run
# Acceder al dashboard: http://localhost:8761
```

**2. Microservicios** (en cualquier orden, después de Eureka)

Crear las bases de datos en MySQL antes de iniciar:
```sql
CREATE DATABASE db_cliente_proyecto_dev;
CREATE DATABASE db_inventario_proyecto_dev;
CREATE DATABASE db_carrito_compras;
CREATE DATABASE db_boletas;
```

Luego iniciar cada servicio:
```bash
cd ClienteProyecto   && mvn spring-boot:run   # perfil dev por defecto
cd Inventario_Proyecto && mvn spring-boot:run  # perfil dev por defecto
cd CarritoProyecto   && mvn spring-boot:run
cd Pago_Proyecto     && mvn spring-boot:run
```

**3. API Gateway** (último, después de que los servicios estén registrados en Eureka)
```bash
cd Api_Gateway
mvn spring-boot:run
```

---

### Ejecución con Docker

Cada microservicio tiene su propio `Dockerfile` y `docker-compose.yml`.  
Para levantar un servicio individual con Docker:

```bash
# Ejemplo con ClienteProyecto
cd ClienteProyecto
docker-compose up --build
```

Para levantar **todos los servicios** desde la raíz del proyecto, primero compilar cada uno:

```bash
# Construir imagen de Eureka
cd Eureka_Server && docker build -t eureka-server .

# Construir imagen del Gateway
cd ../Api_Gateway && docker build -t api-gateway .

# Construir microservicios
cd ../ClienteProyecto      && docker build -t cliente-service .
cd ../Inventario_Proyecto  && docker build -t inventario-service .
cd ../CarritoProyecto      && docker build -t carrito-service .
cd ../Pago_Proyecto        && docker build -t pago-service .
```

Levantar en orden con Docker run:
```bash
# 1. Eureka
docker run -d -p 8761:8761 --name eureka eureka-server

# 2. Microservicios (reemplazar <IP_HOST> con tu IP local)
docker run -d -p 8082:8082 -e SPRING_PROFILES_ACTIVE=docker cliente-service
docker run -d -p 8081:8081 -e SPRING_PROFILES_ACTIVE=docker inventario-service
docker run -d -p 8083:8083 carrito-service
docker run -d -p 8084:8084 pago-service

# 3. Gateway
docker run -d -p 8080:8080 api-gateway
```

---

## Tecnologías utilizadas

- **Spring Boot 3.x** — framework base de cada microservicio
- **Spring Cloud Netflix Eureka** — service discovery
- **Spring Cloud Gateway (WebMVC)** — API Gateway
- **Spring Cloud OpenFeign** — comunicación REST entre servicios
- **Spring Data JPA + MySQL** — persistencia de datos
- **Springdoc OpenAPI (Swagger UI)** — documentación de endpoints
- **JUnit 5 + Mockito** — pruebas unitarias
- **Docker** — contenerización de servicios
- **Maven** — gestión de dependencias y build

---

## Pruebas unitarias

Las pruebas se encuentran en `src/test/java` de cada microservicio:

| Microservicio | Clases de test |
|---|---|
| ClienteProyecto | `ClienteServiceTest`, `ClienteControllerTest` |
| Inventario_Proyecto | `ProductServiceTest`, `ProductControllerTest` |
| CarritoProyecto | `CarritoServiceTest`, `CarritoControllerTest` |
| Pago_Proyecto | `BoletaServiceTest`, `BoletaControllerTest` |

Para ejecutar las pruebas de un microservicio:
```bash
cd ClienteProyecto
mvn test
```
