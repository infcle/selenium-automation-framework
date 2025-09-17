# 🧪 Guía de Ejecución de Tests

## Tipos de Ejecución

### 1. Ejecución Local

Ejecutar tests en tu máquina local para desarrollo y debugging.

### 2. Ejecución en CI/CD

Ejecutar tests automáticamente en pipelines de integración continua.

### 3. Ejecución Paralela

Ejecutar múltiples tests simultáneamente para reducir tiempo de ejecución.

## Comandos de Ejecución

### Comandos Básicos

#### Ejecutar Todos los Tests

```bash
mvn clean test
```

#### Ejecutar Suite Específica

```bash
# Smoke Tests
mvn test -Dtest=SmokeTests

# Login Tests
mvn test -Dtest=LoginTests

# Navigation Tests
mvn test -Dtest=NavigationTests
```

#### Ejecutar Test Específico

```bash
mvn test -Dtest=LoginTests#testValidLogin
```

#### Ejecutar por Paquete

```bash
mvn test -Dtest="com.kernotec.qa.tests.*"
```

### Parámetros de Ejecución

#### Browser

```bash
# Chrome (por defecto)
mvn test -Dbrowser=chrome

# Firefox
mvn test -Dbrowser=firefox

# Edge
mvn test -Dbrowser=edge
```

#### Ambiente

```bash
# Development
mvn test -Denvironment=dev

# Testing
mvn test -Denvironment=test

# Staging
mvn test -Denvironment=staging
```

#### Modo Headless

```bash
# Con interfaz gráfica
mvn test -Dheadless=false

# Sin interfaz gráfica (más rápido)
mvn test -Dheadless=true
```

#### Ejecución Paralela

```bash
# Ejecutar en paralelo con 3 hilos
mvn test -Dparallel=true -DthreadCount=3

# Ejecutar en paralelo con 5 hilos
mvn test -Dparallel=true -DthreadCount=5
```

### Combinaciones de Parámetros

```bash
# Ejecutar smoke tests en Firefox, modo headless, ambiente de test
mvn test -Dtest=SmokeTests -Dbrowser=firefox -Dheadless=true -Denvironment=test

# Ejecutar login tests en paralelo con 4 hilos
mvn test -Dtest=LoginTests -Dparallel=true -DthreadCount=4
```

## Configuración de TestNG

### Archivo testng.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<suite name="Kernotec Test Suite" parallel="methods" thread-count="3">

    <!-- Smoke Tests -->
    <test name="Smoke Tests" preserve-order="true">
        <classes>
            <class name="com.kernotec.qa.tests.SmokeTests"/>
        </classes>
    </test>

    <!-- Login Tests -->
    <test name="Login Tests" preserve-order="false">
        <classes>
            <class name="com.kernotec.qa.tests.LoginTests"/>
        </classes>
    </test>

    <!-- Navigation Tests -->
    <test name="Navigation Tests" preserve-order="false">
        <classes>
            <class name="com.kernotec.qa.tests.NavigationTests"/>
        </classes>
    </test>

</suite>
```

### Ejecutar con TestNG XML

```bash
mvn test -DsuiteXmlFile=src/test/resources/testng.xml
```

## Ejecución de Suites

### Smoke Suite

```bash
# Ejecutar solo tests críticos
mvn test -Dtest=SmokeSuite
```

### Regression Suite

```bash
# Ejecutar todos los tests
mvn test -Dtest=RegressionSuite
```

### Suite Personalizada

```bash
# Ejecutar suite específica
mvn test -DsuiteXmlFile=src/test/resources/custom-suite.xml
```

## Configuración de Reportes

### ExtentReports

```bash
# Generar reportes ExtentReports
mvn test -DextentReports=true
```

### Screenshots

```bash
# Habilitar capturas en fallos
mvn test -Dscreenshots=true

# Habilitar capturas en éxito y fallo
mvn test -Dscreenshots=true -DscreenshotsOnSuccess=true
```

### Logs

```bash
# Logs detallados
mvn test -DlogLevel=DEBUG

# Logs mínimos
mvn test -DlogLevel=ERROR
```

## Ejecución en Diferentes Entornos

### Desarrollo Local

```bash
mvn test -Denvironment=dev -Dheadless=false -DlogLevel=DEBUG
```

### Ambiente de Testing

```bash
mvn test -Denvironment=test -Dheadless=true -Dparallel=true -DthreadCount=3
```

### Ambiente de Staging

```bash
mvn test -Denvironment=staging -Dheadless=true -Dparallel=true -DthreadCount=5
```

## Ejecución de Data-Driven Tests

### Tests con Excel

```bash
# Ejecutar tests que usan datos de Excel
mvn test -Dtest=ExcelDataTests
```

### Tests con JSON

```bash
# Ejecutar tests que usan datos de JSON
mvn test -Dtest=JsonDataTests
```

### Tests con Data Providers

```bash
# Ejecutar tests con múltiples conjuntos de datos
mvn test -Dtest=DataProviderTests
```

## Ejecución de Cross-Browser Tests

### Todos los Browsers

```bash
# Ejecutar en Chrome
mvn test -Dbrowser=chrome

