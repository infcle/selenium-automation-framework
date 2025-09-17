package com.kernotec.qa.tests;

import com.kernotec.qa.config.ConfigReader;
import com.kernotec.qa.config.DriverManager;
import com.kernotec.qa.config.YamlConfigReader;
import com.kernotec.qa.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Clase base para todos los tests Contiene configuración común y métodos utilitarios para tests
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public class BaseTest {

    protected static final Logger logger = LogManager.getLogger(BaseTest.class);

    // Parámetros configurables
    @Parameters({"browser", "headless", "environment"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("chrome") String browser,
        @Optional("false") String headless,
        @Optional("test") String environment)
    {

        logger.info("=== INICIANDO TEST ===");
        logger.info("Browser: {}, Headless: {}, Environment: {}", browser, headless, environment);

        try {
            // Establecer propiedades del sistema si se pasan como parámetros
            System.setProperty("test.environment", environment);

            // Inicializar driver
            DriverManager.initializeDriver(browser, Boolean.parseBoolean(headless));

            // Navegar a la URL del ambiente
            String targetUrl = ConfigReader.getBaseUrl();
            logger.info("Navegando a URL: {}", targetUrl);
            DriverManager.navigateToUrl(targetUrl);

            // Maximizar ventana si no es headless
            if (!Boolean.parseBoolean(headless)) {
                DriverManager.getDriver()
                    .manage()
                    .window()
                    .maximize();
            }

            logger.info("Setup completado exitosamente");

        } catch (Exception e) {
            logger.error("Error durante setup: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo en setup del test", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        logger.info("=== FINALIZANDO TEST ===");

        try {
            // Capturar screenshot si el test falló
            if (result.getStatus() == ITestResult.FAILURE &&
                ConfigReader.isScreenshotsEnabled())
            {

                String testName = result.getMethod()
                    .getMethodName();
                String screenshotPath = ScreenshotUtils.takeScreenshot(
                    testName + "_FAILED_" + System.currentTimeMillis());

                logger.error("Test falló: {}. Screenshot guardado en: {}",
                    testName, screenshotPath
                );

                // Agregar screenshot al reporte TestNG
                System.setProperty("org.uncommons.reportng.escape-output", "false");
                logger.info("Screenshot: <a href='{}'>Ver Captura</a>", screenshotPath);
            }

            // Información del resultado del test
            logTestResult(result);

        } catch (Exception e) {
            logger.error("Error durante teardown: {}", e.getMessage());
        } finally {
            // Cerrar driver
            DriverManager.quitDriver();
            logger.info("Driver cerrado");
        }
    }

    /**
     * Setup que se ejecuta una vez antes de todos los tests de la clase
     */
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        logger.info(
            "=== INICIANDO CLASE DE TEST: {} ===", this.getClass()
                .getSimpleName());

        // Crear directorios necesarios
        createTestDirectories();
    }

    /**
     * Cleanup que se ejecuta una vez después de todos los tests de la clase
     */
    @AfterClass(alwaysRun = true)
    public void afterClass() {
        logger.info(
            "=== FINALIZANDO CLASE DE TEST: {} ===", this.getClass()
                .getSimpleName());
    }

    /**
     * Setup que se ejecuta una vez antes de toda la suite de tests
     */
    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        logger.info("=== INICIANDO SUITE DE TESTS ===");
        logger.info("Configuración del framework:");
        logger.info("- Browser por defecto: {}", ConfigReader.getDefaultBrowser());
        logger.info("- Environment: {}", ConfigReader.getEnvironment());
        logger.info("- Base URL: {}", ConfigReader.getBaseUrl());
        logger.info("- Headless: {}", ConfigReader.isHeadlessMode());
        logger.info("- Parallel execution: {}", YamlConfigReader.isParallelExecution());
        logger.info("- Thread count: {}", YamlConfigReader.getThreadCount());
    }

    /**
     * Cleanup que se ejecuta una vez después de toda la suite de tests
     */
    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        logger.info("=== SUITE DE TESTS COMPLETADA ===");
    }

    /**
     * Crea los directorios necesarios para screenshots y reportes
     */
    private void createTestDirectories() {
        try {
            java.nio.file.Files.createDirectories(
                java.nio.file.Paths.get(YamlConfigReader.getScreenshotDirectory()));
            java.nio.file.Files.createDirectories(
                java.nio.file.Paths.get(YamlConfigReader.getReportsDirectory()));

            logger.debug("Directorios de test creados");
        } catch (Exception e) {
            logger.warn("Error creando directorios: {}", e.getMessage());
        }
    }

    /**
     * Registra en logs el resultado del test
     *
     * @param result Resultado del test
     */
    private void logTestResult(ITestResult result) {
        String testName = result.getMethod()
            .getMethodName();
        String className = result.getTestClass()
            .getName();
        long duration = result.getEndMillis() - result.getStartMillis();

        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                logger.info("✅ TEST PASÓ: {}.{} ({}ms)", className, testName, duration);
                break;
            case ITestResult.FAILURE:
                logger.error("❌ TEST FALLÓ: {}.{} ({}ms)", className, testName, duration);
                logger.error(
                    "Causa del fallo: {}", result.getThrowable()
                        .getMessage());
                break;
            case ITestResult.SKIP:
                logger.warn("⏭️ TEST SALTADO: {}.{}", className, testName);
                logger.warn("Razón: {}", result.getThrowable() != null ? result.getThrowable()
                    .getMessage() : "No especificada");
                break;
            default:
                logger.info("❓ TEST ESTADO DESCONOCIDO: {}.{}", className, testName);
        }
    }

    /**
     * Método helper para esperar un tiempo específico (usar con moderación)
     *
     * @param milliseconds Milisegundos a esperar
     */
    protected void waitFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread()
                .interrupt();
            logger.error("Espera interrumpida: {}", e.getMessage());
        }
    }

    /**
     * Método helper para obtener el nombre del test actual
     *
     * @return Nombre del test en ejecución
     */
    protected String getCurrentTestName() {
        return Thread.currentThread()
            .getStackTrace()[2].getMethodName();
    }

    /**
     * Método helper para generar datos únicos de test
     *
     * @param prefix Prefijo para el dato
     * @return String único con timestamp
     */
    protected String generateUniqueData(String prefix) {
        return prefix + "_" + System.currentTimeMillis();
    }

    /**
     * Método helper para validar que un test está corriendo en el ambiente esperado
     *
     * @param expectedEnvironment Ambiente esperado
     * @throws RuntimeException si no está en el ambiente correcto
     */
    protected void validateEnvironment(String expectedEnvironment) {
        String currentEnv = YamlConfigReader.getEnvironment();
        if (!currentEnv.equalsIgnoreCase(expectedEnvironment)) {
            throw new RuntimeException(
                String.format("Test requiere ambiente '%s' pero está ejecutando en '%s'",
                    expectedEnvironment, currentEnv
                ));
        }
    }

    /**
     * Método helper para marcar un test como en progreso en logs
     *
     * @param testDescription Descripción del test
     */
    protected void logTestStart(String testDescription) {
        logger.info("🚀 Iniciando test: {}", testDescription);
    }

    /**
     * Método helper para marcar un paso del test en logs
     *
     * @param stepDescription Descripción del paso
     */
    protected void logTestStep(String stepDescription) {
        logger.info("📋 Paso: {}", stepDescription);
    }
}