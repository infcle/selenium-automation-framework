package com.kernotec.qa.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Clase unificada para lectura y gestión de configuraciones desde archivos YAML. Implementa el
 * patrón Singleton para asegurar una única instancia de configuración. Consolida toda la
 * funcionalidad de configuración en una sola clase.
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static ConfigReader instance;
    private JsonNode yamlConfig;
    private final ObjectMapper yamlMapper;

    /**
     * Constructor privado para implementar Singleton
     */
    private ConfigReader() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        loadConfigurations();
    }

    /**
     * Obtiene la instancia única de ConfigReader (Singleton)
     *
     * @return Instancia de ConfigReader
     */
    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    /**
     * Carga las configuraciones desde archivos YAML
     */
    private void loadConfigurations() {
        try {
            // Cargar configuración principal desde application.yml
            loadYamlConfig("config/application.yml");

            // Cargar configuración de test desde test-config.yml (override)
            loadYamlConfig("config/test-config.yml");

            logger.info("Configuraciones YAML cargadas exitosamente");
        } catch (Exception e) {
            logger.error("Error cargando configuraciones: {}", e.getMessage());
            throw new RuntimeException("No se pudieron cargar las configuraciones", e);
        }
    }

    /**
     * Carga configuración desde archivo YAML
     */
    private void loadYamlConfig(String configPath) {
        try (InputStream inputStream = getClass().getClassLoader()
            .getResourceAsStream(configPath))
        {
            if (inputStream != null) {
                JsonNode config = yamlMapper.readTree(inputStream);
                if (yamlConfig == null) {
                    yamlConfig = config;
                } else {
                    // Merge configurations (test-config.yml override application.yml)
                    yamlConfig = mergeJsonNodes(yamlConfig, config);
                }
                logger.debug("Configuración YAML cargada desde: {}", configPath);
            } else {
                logger.debug("No se encontró el archivo de configuración: {}", configPath);
            }
        } catch (IOException e) {
            logger.error(
                "Error cargando configuración YAML desde {}: {}", configPath, e.getMessage());
        }
    }

    /**
     * Fusiona dos nodos JSON (para override de configuraciones)
     */
    private JsonNode mergeJsonNodes(JsonNode mainNode, JsonNode updateNode) {
        if (updateNode.isObject() && mainNode.isObject()) {
            Map<String, JsonNode> mergedFields = new HashMap<>();

            // Agregar campos del nodo principal
            mainNode.fieldNames()
                .forEachRemaining(
                    fieldName -> mergedFields.put(fieldName, mainNode.get(fieldName)));

            // Sobrescribir con campos del nodo de actualización
            updateNode.fieldNames()
                .forEachRemaining(
                    fieldName -> mergedFields.put(fieldName, updateNode.get(fieldName)));

            return yamlMapper.createObjectNode()
                .setAll(mergedFields);
        }
        return updateNode;
    }

    // ===================================================================
    // MÉTODOS PÚBLICOS ESTÁTICOS PARA ACCEDER A CONFIGURACIONES
    // ===================================================================

    /**
     * Obtiene un valor String desde la configuración
     *
     * @param key Clave de configuración (ej: "browser.default")
     * @return Valor String o null si no existe
     */
    public static String getString(String key) {
        return getInstance().getStringValue(key);
    }

    /**
     * Obtiene un valor String con valor por defecto
     */
    public static String getString(String key, String defaultValue) {
        String value = getString(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Obtiene un valor Integer desde la configuración
     */
    public static Integer getInt(String key) {
        String value = getString(key);
        try {
            return value != null ? Integer.parseInt(value) : null;
        } catch (NumberFormatException e) {
            logger.warn(
                "No se pudo convertir a entero el valor '{}' para la clave '{}'", value, key);
            return null;
        }
    }

    /**
     * Obtiene un valor Integer con valor por defecto
     */
    public static int getInt(String key, int defaultValue) {
        Integer value = getInt(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Obtiene un valor Boolean desde la configuración
     */
    public static Boolean getBoolean(String key) {
        String value = getString(key);
        return value != null ? Boolean.parseBoolean(value) : null;
    }

    /**
     * Obtiene un valor Boolean con valor por defecto
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        Boolean value = getBoolean(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Obtiene un mapa de valores desde la configuración
     */
    public static Map<String, Object> getMap(String key) {
        return getInstance().getMapValue(key);
    }

    /**
     * Obtiene una lista de strings desde la configuración
     */
    public static List<String> getList(String key) {
        return getInstance().getListValue(key);
    }

    // ===================================================================
    // MÉTODOS INTERNOS DE IMPLEMENTACIÓN
    // ===================================================================

    /**
     * Implementación interna para obtener valor String
     */
    private String getStringValue(String key) {
        return getValueFromYaml(yamlConfig, key);
    }

    /**
     * Implementación interna para obtener valor Map
     */
    private Map<String, Object> getMapValue(String key) {
        JsonNode node = getNodeFromYaml(yamlConfig, key);
        if (node != null && node.isObject()) {
            Map<String, Object> map = new HashMap<>();
            node.fieldNames()
                .forEachRemaining(fieldName -> {
                    JsonNode fieldNode = node.get(fieldName);
                    if (fieldNode.isTextual()) {
                        map.put(fieldName, fieldNode.asText());
                    } else if (fieldNode.isNumber()) {
                        map.put(fieldName, fieldNode.asInt());
                    } else if (fieldNode.isBoolean()) {
                        map.put(fieldName, fieldNode.asBoolean());
                    } else if (fieldNode.isArray()) {
                        List<String> list = new ArrayList<>();
                        fieldNode.forEach(element -> list.add(element.asText()));
                        map.put(fieldName, list);
                    } else {
                        map.put(fieldName, fieldNode.toString());
                    }
                });
            return map;
        }
        return null;
    }

    /**
     * Implementación interna para obtener valor List
     */
    private List<String> getListValue(String key) {
        JsonNode node = getNodeFromYaml(yamlConfig, key);
        List<String> list = new ArrayList<>();

        if (node != null && node.isArray()) {
            node.forEach(element -> list.add(element.asText()));
        }

        return list;
    }

    /**
     * Obtiene un valor desde configuración YAML usando notación de punto
     */
    private String getValueFromYaml(JsonNode node, String key) {
        JsonNode targetNode = getNodeFromYaml(node, key);
        return targetNode != null ? targetNode.asText() : null;
    }

    /**
     * Obtiene un nodo desde configuración YAML usando notación de punto
     */
    private JsonNode getNodeFromYaml(JsonNode node, String key) {
        if (node == null || key == null) {
            return null;
        }

        String[] keys = key.split("\\.");
        JsonNode current = node;

        for (String k : keys) {
            if (current == null || !current.has(k)) {
                return null;
            }
            current = current.get(k);
        }

        return current;
    }

    // ===================================================================
    // MÉTODOS DE CONVENIENCIA PARA CONFIGURACIONES ESPECÍFICAS
    // ===================================================================

    /**
     * Obtiene la URL base del ambiente actual
     */
    public static String getBaseUrl() {
        String environment = getEnvironment();
        String environmentUrl = getString("environments." + environment + ".baseUrl");
        return environmentUrl != null ? environmentUrl : getString(
            "app.baseUrl", "https://test-app.company.com");
    }

    /**
     * Obtiene el browser por defecto
     */
    public static String getDefaultBrowser() {
        return getString("browser.default", "chrome");
    }

    /**
     * Obtiene el timeout implícito
     */
    public static int getImplicitWait() {
        return getInt("timeouts.implicit", 10);
    }

    /**
     * Obtiene el timeout explícito
     */
    public static int getExplicitWait() {
        return getInt("timeouts.explicit", 30);
    }

    /**
     * Obtiene el timeout de carga de página
     */
    public static int getPageLoadTimeout() {
        return getInt("timeouts.pageLoad", 60);
    }

    /**
     * Obtiene el timeout de script
     */
    public static int getScriptTimeout() {
        return getInt("timeouts.script", 30);
    }

    /**
     * Obtiene el intervalo de polling
     */
    public static int getPollingInterval() {
        return getInt("timeouts.polling", 500);
    }

    /**
     * Verifica si el modo headless está habilitado
     */
    public static boolean isHeadlessMode() {
        return getBoolean("browser.headless", false);
    }

    /**
     * Obtiene el ambiente actual
     */
    public static String getEnvironment() {
        return getString("testing.environment", "test");
    }

    /**
     * Verifica si las capturas de pantalla están habilitadas
     */
    public static boolean isScreenshotsEnabled() {
        return getBoolean("features.screenshots", true);
    }

    /**
     * Verifica si se deben tomar capturas en fallos
     */
    public static boolean takeScreenshotOnFailure() {
        return getBoolean("screenshots.onFailure", true);
    }

    /**
     * Obtiene el directorio de capturas
     */
    public static String getScreenshotDirectory() {
        return getString("screenshots.directory", "test-output/screenshots");
    }

    /**
     * Obtiene el directorio de reportes
     */
    public static String getReportsDirectory() {
        return getString("reports.directory", "test-output/reports");
    }

    /**
     * Obtiene el directorio de logs
     */
    public static String getLogsDirectory() {
        return getString("logging.directory", "test-output/logs");
    }

    /**
     * Verifica si la ejecución debe ser paralela
     */
    public static boolean isParallelExecution() {
        return getBoolean("testing.parallel.enabled", false);
    }

    /**
     * Obtiene el número de threads para ejecución paralela
     */
    public static int getThreadCount() {
        return getInt("testing.parallel.threadCount", 1);
    }

    /**
     * Obtiene el username para un rol específico
     */
    public static String getUsername(String role) {
        return getString("users." + role + ".username");
    }

    /**
     * Obtiene el password para un rol específico
     */
    public static String getPassword(String role) {
        return getString("users." + role + ".password");
    }

    /**
     * Obtiene las credenciales de un usuario específico
     */
    public static Map<String, Object> getUserCredentials(String role) {
        return getMap("users." + role);
    }

    // ===================================================================
    // MÉTODOS ESPECÍFICOS PARA BROWSERS
    // ===================================================================

    /**
     * Obtiene las opciones de Chrome desde la configuración
     */
    public static List<String> getChromeOptions() {
        return getList("browser.chrome.options");
    }

    /**
     * Obtiene las preferencias de Firefox desde la configuración
     */
    public static Map<String, Object> getFirefoxPreferences() {
        return getMap("browser.firefox.preferences");
    }

    /**
     * Obtiene las opciones de Edge desde la configuración
     */
    public static List<String> getEdgeOptions() {
        return getList("browser.edge.options");
    }

    // ===================================================================
    // MÉTODOS PARA FEATURES Y CONFIGURACIONES AVANZADAS
    // ===================================================================

    /**
     * Verifica si una característica está habilitada
     */
    public static boolean isFeatureEnabled(String feature) {
        return getBoolean("features." + feature, false);
    }

    /**
     * Obtiene la configuración de API
     */
    public static Map<String, Object> getApiConfig() {
        return getMap("api");
    }

    /**
     * Obtiene la URL base de la API
     */
    public static String getApiBaseUrl() {
        String environment = getEnvironment();
        String apiUrl = getString("environments." + environment + ".apiUrl");
        return apiUrl != null ? apiUrl : getString("api.baseUrl");
    }

    /**
     * Obtiene el timeout de API
     */
    public static int getApiTimeout() {
        return getInt("api.timeout", 30);
    }

    /**
     * Obtiene el número de reintentos para elementos
     */
    public static int getElementRetryCount() {
        return getInt("advanced.element.retry.count", 3);
    }

    /**
     * Obtiene el delay entre reintentos para elementos
     */
    public static int getElementRetryDelay() {
        return getInt("advanced.element.retry.delay", 1000);
    }

    /**
     * Recarga la configuración YAML (útil para testing)
     */
    public static void reloadConfiguration() {
        instance = null;
        getInstance();
        logger.info("Configuración recargada");
    }

    /**
     * Verifica si la configuración está cargada
     */
    public static boolean isConfigurationLoaded() {
        return getInstance().yamlConfig != null;
    }

    /**
     * Imprime la configuración actual para debugging
     */
    public static void printCurrentConfig() {
        logger.info("=== CONFIGURACIÓN ACTUAL ===");
        logger.info("Environment: {}", getEnvironment());
        logger.info("Base URL: {}", getBaseUrl());
        logger.info("Default Browser: {}", getDefaultBrowser());
        logger.info("Headless: {}", isHeadlessMode());
        logger.info("Parallel Execution: {}", isParallelExecution());
        logger.info("Thread Count: {}", getThreadCount());
        logger.info("Screenshots Enabled: {}", isScreenshotsEnabled());
        logger.info("===========================");
    }
}