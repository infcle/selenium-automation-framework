# 🚀 Guía de Configuración del Framework

## Requisitos del Sistema

### Software Requerido

- **Java**: JDK 17 o superior
- **Maven**: 3.8+
- **Git**: Para clonación del repositorio
- **IDE**: IntelliJ IDEA, Eclipse, o VS Code

### Navegadores Soportados

- **Chrome**: Versión 90+
- **Firefox**: Versión 88+
- **Edge**: Versión 90+

### Sistema Operativo

- Windows 10/11
- macOS 10.15+
- Linux Ubuntu 18.04+

## Instalación Paso a Paso

### 1. Clonar el Repositorio

```bash
git clone https://github.com/kernotec/selenium-automation-framework.git
cd selenium-automation-framework
```

### 2. Verificar Java

```bash
java -version
# Debe mostrar Java 17 o superior
```

### 3. Verificar Maven

```bash
mvn -version
# Debe mostrar Maven 3.8 o superior
```

### 4. Instalar Dependencias

```bash
mvn clean install
```

### 5. Configurar Variables de Entorno (Opcional)

```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%PATH%;%JAVA_HOME%\bin

# macOS/Linux
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export PATH=$PATH:$JAVA_HOME/bin
```

## Configuración del Proyecto

### 1. Configuración de IDE

#### IntelliJ IDEA

1. Abrir el proyecto
2. Configurar SDK: File → Project Structure → Project → Project SDK
3. Configurar Maven: File → Settings → Build → Build Tools → Maven
4. Importar dependencias automáticamente

#### Eclipse

1. Importar como proyecto Maven existente
2. Configurar JDK: Project → Properties → Java Build Path
3. Actualizar proyecto: Alt+F5

#### VS Code

1. Instalar extensiones:
   - Extension Pack for Java
   - Maven for Java
   - Test Runner for Java
2. Abrir carpeta del proyecto
3. Configurar Java Home en settings.json

### 2. Configuración de Navegadores

#### Chrome

- Instalar Chrome normalmente
- WebDriverManager manejará automáticamente el driver

#### Firefox

- Instalar Firefox normalmente
- WebDriverManager manejará automáticamente el driver

#### Edge

- Instalar Edge normalmente
- WebDriverManager manejará automáticamente el driver

### 3. Configuración de Archivos

#### application.yml

```yaml
# Ubicación: src/main/resources/config/application.yml
environments:
  dev:
    baseUrl: "https://dev.kernotec.com"
  test:
    baseUrl: "https://test.kernotec.com"

browser:
  default: "chrome"
  headless: false

timeouts:
  implicit: 10
  explicit: 30
  pageLoad: 60
```

#### test-config.yml

```yaml
# Ubicación: src/main/resources/config/test-config.yml
test:
  environment: "test"
  baseUrl: "https://test.kernotec.com"

browser:
  default: "chrome"
  headless: true
```

## Configuración de Datos de Prueba

### 1. Crear Archivo Excel de Usuarios

1. Abrir Microsoft Excel o Google Sheets
2. Copiar contenido de `src/test/resources/testdata/users_data.txt`
3. Pegar en la primera hoja
4. Renombrar hoja como "Users"
5. Guardar como `users.xlsx` en `src/test/resources/testdata/`

### 2. Configurar Credenciales

Editar `src/main/resources/config/application.yml`:

```yaml
users:
  admin:
    username: "admin@kernotec.com"
    password: "Admin123!"
  testuser:
    username: "testuser@kernotec.com"
    password: "Test123!"
```

## Configuración de Logging

### 1. Configuración de Log4j2

El archivo `src/main/resources/log4j2.xml` ya está configurado con:

- Logs en consola
- Logs en archivos
- Rotación automática
- Niveles configurables

### 2. Niveles de Log

```xml
<!-- Cambiar nivel en log4j2.xml -->
<Logger name="com.kernotec.qa" level="DEBUG" additivity="false">
```

## Configuración de Reportes

### 1. ExtentReports

```yaml
# En application.yml
reporting:
  extentReports:
    enabled: true
    path: "test-output/extent-reports/"
    title: "Kernotec Automation Report"
```

### 2. Screenshots

```yaml
reporting:
  screenshots:
    enabled: true
    path: "test-output/screenshots/"
    onFailure: true
    onSuccess: false
```

## Configuración de Ejecución

### 1. Ejecución Local

