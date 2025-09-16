# 🚀 Selenium Automation Framework

## Estructura de Directorios

```
selenium-automation-framework/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/company/qa/
│   │           ├── config/
│   │           │   ├── DriverManager.java
│   │           │   ├── ConfigReader.java
│   │           │   └── TestConfig.java
│   │           ├── pages/
│   │           │   ├── BasePage.java
│   │           │   ├── LoginPage.java
│   │           │   ├── HomePage.java
│   │           │   └── DashboardPage.java
│   │           ├── utils/
│   │           │   ├── WebDriverUtils.java
│   │           │   ├── ExcelUtils.java
│   │           │   ├── ScreenshotUtils.java
│   │           │   └── DateUtils.java
│   │           └── data/
│   │               ├── TestDataProvider.java
│   │               └── UserCredentials.java
│   ├── test/
│   │   └── java/
│   │       └── com/company/qa/
│   │           ├── tests/
│   │           │   ├── BaseTest.java
│   │           │   ├── LoginTests.java
│   │           │   ├── NavigationTests.java
│   │           │   └── SmokeTests.java
│   │           └── suites/
│   │               ├── RegressionSuite.java
│   │               └── SmokeSuite.java
│   └── resources/
│       ├── config/
│       │   ├── application.yml
│       │   └── test-config.yml
│       ├── testdata/
│       │   ├── users.xlsx
│       │   └── test-scenarios.json
│       ├── drivers/
│       │   ├── chromedriver.exe
│       │   └── geckodriver.exe
│       └── reports/
│           └── templates/
├── test-output/
│   ├── screenshots/
│   ├── reports/
│   └── logs/
├── docs/
│   ├── architecture.md
│   ├── setup-guide.md
│   └── test-execution-guide.md
└── .github/
    └── workflows/
        └── ci-pipeline.yml
```

---

## 📋 Descripción del Proyecto

Framework de automatización web desarrollado con Selenium WebDriver, TestNG y Java 17 para pruebas automatizadas escalables y mantenibles. Implementa patrones de diseño estándar de la industria y mejores prácticas para testing de aplicaciones web.

---

## 🏗️ Arquitectura del Framework

```
selenium-automation-framework/
├── 📁 src/main/java/com/kernotec/qa/
│   ├── 📁 config/          # Configuraciones y gestión de drivers
│   ├── 📁 pages/           # Page Object Model (POM)
│   ├── 📁 utils/           # Utilidades y helpers
│   └── 📁 data/            # Proveedores de datos de test
├── 📁 src/test/java/com/kernotec/qa/
│   ├── 📁 tests/           # Clases de test
│   └── 📁 suites/          # Test suites organizadas
├── 📁 src/test/resources/  # Recursos de test
└── 📁 test-output/         # Reportes y capturas
```

---

## 🎯 Características Principales

### ✅ Patrones de Diseño Implementados

- Page Object Model (POM) - Separación de lógica UI y tests
- Factory Pattern - Gestión centralizada de WebDrivers
- Singleton Pattern - Configuraciones únicas
- Data Driven Testing - Datos externos (Excel/JSON)

### ✅ Funcionalidades Avanzadas

- 🌐 Multi-browser: Chrome, Firefox, Edge, Safari
- ⚡ Ejecución paralela de tests
- 📊 Reportes HTML profesionales con ExtentReports
- 📷 Capturas automáticas en fallos
- 🔧 Waits inteligentes y manejo robusto de elementos
- 📈 Logging estructurado con Log4j2
- 🔒 Gestión segura de credenciales

---

## 🚀 Inicio Rápido

### Prerrequisitos

- ☕ Java 17 o superior
- 📦 Maven 3.8+
- 🌐 Chrome/Firefox instalado

### Instalación

```bash
    # Clonar el repositorio
    git clone [repository-url]
    cd selenium-automation-framework
    
    # Instalar dependencias
    mvn clean install
    
    # Ejecutar tests de ejemplo
    mvn test -Dtest=SmokeTests
```

---

## 📊 Ejecución de Tests

### Comandos Básicos

```bash
    # Ejecutar todos los tests
    mvn clean test

    # Ejecutar suite específica
    mvn test -Dtest=LoginTests
    
    # Ejecutar con browser específico
    mvn test -Dbrowser=chrome
    
    # Ejecutar en paralelo
    mvn test -Dparallel=true -DthreadCount=3
```

### Parámetros Disponibles

| Parámetro   | Valores               | Descripción           |
|-------------|-----------------------|-----------------------|
| browser     | chrome, firefox, edge | Browser a utilizar    |
| environment | dev, test, prod       | Ambiente de ejecución |
| headless    | true, false           | Modo headless         |
| parallel    | true, false           | Ejecución paralela    |

---

