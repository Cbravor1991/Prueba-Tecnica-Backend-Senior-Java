# Prueba Técnica: Desarrollador Backend Senior - FinTech (Java)

[Documentación](#documentación) | [Enunciado](#enunciado)

## Documentación

### 1. # Descripción del Proyecto

## Fintech API

La **Fintech API** es una aplicación diseñada para gestionar transacciones financieras, cuentas de usuarios y proporcionar reportes financieros detallados. Este proyecto tiene como objetivo crear una API robusta y segura que permita a los usuarios realizar operaciones como depósitos, retiros y transferencias entre cuentas, así como consultar el historial de transacciones y generar reportes financieros.

### Objetivos

- **Gestión de cuentas**: Permitir a los usuarios crear, actualizar y eliminar cuentas, así como consultar información detallada sobre ellas.
- **Transacciones financieras**: Facilitar depósitos, retiros y transferencias entre cuentas, asegurando la validación de saldos y monedas.
- **Reportes**: Ofrecer funcionalidades para generar reportes financieros personalizados basados en el historial de transacciones.
- **Auditoría y seguridad**: Implementar un sistema de auditoría y registro de logs para garantizar la seguridad y el seguimiento de las operaciones realizadas.

### Estructura del Proyecto

El proyecto está estructurado de la siguiente manera:

- **Controladores**: Manejan las solicitudes HTTP y gestionan la lógica de negocio, interactuando con los servicios.
- **Servicios**: Contienen la lógica de negocio principal y se encargan de la interacción con los repositorios.
- **Repositorios**: Manejan la persistencia de datos utilizando JPA y se comunican con la base de datos.
- **Modelos**: Definen las entidades que representan los datos dentro del sistema.
- **Configuraciones**: Contienen la configuración de seguridad, programación asíncrona y Swagger para la documentación de la API.

Esta estructura modular permite un fácil mantenimiento y escalabilidad, facilitando la adición de nuevas funcionalidades en el futuro.

### 2. Instalación y Configuración

Para clonar el repositorio, configurar la base de datos y ejecutar la API localmente, sigue los siguientes pasos:

#### 2.1 Clonar el Repositorio

1. Abre una terminal o consola.
2. Ejecuta el siguiente comando para clonar el repositorio:
   ```bash
   git clone https://github.com/Cbravor1991/Prueba-Tecnica-Backend-Senior-Java.git
3. Accede al directorio del proyecto:
   ```bash
   cd Prueba-Tecnica-Backend-Senior-Java

### 2.2 Configurar la Base de Datos

Asegúrate de tener PostgreSQL instalado y en funcionamiento en tu máquina. Crea una nueva base de datos llamada `fintechdb` utilizando el siguiente comando en la consola de PostgreSQL:
   ```sql
   CREATE DATABASE fintechdb;
   ```

Configura el archivo `application.properties` en el directorio `src/main/resources/` con la dirección necesaria de
la base de datos, y tus credenciales, actualmente el mismo es el siguiente
 ```bash
spring.datasource.url=jdbc:postgresql://localhost:3306/fintechdb
spring.datasource.username=postgres
spring.datasource.password=1234
```

### 3. Endpoints de la API

### 3.1 Transacciones

### 3.1.1 Realizar un Depósito

- **Endpoint:** `POST /api/transacciones/deposito/{cuentaId}`
- **Descripción:** Permite realizar un depósito en la cuenta especificada.
- **Request Body:**
  ```json
  {
    "monto": 500.00
  }

### Response:

- **Status 200:** Depósito realizado exitosamente.

  ```json
  {
    "id": 1,
    "monto": 500.00,
    "tipo": "DEPOSITO",
    "fecha": "2024-10-16T15:30:00"
  }

- **Status 404:** Usuario o cuenta no encontrado.
- **Status 403:** Usuario no autorizado.
- **Status 500:** Error en el servidor.

### 3.1.2. Realizar un Retiro

**Endpoint:** `POST /api/transacciones/retiro/{cuentaId}`  
**Descripción:** Realiza un retiro de la cuenta especificada.

### Request Body:
```json
{
  "monto": 200.00
}
```

### Response:

- **Status 200:** Retiro realizado exitosamente.

```json
{
  "id": 2,
  "monto": 200.00,
  "tipo": "RETIRO",
  "fecha": "2024-10-16T15:45:00"
}
```
- **Status 404:** Usuario o cuenta no encontrado.
- **Status 403:** Usuario no autorizado.
- **Status 500:** Error en el servidor.

### 3.1.3 Realizar una Transferencia

**Endpoint:** `POST /api/transacciones/transferencia/{cuentaOrigenId}`  
**Descripción:** Permite realizar una transferencia desde una cuenta de origen a una cuenta destino.

### Request Body:
```json
{
"monto": 300.00,
"cuentaDestinoId": 5
}
```

### Response:

- **Status 200:** Retiro realizado exitosamente.
```json
{
"id": 3,
"monto": 300.00,
"tipo": "TRANSFERENCIA",
"fecha": "2024-10-16T16:00:00",
"cuentaDestinoId": 5
}
```

- **Status 404:** Usuario o cuenta no encontrado.
- **Status 403:** Usuario no autorizado.
- **Status 500:** Error en el servidor.

## 3.1.4. Obtener Historial de Transacciones

**Endpoint:** `GET /api/transacciones/historial/{cuentaId}`  
**Descripción:** Devuelve el historial de transacciones de una cuenta.

### Query Params:
- **tipo** (opcional): Filtrar por tipo de transacción (DEPOSITO, RETIRO, TRANSFERENCIA).
- **fechaDesde** (opcional): Filtrar desde una fecha específica.
- **fechaHasta** (opcional): Filtrar hasta una fecha específica.
- **page** (opcional): Número de página (default: 0).
- **size** (opcional): Tamaño de página (default: 10).

### Response:
- **Status 200:** Historial de transacciones.
```json
[
{
"id": 1,
"monto": 500.00,
"tipo": "DEPOSITO",
"fecha": "2024-10-16T15:30:00"
},
{
"id": 2,
"monto": 200.00,
"tipo": "RETIRO",
"fecha": "2024-10-16T15:45:00"
}
]
```
- **Status 404:** Usuario o cuenta no encontrado.
- **Status 403:** Usuario no autorizado.
- **Status 500:** Error en el servidor.

## 3.1.5. Generar Reporte Financiero

**Endpoint:** `GET /api/transacciones/reportes/{cuentaId}`  
**Descripción:** Genera un reporte financiero para una cuenta en un rango de fechas.

### Query Params:
- **fechaDesde:** Fecha inicial del reporte.
- **fechaHasta:** Fecha final del reporte.

### Response:
- **Status 200:** Reporte financiero generado.

```json
{
"saldoInicial": 1000.00,
"totalDepositos": 500.00,
"totalRetiros": 200.00,
"saldoFinal": 1300.00,
"fechaDesde": "2024-10-01T00:00:00",
"fechaHasta": "2024-10-16T23:59:59"
}

```

- **Status 404:** Usuario o cuenta no encontrado.
- **Status 403:** Usuario no autorizado.
- **Status 500:** Error en el servidor.


### 3.2 Usuarios

### 3.2.1 Registro de Usuario

- **Endpoint:** `POST /api/auth/register`
- **Descripción:** Permite registrar un nuevo usuario en el sistema.
- **Request Body:**
  ```json
  {
      "username": "nuevo_usuario",
      "password": "contraseña_segura",
      "nombreCompleto": "Nombre Completo"
  }
  ```


### Respuesta Exitosa:

- **Código de Estado:** 200 OK
- **Cuerpo de Respuesta:**

 ```json
{
"id": 1,
"username": "nuevo_usuario",
"nombreTitular": "Nombre Completo",
"role": "USER"
}
  ```

## 3.2. Inicio de Sesión

**Endpoint:** `POST /api/auth/login`

**Descripción:** Permite a un usuario autenticarse y obtener un token JWT.

### Request Body:
 ```json

{
"username": "usuario_existente",
"password": "contraseña_correcta"
}
```

### Respuesta Exitosa:

- **Código de Estado:** 200 OK
- **Cuerpo de Respuesta:**

```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Respuesta de Error:

- **Código de Estado:** 401 Unauthorized
- **Cuerpo de Respuesta:*

```json
"Credenciales inválidas"
```

## 3.3. Endpoints de gestión de cuentas

### 3.3.1. Crear cuenta

**Endpoint:** `POST /api/cuentas`  
**Descripción:** Permite crear una nueva cuenta asociada al usuario autenticado.

#### Request Body:
```json
{
  "saldo": 1000.00,
  "moneda": "USD"
}
```

### Response:

- **200 OK**
```json
{
"id": 1,
"numeroCuenta": "NUMERO_AUTOGENERADO",
"titular": "string",
"saldo": 1000.00,
"moneda": "USD"
}
```

### 3.3.2. Actualizar cuenta

**Endpoint:** `PUT /api/cuentas/{id}`  
**Descripción:** Permite actualizar los detalles de una cuenta existente.

#### Request Body
```json
{
"saldo": 2000.00,
"moneda": "USD"
}
```

### Response:

- **200 OK**
```json
{
  "id": 1,
  "numeroCuenta": "NUMERO_AUTOGENERADO",
  "titular": "string",
  "saldo": 2000.00,
  "moneda": "USD"
}

```

### 3.3.3 Eliminar cuenta

**Endpoint:** `DELETE /api/cuentas/{id}`  
**Descripción:** Permite eliminar una cuenta existente.

### Response:
- **204 No Content**
- **403 Forbidden**
- 
## 4. Seguridad y Autenticación

La API está protegida mediante un sistema de autenticación y autorización basado en JSON Web Tokens (JWT). Este enfoque asegura que solo los usuarios autenticados y autorizados puedan acceder a los recursos de la API, como la creación, actualización y eliminación de cuentas.

### Implementación de JWT

- **Generación del Token:** Al iniciar sesión, el usuario proporciona sus credenciales (nombre de usuario y contraseña). Si las credenciales son válidas, se genera un JWT que incluye información relevante sobre el usuario (por ejemplo, el nombre de usuario). Este token se firma utilizando una clave secreta para garantizar su integridad.

- **Almacenamiento del Token:** El token generado se envía al cliente, que debe almacenarlo localmente (por ejemplo, en localStorage o en cookies) para utilizarlo en futuras solicitudes a la API.

- **Autenticación en las Solicitudes:** Para acceder a los endpoints protegidos, como la creación, actualización y eliminación de cuentas, el cliente debe incluir el JWT en el encabezado `Authorization` de la solicitud HTTP. El formato del encabezado es:

```bash
Authorization: Bearer <token>
```


- **Validación del Token:** En el lado del servidor, se interceptan las solicitudes entrantes mediante un filtro de seguridad que valida el JWT. Si el token es válido y no ha expirado, se permite el acceso al recurso solicitado. Si el token es inválido o ha expirado, se devuelve un `401 Unauthorized`.

- **Control de Acceso:** Adicionalmente, se implementan controles de acceso basados en el rol del usuario. Por ejemplo, en el endpoint de eliminación de cuentas, se verifica que el usuario que intenta eliminar una cuenta sea el titular de la misma. Si el usuario no es el titular, se devuelve un `403 Forbidden`.

### Manejo de Errores

En caso de errores durante el proceso de autenticación o autorización (por ejemplo, token inválido o cuenta no encontrada), se utilizan excepciones personalizadas y un controlador global de excepciones (`GlobalExceptionHandler`) para devolver respuestas apropiadas con mensajes claros para el cliente. Esto mejora la experiencia del usuario y facilita la depuración.


## 5. Auditoría y Logging

La aplicación implementa un sistema de auditoría y logging para rastrear las acciones realizadas en la API y proporcionar un registro detallado de las operaciones que afectan a las cuentas de usuario. Este enfoque ayuda en el diagnóstico de problemas, mejora la seguridad y facilita el cumplimiento normativo.

### 5.1 Sistema de Logging

#### 5.1.1 Uso de SLF4J
La aplicación utiliza **SLF4J (Simple Logging Facade for Java)** junto con una implementación de logging, como **Logback**, para manejar los logs. Esta elección permite una gestión flexible y eficiente de los mensajes de log, ajustando el nivel de detalle según las necesidades del entorno (desarrollo, prueba, producción).

#### 5.1.2 Niveles de Log
Se registran mensajes en diferentes niveles (info, error, warn) dependiendo de la gravedad del evento. Por ejemplo:

- **Info**: Se registran eventos importantes, como la creación y actualización de cuentas. Esto permite auditar las operaciones realizadas por los usuarios y tener un seguimiento de las acciones significativas.

- **Error**: Se registran errores y excepciones críticas que ocurren durante la ejecución de la aplicación, lo que facilita la identificación y solución de problemas.
#### 5.1.3 Registro de Eventos Clave
Cada acción relacionada con la gestión de cuentas se registra. Por ejemplo, en la clase **CuentaService**, se registra cuando se crea, actualiza o elimina una cuenta:

- **Creación de cuenta**: Se registra un mensaje indicando que se ha creado una nueva cuenta, incluyendo detalles relevantes como el ID de la cuenta y el saldo inicial.

- **Actualización de cuenta**: Se registra un mensaje cuando se actualizan los detalles de una cuenta, especificando qué campos han cambiado y sus nuevos valores.

- **Eliminación de cuenta**: Se registra un mensaje al eliminar una cuenta, incluyendo el ID de la cuenta que ha sido eliminada.

Este registro de eventos
```bash
logger.info("Creando nueva cuenta para el titular: {}", cuenta.getTitular());
logger.info("Cuenta creada exitosamente con ID: {}", cuentaGuardada.getId());
```

### 5.2 Configuración de Logging

La configuración de logging está definida en el archivo **logback.xml**, donde se han establecido dos appenders:

- **CONSOLE**: Este appender permite la visualización de logs en la consola con un patrón de salida bien estructurado, facilitando el monitoreo en tiempo real de las operaciones.

- **FILE**: Este appender registra los logs en archivos, utilizando un **RollingFileAppender** para archivar automáticamente los logs diarios y mantener un historial de hasta 30 días. Esto asegura que se conserve un registro histórico de las actividades de la API sin sobrecargar el sistema con archivos de log antiguos.







### 6. Testing

A pesar de que la prueba no requería la implementación de pruebas unitarias, considero fundamental garantizar la calidad y el correcto funcionamiento de nuestros servicios mediante la creación de pruebas automatizadas. Estas pruebas están diseñadas para verificar el comportamiento esperado de los servicios **CuentaService**, **TransaccionService** y **UsuarioService**. A continuación, se describen brevemente las pruebas realizadas:

### Ubicación de las Pruebas
**src/test/java/com/gestion/fintech/services**

### CuentaServiceTest
- **crearCuenta_shouldReturnCuenta**:
  - Verifica que al crear una cuenta, se retorne un objeto **Cuenta** no nulo y que contenga el titular correcto. También se asegura de que el método **save** del repositorio sea invocado una vez.

- **actualizarCuenta_shouldReturnUpdatedCuenta**:
  - Asegura que la cuenta se actualice correctamente y se devuelva el objeto actualizado, verificando que se haya llamado a **findById** y **save** en el repositorio.

- **eliminarCuenta_shouldNotThrowException**:
  - Comprueba que al eliminar una cuenta, no se lance ninguna excepción y que se invoque **deleteById** en el repositorio.

- **obtenerCuentaPorId_shouldReturnCuenta**:
  - Verifica que al obtener una cuenta por su ID, se retorne un objeto **Cuenta** no nulo y se llame al método **findById** del repositorio.

- **obtenerCuentaPorId_shouldThrowException_whenCuentaNotFound**:
  - Asegura que se lance una excepción cuando se intenta obtener una cuenta que no existe.

### TransaccionServiceTest
- **obtenerCuentaPorId_CuentaExistente_RetornaCuenta**:
  - Verifica que al obtener una cuenta existente, se retorne el objeto correcto y se invoque **findById**.

- **obtenerCuentaPorId_CuentaNoExistente_LanzaException**:
  - Comprueba que se lance una excepción si se intenta obtener una cuenta que no existe.

- **realizarDeposito_MontoValido_RetornaTransaccion**:
  - Asegura que al realizar un depósito con un monto válido, se retorne un objeto **Transaccion** y se actualice el saldo de la cuenta.

- **realizarDeposito_MontoInvalido_LanzaTransaccionException**:
  - Verifica que se lance una excepción si se intenta realizar un depósito con un monto negativo.

- **realizarRetiro_MontoValido_RetornaTransaccion**:
  - Comprueba que al realizar un retiro con un monto válido, se retorne un objeto **Transaccion** y se actualice el saldo de la cuenta.

- **realizarRetiro_SaldoInsuficiente_LanzaTransaccionException**:
  - Verifica que se lance una excepción si se intenta retirar un monto mayor al saldo disponible.

- **realizarTransferencia_MontoValido_RetornaTransaccion**:
  - Asegura que al realizar una transferencia con un monto válido, se retorne un objeto **Transaccion** y se actualicen los saldos de ambas cuentas.

- **realizarTransferencia_MonedasDistintas_LanzaTransaccionException**:
  - Comprueba que se lance una excepción si se intenta realizar una transferencia entre cuentas de diferentes monedas.

- **obtenerHistorial_RetornaTransacciones**:
  - Verifica que se retornen las transacciones correctas al solicitar el historial.

### UsuarioServiceTest
- **testRegistrarUsuario_Success**:
  - Verifica que al registrar un usuario nuevo, se retorne un objeto **Usuario** no nulo con el nombre de usuario correcto y la contraseña encriptada.

- **testRegistrarUsuario_UsuarioYaExiste**:
  - Asegura que se lance una excepción si se intenta registrar un usuario que ya existe.

- **testAutenticarUsuario_Success**:
  - Verifica que al autenticar un usuario con credenciales correctas, se retorne un objeto **Usuario** que coincida con el nombre de usuario.

- **testAutenticarUsuario_Fail**:
  - Asegura que no se retorne ningún usuario si las credenciales son incorrectas.

## 7. Escalabilidad

La API está preparada para manejar un alto volumen de solicitudes concurrentes gracias al uso de la anotación `@Async` y `CompletableFuture`. Esto permite que las operaciones críticas, como depósitos, retiros, transferencias, y la generación de reportes o historial de transacciones, se ejecuten de manera asíncrona, liberando el hilo principal y permitiendo que el sistema gestione múltiples solicitudes simultáneamente sin bloqueo.

La decisión de utilizar `@Async` junto con `CompletableFuture` garantiza que las tareas de larga duración no afecten el rendimiento general de la API, permitiendo la concurrencia controlada sin necesidad de complejos manejos de hilos manuales. Además, cada operación está diseñada para ser transaccional, lo que asegura la consistencia de los datos en escenarios concurrentes.

## 8. Rendimiento

Para optimizar el rendimiento en las consultas a la base de datos, se han implementado varias técnicas. Se utilizan índices en las columnas más consultadas de la tabla `Transaccion`, como `cuenta_origen_id`, `cuenta_destino_id`, `tipo` y `fecha`, lo cual mejora significativamente los tiempos de búsqueda y filtrado de datos. Estas son las definiciones de índices utilizadas:

```java
indexes = {
    @Index(name = "idx_cuenta_origen_id", columnList = "cuenta_origen_id"),
    @Index(name = "idx_cuenta_destino_id", columnList = "cuenta_destino_id"),
    @Index(name = "idx_tipo", columnList = "tipo"),
    @Index(name = "idx_fecha", columnList = "fecha")
}
```
Además, se utilizan consultas optimizadas, como la siguiente consulta personalizada para sumar los montos de transacciones según su tipo dentro de un rango de fechas:

```java
@Query("SELECT SUM(t.monto) FROM Transaccion t WHERE t.cuentaOrigenId = :cuentaId AND t.tipo = :tipo AND t.fecha BETWEEN :fechaDesde AND :fechaHasta")
BigDecimal sumarMontosPorTipo(@Param("cuentaId") Long cuentaId, @Param("tipo") String tipo, @Param("fechaDesde") LocalDateTime fechaDesde, @Param("fechaHasta") LocalDateTime fechaHasta);
```

Estas optimizaciones permiten que las operaciones como la generación de reportes financieros y el historial de transacciones se ejecuten de manera eficiente, incluso con grandes volúmenes de datos, reduciendo considerablemente la carga en el sistema y mejorando el tiempo de respuesta en consultas complejas.



---

## Enunciado
Estás postulando para una posición como desarrollador backend senior en una empresa FinTech que ofrece una plataforma para la gestión de cuentas financieras, transferencias y análisis en tiempo real. Tu tarea es desarrollar una API robusta y escalable que permita realizar operaciones financieras clave mientras aseguras la seguridad y el rendimiento de la aplicación.

### Requisitos Funcionales

#### Gestión de cuentas de usuario:
- Desarrolla endpoints REST para crear, actualizar y eliminar cuentas de usuario.
- Cada cuenta debe tener los siguientes atributos:
  - Nombre del titular
  - Número de cuenta (único, generado automáticamente)
  - Saldo actual
  - Moneda (USD, EUR, etc.)
- Valida que los saldos no puedan ser negativos.

#### Transacciones:
- Implementa endpoints para realizar:
  - Depósitos
  - Retiros
  - Transferencias entre cuentas
- Asegúrate de que las transacciones sean atómicas utilizando técnicas como control de transacciones en la base de datos para evitar inconsistencias.
- Registra cada transacción con detalles como el tipo, monto, fecha y cuentas involucradas.

#### Historial de transacciones:
- Crea un endpoint que permita obtener el historial de transacciones de una cuenta.
- Incluye filtros por fecha y tipo de transacción.

#### Reportes financieros:
- Implementa un endpoint para generar reportes financieros, mostrando el saldo inicial, movimientos (depósitos/retiros) y saldo final para un rango de fechas específico.

#### Seguridad:
- Utiliza JWT (JSON Web Tokens) para autenticación y autorización.
- Solo los usuarios autenticados deben poder acceder a sus cuentas y realizar operaciones financieras.
- Asegura que cada cuenta solo sea accesible por su titular.

### Requisitos No Funcionales

#### Escalabilidad:
- La API debe estar preparada para manejar un gran volumen de solicitudes concurrentes. Utiliza prácticas de programación concurrente en Java (ej., ExecutorService o CompletableFuture).

#### Rendimiento:
- Optimiza las consultas de base de datos para que los reportes y el historial de transacciones respondan de manera eficiente, incluso con grandes volúmenes de datos.

#### Persistencia:
- Utiliza Spring Data JPA o Hibernate para gestionar la persistencia de datos con una base de datos relacional como PostgreSQL.
- Las transacciones deben ser consistentes y utilizar manejo adecuado de commit y rollback.

#### Tolerancia a fallos:
- Implementa manejo de excepciones robusto para garantizar que las transacciones financieras no se pierdan o generen inconsistencias en caso de errores.

#### Auditoría y Logging:
- Implementa logs detallados para registrar todas las transacciones, accesos y cambios en las cuentas, que puedan ser usados para auditorías futuras.
- Usa una librería como SLF4J con Logback para gestionar los logs.

### Stack Tecnológico Obligatorio:
- Lenguaje: Java 11 o superior
- Framework: Spring Boot
- Persistencia: Spring Data JPA o Hibernate con PostgreSQL
- Seguridad: Spring Security con JWT para autenticación
- Testing: JUnit, Mockito para pruebas unitarias y de integración




---

