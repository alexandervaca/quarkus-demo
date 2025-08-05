# code-with-quarkus

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.

## Provided Code

# Employee Management REST API with AWS Integration

Este proyecto implementa una API RESTful desarrollada con Java 17 y Quarkus 3.17.6, desplegable sobre infraestructura de AWS. Está orientada a la gestión de empleados e incorpora autenticación con JWT, manejo de sesiones temporales, operaciones CRUD básicas y consultas optimizadas de manera asíncrona.

# REST API
# Descripcion del challenge solicitado

Se requiere crear una API RESTFul utilizando servicios de AWS, con las siguientes funcionalidades:

- Permitir hacer login con nombre de usuario y clave. Debe devolver un token de expiración automática (por default de 5 minutos pero debe ser parametrizable).
- Permitir registrar/actualizar un empleado. El API debe esperar un json válido con atributos mínimos: nombre, email, supervisor_id.
- Obtener los detalles de todos los empleados y sus datetime de última actualización.
- Obtener los detalles de un empleado dado su ID. Además de los atributos indicados, el detalle por ID debe indicar: datetime de última actualización y cantidad de empleados a cargo (directos). La obtención de detalles del empleado y su cantidad de personal a cargo debe hacerse en forma paralela con objetivo de responder más rápidamente el request.

Todos los endpoints excepto el de login deben validar el token de sesión y devolver un error adecuado si no hay token, o el mismo es inválido/caducado.


## 🔧 Tecnologías Utilizadas

- **Java 17**
- **Quarkus 3.17.6 (Quarkus, Quarkus Security, Hibernate)**
- **JWT (JSON Web Token) para autenticación**
- **PostgreSQL)**
- **OpenAPI (Swagger) para documentación**
- **Maven**

### Para generacion de claves publica y privada se uso lo siguiente:
- **openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out privateKey.pem**
- **openssl rsa -in privateKey.pem -pubout -out publicKey.pem**

---

## ✅ Funcionalidades

### 1. Login
- Endpoint: `POST /api/auth/login`
- Permite autenticarse con usuario y contraseña.
- Devuelve un **token JWT** con expiración por defecto de **5 minutos** (parametrizable por configuración).
- Ejemplo de request:
```json
{
  "username": "usuario",
  "password": "clave123"
}