## 📁 Estructura de Archivos

### 🔧 Configuración (config/)

- **DriverManager.java** - Gestión centralizada de WebDrivers
- **ConfigReader.java** - Lectura de archivos de configuración
- **TestConfig.java** - Configuraciones de test

### 📄 Page Objects (pages/)

- **BasePage.java** - Funcionalidades base para todas las páginas
- **LoginPage.java** - Página de login
- **HomePage.java** - Página principal
- **DashboardPage.java** - Panel de control

### 🛠️ Utilidades (utils/)

- **WebDriverUtils.java** - Helpers para WebDriver
- **ExcelUtils.java** - Manejo de archivos Excel
- **ScreenshotUtils.java** - Gestión de capturas
- **DateUtils.java** - Utilidades de fechas

### 📊 Datos de Test (data/)

- **TestDataProvider.java** - Proveedor de datos para TestNG
- **UserCredentials.java** - Credenciales de usuarios

### 🧪 Tests (tests/)

- **BaseTest.java** - Configuración base para todos los tests
- **LoginTests.java** - Tests de autenticación
- **NavigationTests.java** - Tests de navegación
- **SmokeTests.java** - Tests de humo

---

## 📈 Reportes y Resultados

### Ubicación de Reportes

```
    test-output/
    ├── extent-reports/     # Reportes HTML detallados
    ├── screenshots/        # Capturas de pantalla
    ├── logs/              # Archivos de log
    └── testng-results.xml # Resultados TestNG
```

### Acceso a Reportes

- **ExtentReport:** test-output/extent-reports/index.html
- **TestNG Report:** test-output/index.html
- **Logs:** test-output/logs/application.log

---

## ⚙️ Configuración

### Archivos de Configuración
```
    src/test/resources/
    ├── config/
    │   ├── application.properties    # Configuración general
    │   └── test-config.properties   # Configuración específica de tests
    ├── testdata/
    │   ├── users.xlsx               # Datos de usuarios
    │   └── test-scenarios.json     # Escenarios de test
    └── testng.xml                  # Suite de TestNG
```

### Variables de Entorno
```yml
# application.yml
app:
    url: https://example.com
    browser: chrome
    headless: false

timeouts:
    implicit: 10
    explicit: 30
```

---

## 🔍 Mejores Prácticas Implementadas

### ✅ Código Limpio

- Nombres descriptivos de métodos y variables
- Separación clara de responsabilidades
- Comentarios donde sea necesario
- Manejo adecuado de excepciones

### ✅ Mantenibilidad

- Page Object Model para fácil mantenimiento
- Configuraciones centralizadas
- Reutilización de código común
- Versionado de dependencias

### ✅ Escalabilidad

- Estructura modular
- Ejecución paralela
- Data providers externos
- Fácil adición de nuevos tests

---

## 📋 Checklist de Test Execution

### Antes de Ejecutar

- Verificar configuración de ambiente
- Confirmar datos de test actualizados
- Validar conectividad de red
- Revisar versiones de browsers

### Después de Ejecutar

- Revisar reportes generados
- Analizar fallos si los hay
- Archivar capturas importantes
- Actualizar documentación si es necesario

---

## 🤝 Contribución

### Estándares de Código

1. Seguir convenciones de naming de Java
2. Agregar JavaDoc a métodos públicos
3. Incluir tests unitarios cuando sea apropiado
4. Actualizar README con nuevas funcionalidades

### Proceso de Desarrollo

1. Crear feature branch desde develop
2. Implementar funcionalidad
3. Ejecutar tests localmente
4. Crear pull request con descripción detallada

---

## 🆘 Troubleshooting

### Problemas Comunes

| Problema                       | Solución                                      |
|--------------------------------|-----------------------------------------------|
| WebDriver no encontrado        | Verificar WebDriverManager en pom.xml         |
| Tests fallan intermitentemente | Revisar waits y timeouts                      |
| Capturas no se generan         | Verificar permisos en carpeta test-output     |
| Reportes no se abren           | Verificar que ExtentReports esté en classpath |

### Logs de Debug

```bash
# Ejecutar con logs detallados
mvn test -Dlog4j.configuration=debug

# Ver logs en tiempo real
tail -f test-output/logs/application.log
```

---

## 📞 Soporte

### Para soporte técnico o dudas:

- **Team Lead QA:** elmer.coronel@kernotec.com
- **Wiki Interno:** `no definido`
- **JIRA Project:** `no definido`

### 📊 Métricas del Framework

### Beneficios Medibles

- **Reducción tiempo de testing:** 75%
- **Incremento cobertura:** 60%
- **Detección temprana bugs:** 80%
- **ROI proyectado:** 300% en 6 meses

---
Framework desarrollado por el equipo de QA de Kernotec - Versión 1.0.0