# Ejecutar en Firefox
mvn test -Dbrowser=firefox

# Ejecutar en Edge
mvn test -Dbrowser=edge
```

### Script para Múltiples Browsers

```bash
#!/bin/bash
browsers=("chrome" "firefox" "edge")

for browser in "${browsers[@]}"; do
    echo "Ejecutando tests en $browser..."
    mvn test -Dbrowser=$browser -Dheadless=true
    if [ $? -ne 0 ]; then
        echo "Tests fallaron en $browser"
        exit 1
    fi
done
echo "Todos los tests pasaron en todos los browsers"
```

## Ejecución de Tests de Performance

### Tests con Timeouts Largos

```bash
mvn test -Dtest=PerformanceTests -Dtimeout=300
```

### Tests de Carga

```bash
mvn test -Dtest=LoadTests -Dparallel=true -DthreadCount=10
```

## Monitoreo y Debugging

### Ejecución con Debug

```bash
# Ejecutar con información de debug
mvn test -Ddebug=true -DlogLevel=DEBUG
```

### Ejecución Lenta (para debugging)

```bash
# Ejecutar con delays para observar
mvn test -DslowMode=true -Dheadless=false
```

### Ejecución con Screenshots en Cada Paso

```bash
mvn test -DscreenshotOnEveryStep=true
```

## Gestión de Resultados

### Limpiar Resultados Previos

```bash
# Limpiar reportes y screenshots previos
mvn clean test
```

### Conservar Resultados

```bash
# Ejecutar sin limpiar resultados previos
mvn test -DcleanResults=false
```

### Archivar Resultados

```bash
# Archivar resultados con timestamp
mvn test -DarchiveResults=true
```

## Ejecución en CI/CD

### GitHub Actions

```yaml
name: Test Execution
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        browser: [chrome, firefox, edge]
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: "17"
      - name: Run tests
        run: mvn test -Dbrowser=${{ matrix.browser }} -Dheadless=true
      - name: Upload reports
        uses: actions/upload-artifact@v3
        with:
          name: test-reports-${{ matrix.browser }}
          path: test-output/
```

### Jenkins Pipeline

```groovy
pipeline {
    agent any
    stages {
        stage('Smoke Tests') {
            steps {
                sh 'mvn test -Dtest=SmokeTests -Dheadless=true'
            }
        }
        stage('Regression Tests') {
            parallel {
                stage('Chrome') {
                    steps {
                        sh 'mvn test -Dbrowser=chrome -Dheadless=true'
                    }
                }
                stage('Firefox') {
                    steps {
                        sh 'mvn test -Dbrowser=firefox -Dheadless=true'
                    }
                }
                stage('Edge') {
                    steps {
                        sh 'mvn test -Dbrowser=edge -Dheadless=true'
                    }
                }
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
            archiveArtifacts artifacts: 'test-output/**/*', fingerprint: true
        }
    }
}
```

## Troubleshooting de Ejecución

### Problemas Comunes

#### Tests Fallan Intermitentemente

```bash
# Ejecutar con retry
mvn test -DretryCount=3

# Ejecutar con timeouts más largos
mvn test -Dtimeout=60
```

#### Tests Lentos

```bash
# Ejecutar en paralelo
mvn test -Dparallel=true -DthreadCount=4

# Ejecutar en modo headless
mvn test -Dheadless=true
```

#### Problemas de Memoria

```bash
# Aumentar memoria JVM
mvn test -Dmaven.test.jvmargs="-Xmx2g -XX:MaxPermSize=512m"
```

### Logs de Ejecución

```bash
# Ver logs en tiempo real
mvn test -DlogLevel=DEBUG | tee execution.log

# Guardar logs en archivo
mvn test > execution.log 2>&1
```

## Métricas de Ejecución

### Tiempo de Ejecución

```bash
# Ejecutar con timing
time mvn test

# Ejecutar con profiling
mvn test -Dprofile=true
```

### Estadísticas de Tests

```bash
# Generar estadísticas
mvn test -DgenerateStats=true
```

## Automatización Avanzada

### Script de Ejecución Diaria

```bash
#!/bin/bash
# daily-test-run.sh

echo "Iniciando ejecución diaria de tests..."

# Smoke tests
echo "Ejecutando smoke tests..."
mvn test -Dtest=SmokeTests -Dheadless=true
if [ $? -ne 0 ]; then
    echo "Smoke tests fallaron - deteniendo ejecución"
    exit 1
fi

# Regression tests
echo "Ejecutando regression tests..."
mvn test -Dtest=RegressionTests -Dheadless=true -Dparallel=true -DthreadCount=3

# Generar reporte consolidado
echo "Generando reporte consolidado..."
mvn test -DgenerateConsolidatedReport=true

echo "Ejecución diaria completada"
```

### Ejecución Programada

```bash
# Agregar a crontab para ejecución diaria a las 2 AM
0 2 * * * /path/to/daily-test-run.sh
```

---

_Guía desarrollada por el equipo de QA de Kernotec - Versión 1.0.0_
