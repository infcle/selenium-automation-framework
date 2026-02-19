# Selenium Automation Framework

Framework base de automatizacion UI con Selenium + TestNG + Java 17.

## Estructura

```text
selenium-automation-framework/
|-- pom.xml
|-- src/
|   |-- main/
|   |   |-- java/com/kernotec/qa/
|   |   |   |-- config/        # Driver y lectura de configuracion
|   |   |   |-- pages/         # Page Objects
|   |   |   |-- utils/         # Utilidades comunes
|   |   |   `-- data/          # Datos/helpers de test
|   |-- test/
|   |   |-- java/com/kernotec/qa/tests/
|   |   |   |-- BaseTest.java
|   |   |   `-- SmokeTests.java
|   |   `-- resources/config/
|   |       |-- application.yml
|   |       `-- test-config.yml
|-- docs/
`-- test-output/               # Reportes, logs, screenshots
```

## Requisitos

- Java 17+
- Maven 3.8+
- Chrome/Firefox/Edge instalado

## Ejecutar

```bash
mvn clean test
```

Con parametros:

```bash
mvn test -Dbrowser=chrome -Dheadless=true -Denvironment=test
```

## Configuracion

Archivo principal: `src/test/resources/config/application.yml`

Overrides locales: `src/test/resources/config/test-config.yml`

Claves basicas:

- `testing.environment`
- `browser.default`
- `browser.headless`
- `timeouts.*`
- `screenshots.*`

## Siguiente paso recomendado

1. Implementar `LoginTests` y `NavigationTests` con Page Objects reales de tu aplicacion.
2. Agregar `testng.xml` por suites (smoke, regression) cuando ya tengas casos estables.
