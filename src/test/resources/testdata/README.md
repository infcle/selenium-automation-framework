# Datos de Prueba

Este directorio contiene los archivos de datos de prueba utilizados por el framework de automatización.

## Archivos incluidos

### 1. test-scenarios.json

Archivo JSON que contiene:

- Escenarios de prueba detallados
- Datos de prueba para cada escenario
- Configuraciones de entornos
- Configuraciones de testing

### 2. users_data.txt

Archivo de texto con datos de usuarios en formato CSV que debe convertirse a Excel.

### 3. users.xlsx (CREAR MANUALMENTE)

Archivo Excel con datos de usuarios. Para crearlo:

1. Abrir Microsoft Excel o Google Sheets
2. Copiar el contenido de `users_data.txt`
3. Pegar en la primera hoja
4. Renombrar la hoja como "Users"
5. Guardar como `users.xlsx` en este directorio

### Estructura del archivo users.xlsx

| Columna     | Descripción             | Ejemplo            |
| ----------- | ----------------------- | ------------------ |
| Username    | Nombre de usuario único | admin@kernotec.com |
| Password    | Contraseña del usuario  | Admin123!          |
| Role        | Rol del usuario         | admin              |
| DisplayName | Nombre para mostrar     | Administrator      |
| Email       | Email del usuario       | admin@kernotec.com |
| Department  | Departamento            | IT                 |
| IsActive    | Usuario activo          | true               |

## Roles de usuario disponibles

- **admin**: Administrador con todos los permisos
- **testuser**: Usuario de prueba con permisos de lectura y escritura
- **readonly**: Usuario solo lectura
- **manager**: Manager con permisos de gestión
- **user**: Usuario regular
- **developer**: Desarrollador
- **analyst**: Analista
- **support**: Usuario de soporte

## Uso en tests

Los datos se pueden acceder usando:

```java
// Desde configuración YAML
String username = ConfigReader.getUsername("admin");
String password = ConfigReader.getPassword("admin");

// Desde Excel
List<Map<String, String>> users = ExcelUtils.readExcelData("users.xlsx", "Users");

// Desde UserCredentials
UserCredentials.CredentialData creds = UserCredentials.getCredentials(UserCredentials.UserRole.ADMIN);
```

## Seguridad

⚠️ **IMPORTANTE**: Este archivo contiene credenciales de prueba. En un entorno de producción:

- No incluir credenciales reales
- Usar variables de entorno
- Implementar encriptación
- Mantener el archivo fuera del control de versiones si es necesario