```bash
# Ejecutar todos los tests
mvn clean test

# Ejecutar suite específica
mvn test -Dtest=SmokeTests

# Ejecutar con browser específico
mvn test -Dbrowser=chrome

# Ejecutar en modo headless
mvn test -Dheadless=true
```

### 2. Parámetros de Ejecución

| Parámetro   | Valores               | Descripción               |
| ----------- | --------------------- | ------------------------- |
| browser     | chrome, firefox, edge | Browser a utilizar        |
| environment | dev, test, staging    | Ambiente de ejecución     |
| headless    | true, false           | Modo headless             |
| parallel    | true, false           | Ejecución paralela        |
| threadCount | 1-10                  | Número de hilos paralelos |

### 3. Configuración de TestNG

```xml
<!-- src/test/resources/testng.xml -->
<suite name="Kernotec Test Suite" parallel="methods" thread-count="3">
    <test name="Smoke Tests">
        <classes>
            <class name="com.kernotec.qa.tests.SmokeTests"/>
        </classes>
    </test>
</suite>
```

## Configuración de CI/CD

### 1. GitHub Actions

```yaml
# .github/workflows/ci-pipeline.yml
name: CI Pipeline
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: "17"
      - name: Run tests
        run: mvn clean test
```

### 2. Jenkins

```groovy
pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
        }
    }
    post {
        always {
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'test-output/extent-reports',
                reportFiles: 'index.html',
                reportName: 'Test Report'
            ])
        }
    }
}
```

## Troubleshooting

### Problemas Comunes

#### 1. Error de WebDriver

```
Error: WebDriver not found
```

**Solución**: Verificar que WebDriverManager esté en pom.xml y que el navegador esté instalado.

#### 2. Error de Configuración

```
Error: Configuration not found
```

**Solución**: Verificar que application.yml esté en src/main/resources/config/

#### 3. Error de Dependencias

```
Error: ClassNotFoundException
```

**Solución**: Ejecutar `mvn clean install` para descargar dependencias.

#### 4. Error de Permisos

```
Error: Permission denied
```

**Solución**: Verificar permisos en carpeta test-output/

### Logs de Debug

```bash
# Ejecutar con logs detallados
mvn test -Dlog4j.configuration=debug

# Ver logs en tiempo real
tail -f test-output/logs/application.log
```

### Verificación de Configuración

```bash
# Verificar configuración
mvn test -Dtest=ConfigValidationTest

# Verificar drivers
mvn test -Dtest=DriverValidationTest
```

## Configuración Avanzada

### 1. Configuración de Proxy

```yaml
# En application.yml
proxy:
  enabled: true
  host: "proxy.company.com"
  port: 8080
  username: "user"
  password: "pass"
```

### 2. Configuración de Base de Datos

```yaml
database:
  enabled: true
  type: "mysql"
  host: "localhost"
  port: 3306
  name: "testdb"
  username: "testuser"
  password: "testpass"
```

### 3. Configuración de Email

```yaml
email:
  enabled: true
  smtp:
    host: "smtp.gmail.com"
    port: 587
    username: "automation@kernotec.com"
    password: "password"
  recipients:
    - "qa-team@kernotec.com"
```

## Validación de Configuración

### 1. Script de Validación

```bash
#!/bin/bash
echo "Validando configuración del framework..."

# Verificar Java
java -version || { echo "Java no encontrado"; exit 1; }

# Verificar Maven
mvn -version || { echo "Maven no encontrado"; exit 1; }

# Verificar archivos de configuración
[ -f "src/main/resources/config/application.yml" ] || { echo "application.yml no encontrado"; exit 1; }

# Verificar dependencias
mvn dependency:resolve || { echo "Error en dependencias"; exit 1; }

echo "Configuración validada exitosamente!"
```

### 2. Test de Configuración

```java
@Test
public void testConfiguration() {
    // Verificar configuración básica
    assertNotNull(ConfigReader.getBaseUrl());
    assertNotNull(ConfigReader.getDefaultBrowser());

    // Verificar drivers
    WebDriver driver = DriverManager.getDriver();
    assertNotNull(driver);

    // Limpiar
    DriverManager.quitDriver();
}
```

## Próximos Pasos

1. **Ejecutar Smoke Tests**: Verificar que todo funciona
2. **Configurar CI/CD**: Integrar con pipeline
3. **Agregar Tests**: Desarrollar nuevos casos de prueba
4. **Configurar Reportes**: Personalizar reportes
5. **Optimizar Performance**: Configurar paralelismo

---

_Guía desarrollada por el equipo de QA de Kernotec - Versión 1.0.0_
