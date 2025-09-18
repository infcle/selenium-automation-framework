package com.kernotec.qa.config;

import java.util.Map;

/**
 * Clase wrapper simplificada que proporciona acceso fácil a las configuraciones más comunes. Actúa
 * como una capa de conveniencia sobre ConfigReader para tests. Esta versión elimina duplicación y
 * se enfoca en métodos útiles para testing.
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public final class TestConfig {

    /**
     * Constructor privado para evitar instanciación
     */
    private TestConfig() {
        throw new UnsupportedOperationException("TestConfig es una clase utilitaria");
    }

    // ===================================================================
    // MÉTODOS DE CONFIGURACIÓN DE ENTORNO Y EJECUCIÓN
    // ===================================================================

    /**
     * Obtiene el entorno de ejecución actual
     *
     * @return Nombre del entorno (dev, test, staging, prod)
     */
    public static String getEnvironment() {
        return ConfigReader.getEnvironment();
    }

    /**
     * Obtiene la URL base para el entorno actual
     *
     * @return URL base de la aplicación
     */
    public static String getBaseUrl() {
        return ConfigReader.getBaseUrl();
    }

    /**
     * Obtiene la URL base de la API para el entorno actual
     *
     * @return URL base de la API
     */
    public static String getApiBaseUrl() {
        return ConfigReader.getApiBaseUrl();
    }

    // ===================================================================
    // MÉTODOS DE CONFIGURACIÓN DE BROWSER
    // ===================================================================

    /**
     * Obtiene el browser por defecto
     *
     * @return Nombre del browser (chrome, firefox, edge)
     */
    public static String getDefaultBrowser() {
        return ConfigReader.getDefaultBrowser();
    }

    /**
     * Verifica si debe ejecutarse en modo headless
     *
     * @return true si headless está activado
     */
    public static boolean isHeadlessMode() {
        return ConfigReader.isHeadlessMode();
    }

    // ===================================================================
    // MÉTODOS DE TIMEOUTS
    // ===================================================================

    /**
     * Obtiene el timeout implícito en segundos
     *
     * @return Timeout implícito
     */
    public static int getImplicitTimeout() {
        return ConfigReader.getImplicitWait();
    }

    /**
     * Obtiene el timeout explícito en segundos
     *
     * @return Timeout explícito
     */
    public static int getExplicitTimeout() {
        return ConfigReader.getExplicitWait();
    }

    /**
     * Obtiene el timeout de carga de página en segundos
     *
     * @return Timeout de página
     */
    public static int getPageLoadTimeout() {
        return ConfigReader.getPageLoadTimeout();
    }

    /**
     * Obtiene el timeout de script en segundos
     *
     * @return Timeout de script
     */
    public static int getScriptTimeout() {
        return ConfigReader.getScriptTimeout();
    }

    // ===================================================================
    // MÉTODOS DE USUARIOS DE PRUEBA
    // ===================================================================

    /**
     * Obtiene credenciales completas de un usuario por su rol
     *
     * @param role Rol del usuario (admin, manager, employee, test)
     * @return Mapa con credenciales y información del usuario
     */
    public static Map<String, Object> getUserCredentials(String role) {
        return ConfigReader.getUserCredentials(role);
    }

    /**
     * Obtiene el username de un usuario por su rol
     *
     * @param role Rol del usuario
     * @return Username del usuario
     */
    public static String getUsername(String role) {
        return ConfigReader.getUsername(role);
    }

    /**
     * Obtiene la contraseña de un usuario por su rol
     *
     * @param role Rol del usuario
     * @return Contraseña del usuario
     */
    public static String getPassword(String role) {
        return ConfigReader.getPassword(role);
    }

    // Métodos de conveniencia para usuarios específicos

    public static String getAdminUsername() {
        return getUsername("admin");
    }

    public static String getAdminPassword() {
        return getPassword("admin");
    }

    public static String getTestUsername() {
        return getUsername("test");
    }

    public static String getTestPassword() {
        return getPassword("test");
    }

    // ===================================================================
    // MÉTODOS DE CONFIGURACIÓN DE EJECUCIÓN
    // ===================================================================

    /**
     * Verifica si la ejecución paralela está habilitada
     *
     * @return true si está habilitada
     */
    public static boolean isParallelExecution() {
        return ConfigReader.isParallelExecution();
    }

    /**
     * Obtiene el número de threads para ejecución paralela
     *
     * @return Número de threads
     */
    public static int getThreadCount() {
        return ConfigReader.getThreadCount();
    }

    /**
     * Obtiene el número máximo de reintentos para tests fallidos
     *
     * @return Número de reintentos
     */
    public static int getRetryCount() {
        return ConfigReader.getInt("testing.retry.failedTests", 1);
    }

    // ===================================================================
    // MÉTODOS DE CONFIGURACIÓN DE FEATURES
    // ===================================================================

    /**
     * Verifica si las capturas de pantalla están habilitadas
     *
     * @return true si están habilitadas
     */
    public static boolean isScreenshotsEnabled() {
        return ConfigReader.isScreenshotsEnabled();
    }

    /**
     * Verifica si se deben tomar capturas en fallos
     *
     * @return true si se deben tomar capturas en fallos
     */
    public static boolean takeScreenshotOnFailure() {
        return ConfigReader.takeScreenshotOnFailure();
    }

    /**
     * Verifica si el testing de API está habilitado
     *
     * @return true si API testing está habilitado
     */
    public static boolean isApiTestingEnabled() {
        return ConfigReader.isFeatureEnabled("apiTesting");
    }

    /**
     * Verifica si el cross-browser testing está habilitado
     *
     * @return true si está habilitado
     */
    public static boolean isCrossBrowserTestingEnabled() {
        return ConfigReader.isFeatureEnabled("crossBrowserTesting");
    }

    /**
     * Verifica si una feature específica está habilitada
     *
     * @param featureName Nombre de la feature
     * @return true si está habilitada
     */
    public static boolean isFeatureEnabled(String featureName) {
        return ConfigReader.isFeatureEnabled(featureName);
    }

    // ===================================================================
    // MÉTODOS DE CONFIGURACIÓN DE DIRECTORIOS
    // ===================================================================

    /**
     * Obtiene el directorio de capturas de pantalla
     *
     * @return Ruta del directorio
     */
    public static String getScreenshotDirectory() {
        return ConfigReader.getScreenshotDirectory();
    }

    /**
     * Obtiene el directorio de reportes
     *
     * @return Ruta del directorio
     */
    public static String getReportsDirectory() {
        return ConfigReader.getReportsDirectory();
    }

    /**
     * Obtiene el directorio de logs
     *
     * @return Ruta del directorio
     */
    public static String getLogsDirectory() {
        return ConfigReader.getLogsDirectory();
    }

    // ===================================================================
    // MÉTODOS DE CONFIGURACIÓN AVANZADA
    // ===================================================================

    /**
     * Obtiene el número de reintentos para elementos
     *
     * @return Número de reintentos
     */
    public static int getElementRetryCount() {
        return ConfigReader.getElementRetryCount();
    }

    /**
     * Obtiene el delay entre reintentos para elementos
     *
     * @return Delay en milisegundos
     */
    public static int getElementRetryDelay() {
        return ConfigReader.getElementRetryDelay();
    }

    /**
     * Obtiene el intervalo de polling para waits
     *
     * @return Intervalo en milisegundos
     */
    public static int getPollingInterval() {
        return ConfigReader.getPollingInterval();
    }

    /**
     * Obtiene el timeout de API
     *
     * @return Timeout en segundos
     */
    public static int getApiTimeout() {
        return ConfigReader.getApiTimeout();
    }

    // ===================================================================
    // MÉTODOS DE UTILIDAD
    // ===================================================================

    /**
     * Imprime la configuración actual para debugging
     */
    public static void printConfiguration() {
        ConfigReader.printCurrentConfig();
    }

    /**
     * Verifica si la configuración está completamente cargada
     *
     * @return true si está cargada
     */
    public static boolean isConfigurationLoaded() {
        return ConfigReader.isConfigurationLoaded();
    }

    /**
     * Obtiene información resumida de la configuración actual
     *
     * @return String con información de configuración
     */
    public static String getConfigurationSummary() {
        return String.format(
            "Environment: %s | Browser: %s | Headless: %s | Parallel: %s | Screenshots: %s",
            getEnvironment(),
            getDefaultBrowser(),
            isHeadlessMode(),
            isParallelExecution(),
            isScreenshotsEnabled()
        );
    }

    /**
     * Valida que la configuración esencial esté presente
     *
     * @throws RuntimeException si falta configuración crítica
     */
    public static void validateEssentialConfiguration() {
        if (getBaseUrl() == null || getBaseUrl().isEmpty()) {
            throw new RuntimeException(
                "URL base no configurada para el ambiente: " + getEnvironment());
        }

        if (getDefaultBrowser() == null || getDefaultBrowser().isEmpty()) {
            throw new RuntimeException("Browser por defecto no configurado");
        }

        if (getAdminUsername() == null || getAdminUsername().isEmpty()) {
            throw new RuntimeException("Usuario admin no configurado");
        }

        // Log de validación exitosa
        ConfigReader.getInstance(); // Asegurar que está inicializado
        System.out.println("✓ Configuración esencial validada correctamente");
    }
}