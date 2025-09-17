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
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Clase para lectura y gestión de configuraciones desde archivos YAML y Properties. Implementa el
 * patrón Singleton para asegurar una única instancia de configuración.
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static ConfigReader instance;
    private JsonNode yamlConfig;
    private Properties properties;
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
     * Carga las configuraciones desde archivos YAML y Properties
     */
    private void loadConfigurations() {
        try {
            // Cargar configuración principal desde application.yml
            loadYamlConfig("config/application.yml");

            // Cargar configuración de test desde test-config.yml
            loadYamlConfig("config/test-config.yml");

            // Cargar properties si existen
            loadProperties("application.properties");

            logger.info("Configuraciones cargadas exitosamente");
        } catch (Exception e) {
            logger.error("Error cargando configuraciones: {}", e.getMessage());
            throw new RuntimeException("No se pudieron cargar las configuraciones", e);
        }
    }

    /**
     * Carga configuración desde archivo YAML
     *
     * @param configPath Ruta del archivo de configuración
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
                logger.warn("No se encontró el archivo de configuración: {}", configPath);
            }
        } catch (IOException e) {
            logger.error(
                "Error cargando configuración YAML desde {}: {}", configPath, e.getMessage());
        }
    }

    /**
     * Carga configuración desde archivo Properties
     *
     * @param propertiesPath Ruta del archivo properties
     */
    private void loadProperties(String propertiesPath) {
        try (InputStream inputStream = getClass().getClassLoader()
            .getResourceAsStream(propertiesPath))
        {
            if (inputStream != null) {
                properties = new Properties();
                properties.load(inputStream);
                logger.debug("Configuración Properties cargada desde: {}", propertiesPath);
            }
        } catch (IOException e) {
            logger.debug("No se encontró archivo Properties: {}", propertiesPath);
        }
    }

    /**
     * Fusiona dos nodos JSON (para override de configuraciones)
     */
    private JsonNode mergeJsonNodes(JsonNode mainNode, JsonNode updateNode) {
        if (updateNode.isObject() && mainNode.isObject()) {
            Map<String, JsonNode> mergedFields = new HashMap<>();

            // Agregar todos los campos del nodo principal
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
        return updateNode; // Si no son objetos, retornar el nodo de actualización
    }

    // Métodos públicos para acceder a configuraciones
    // ================================================

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
     * Obtiene un valor Integer desde la configuración
     *
     * @param key Clave de configuración
     * @return Valor Integer o null si no existe
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
     * Obtiene un valor Boolean desde la configuración
     *
     * @param key Clave de configuración
     * @return Valor Boolean o null si no existe
     */
    public static Boolean getBoolean(String key) {
        String value = getString(key);
        return value != null ? Boolean.parseBoolean(value) : null;
    }

    /**
     * Obtiene un mapa de valores desde la configuración
     *
     * @param key Clave de configuración
     * @return Mapa de valores o null si no existe
     */
    public static Map<String, Object> getMap(String key) {
        return getInstance().getMapValue(key);
    }

    /**
     * Implementación interna para obtener valor String
     */
    private String getStringValue(String key) {
        // Primero buscar en Properties (más alta prioridad)
        if (properties != null && properties.containsKey(key)) {
            return properties.getProperty(key);
        }

        // Luego buscar en YAML
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
                    } else {
                        map.put(fieldName, fieldNode.toString());
                    }
                });
            return map;
        }
        return null;
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

    // Métodos de conveniencia para configuraciones específicas
    // =======================================================

    /**
     * Obtiene la URL base del ambiente actual
     */
    public static String getBaseUrl() {
        String environment = getString("testing.environment");
        String baseUrl = getString("environments." + environment + ".baseUrl");
        return baseUrl != null ? baseUrl : getString("app.baseUrl");
    }

    /**
     * Obtiene el browser por defecto
     */
    public static String getDefaultBrowser() {
        return getString("browser.default");
    }

    /**
     * Obtiene el timeout implícito
     */
    public static int getImplicitWait() {
        Integer timeout = getInt("timeouts.implicit");
        return timeout != null ? timeout : 10;
    }

    /**
     * Obtiene las opciones de Chrome desde la configuración
     */
    public static List<String> getChromeOptions() {
        // Implementar lógica para obtener opciones de Chrome desde YAML
        Map<String, Object> browserConfig = getMap("browser.chrome");
        if (browserConfig != null && browserConfig.containsKey("options")) {
            return (List<String>) browserConfig.get("options");
        }
        return new ArrayList<>();
    }

    public static Map<String, Object> getFirefoxPreferences() {
        return getMap("browser.firefox.preferences");
    }

    /**
     * Obtiene el timeout explícito
     */
    public static int getExplicitWait() {
        Integer timeout = getInt("timeouts.explicit");
        return timeout != null ? timeout : 30;
    }

    /**
     * Obtiene el timeout de carga de página
     */
    public static int getPageLoadTimeout() {
        Integer timeout = getInt("timeouts.pageLoad");
        return timeout != null ? timeout : 60;
    }

    /**
     * Verifica si el modo headless está habilitado
     */
    public static boolean isHeadlessMode() {
        Boolean headless = getBoolean("browser.headless");
        return headless != null ? headless : false;
    }

    /**
     * Obtiene el ambiente actual
     */
    public static String getEnvironment() {
        return getString("testing.environment");
    }

    /**
     * Verifica si las capturas de pantalla están habilitadas
     */
    public static boolean isScreenshotsEnabled() {
        Boolean enabled = getBoolean("features.screenshots");
        return enabled != null ? enabled : true;
    }

    /**
     * Obtiene el username para un rol específico
     *
     * @param role Rol del usuario
     * @return Username o null si no existe
     */
    public static String getUsername(String role) {
        return getString("users." + role + ".username");
    }

    /**
     * Obtiene el password para un rol específico
     *
     * @param role Rol del usuario
     * @return Password o null si no existe
     */
    public static String getPassword(String role) {
        return getString("users." + role + ".password");
    }
}