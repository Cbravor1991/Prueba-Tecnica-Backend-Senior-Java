# Prueba Técnica: Desarrollador Backend Senior - FinTech (Java)

## Descripción General
Estás postulando para una posición como desarrollador backend senior en una empresa FinTech que ofrece una plataforma para la gestión de cuentas financieras, transferencias y análisis en tiempo real. Tu tarea es desarrollar una API robusta y escalable que permita realizar operaciones financieras clave mientras aseguras la seguridad y el rendimiento de la aplicación.

## Requisitos Funcionales

### Gestión de cuentas de usuario:
- Desarrolla endpoints REST para crear, actualizar y eliminar cuentas de usuario.
- Cada cuenta debe tener los siguientes atributos:
    - Nombre del titular
    - Número de cuenta (único, generado automáticamente)
    - Saldo actual
    - Moneda (USD, EUR, etc.)
- Valida que los saldos no puedan ser negativos.

### Transacciones:
- Implementa endpoints para realizar:
    - Depósitos
    - Retiros
    - Transferencias entre cuentas
- Asegúrate de que las transacciones sean atómicas utilizando técnicas como control de transacciones en la base de datos para evitar inconsistencias.
- Registra cada transacción con detalles como el tipo, monto, fecha y cuentas involucradas.

### Historial de transacciones:
- Crea un endpoint que permita obtener el historial de transacciones de una cuenta.
- Incluye filtros por fecha y tipo de transacción.

### Reportes financieros:
- Implementa un endpoint para generar reportes financieros, mostrando el saldo inicial, movimientos (depósitos/retiros) y saldo final para un rango de fechas específico.

### Seguridad:
- Utiliza JWT (JSON Web Tokens) para autenticación y autorización.
- Solo los usuarios autenticados deben poder acceder a sus cuentas y realizar operaciones financieras.
- Asegura que cada cuenta solo sea accesible por su titular.

## Requisitos No Funcionales

### Escalabilidad:
- La API debe estar preparada para manejar un gran volumen de solicitudes concurrentes. Utiliza prácticas de programación concurrente en Java (ej., ExecutorService o CompletableFuture).

### Rendimiento:
- Optimiza las consultas de base de datos para que los reportes y el historial de transacciones respondan de manera eficiente, incluso con grandes volúmenes de datos.

### Persistencia:
- Utiliza Spring Data JPA o Hibernate para gestionar la persistencia de datos con una base de datos relacional como PostgreSQL.
- Las transacciones deben ser consistentes y utilizar manejo adecuado de commit y rollback.

### Tolerancia a fallos:
- Implementa manejo de excepciones robusto para garantizar que las transacciones financieras no se pierdan o generen inconsistencias en caso de errores.

### Auditoría y Logging:
- Implementa logs detallados para registrar todas las transacciones, accesos y cambios en las cuentas, que puedan ser usados para auditorías futuras.
- Usa una librería como SLF4J con Logback para gestionar los logs.

## Stack Tecnológico Obligatorio:
- Lenguaje: Java 11 o superior
- Framework: Spring Boot
- Persistencia: Spring Data JPA o Hibernate con PostgreSQL
- Seguridad: Spring Security con JWT para autenticación
- Testing: JUnit, Mockito para pruebas unitarias y de integración

## Requisitos Adicionales

### Optimización de Recursos:
- Utiliza mecanismos de cacheo (ej., Spring Cache, EhCache) para mejorar la respuesta en consultas que se repiten, como el historial de transacciones.

###
