package com.kernotec.qa.config;

import java.util.Map;

/**
 * Clase de configuración de tests que proporciona acceso simplificado a las propiedades de prueba
 * definidas en 'application.yml'. Esta clase actúa como una capa de abstracción sobre ConfigReader
 * para una mejor legibilidad del código de prueba.
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public final class TestConfig {

    /**
     * Constructor privado para evitar la instanciación de la clase utilitaria.
     */
    private TestConfig() {
    }

    // Métodos para acceder a la configuración del entorno y la ejecución
    // ----------------------------------------------------------------------

    /**
     * Obtiene el entorno de ejecución de las pruebas.
     *
     * @return El nombre del entorno (e.g., "dev", "test", "prod").
     */
    public static String getEnvironment() {
        return ConfigReader.getString("testing.environment");
    }

    /**
     * Obtiene la URL base de la aplicación para el entorno actual.
     *
     * @return La URL completa.
     */
    public static String getBaseUrl() {
        return ConfigReader.getBaseUrl();
    }

    /**
     * Obtiene el navegador por defecto para la ejecución.
     *
     * @return El nombre del navegador.
     */
    public static String getBrowser() {
        return ConfigReader.getDefaultBrowser();
    }

    /**
     * Determina si la ejecución se realizará en modo headless.
     *
     * @return true si el modo headless está activado, false de lo contrario.
     */
    public static boolean isHeadlessMode() {
        return ConfigReader.getBoolean("browser.headless");
    }

    // Métodos para acceder a los timeouts
    // ----------------------------------------------------------------------

    /**
     * Obtiene el tiempo de espera explícito en segundos.
     *
     * @return El timeout explícito.
     */
    public static int getExplicitTimeout() {
        return ConfigReader.getExplicitWait();
    }

    /**
     * Obtiene el tiempo de espera implícito en segundos.
     *
     * @return El timeout implícito.
     */
    public static int getImplicitTimeout() {
        return ConfigReader.getImplicitWait();
    }

    // Métodos para acceder a los datos de usuario
    // ----------------------------------------------------------------------

    /**
     * Obtiene las credenciales del usuario de prueba por su rol.
     *
     * @param userRole El rol del usuario (e.g., "admin", "test").
     * @return Un mapa con las credenciales del usuario, o null si el rol no existe.
     */
    public static Map<String, Object> getUserCredentials(String userRole) {
        return ConfigReader.getMap("users." + userRole);
    }

    /**
     * Obtiene el nombre de usuario para un rol específico.
     *
     * @param userRole El rol del usuario.
     * @return El nombre de usuario, o null si no se encuentra.
     */
    public static String getUsername(String userRole) {
        Map<String, Object> user = getUserCredentials(userRole);
        return (user != null) ? (String) user.get("username") : null;
    }

    /**
     * Obtiene la contraseña para un rol de usuario específico.
     *
     * @param userRole El rol del usuario.
     * @return La contraseña, o null si no se encuentra.
     */
    public static String getPassword(String userRole) {
        Map<String, Object> user = getUserCredentials(userRole);
        return (user != null) ? (String) user.get("password") : null;
    }

    // Métodos para acceder a las feature flags
    // ----------------------------------------------------------------------

    /**
     * Determina si las capturas de pantalla están habilitadas.
     *
     * @return true si están habilitadas.
     */
    public static boolean isScreenshotsEnabled() {
        return ConfigReader.getBoolean("features.screenshots");
    }

    /**
     * Determina si el testing de API está habilitado.
     *
     * @return true si el testing de API está habilitado.
     */
    public static boolean isApiTestingEnabled() {
        return ConfigReader.getBoolean("features.apiTesting");
    }

    // Y así sucesivamente con más métodos para otras secciones del YAML
}