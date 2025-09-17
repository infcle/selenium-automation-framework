package com.kernotec.qa.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Clase especializada para lectura de configuraciones YAML.
 * Proporciona métodos específicos para acceder a configuraciones de browsers y
 * timeouts.
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public class YamlConfigReader {

    private static final Logger logger = LogManager.getLogger(YamlConfigReader.class);
    private static JsonNode yamlConfig;
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    static {
        loadYamlConfiguration();
    }

    /**
     * Carga la configuración YAML principal
     */
    private static void loadYamlConfiguration() {
        try (InputStream inputStream = YamlConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config/application.yml")) {

            if (inputStream != null) {
                yamlConfig = yamlMapper.readTree(inputStream);
                logger.info("Configuración YAML cargada exitosamente");
            } else {
                logger.warn("No se encontró el archivo application.yml");
            }
        } catch (Exception e) {
            logger.error("Error cargando configuración YAML: {}", e.getMessage());
            throw new RuntimeException("No se pudo cargar la configuración YAML", e);
        }
    }

    /**
     * Obtiene las opciones de Chrome desde la configuración YAML
     */
    public static List<String> getChromeOptions() {
        return getStringList("browser.options.chrome");
    }

    /**
     * Obtiene las preferencias de Firefox desde la configuración YAML
     */
    public static Map<String, Object> getFirefoxPreferences() {
        return getMap("browser.options.firefox.preferences");
    }

    /**
     * Obtiene el timeout implícito desde YAML
     */
    public static int getImplicitWait() {
        JsonNode node = getNode("timeouts.implicit");
        return node != null ? node.asInt() : 10;
    }

    /**
     * Obtiene el timeout explícito desde YAML
     */
    public static int getExplicitWait() {
        JsonNode node = getNode("timeouts.explicit");
        return node != null ? node.asInt() : 30;
    }

    /**
     * Obtiene el timeout de carga de página desde YAML
     */
    public static int getPageLoadTimeout() {
        JsonNode node = getNode("timeouts.pageLoad");
        return node != null ? node.asInt() : 60;
    }

    /**
     * Obtiene el timeout de script desde YAML
     */
    public static int getScriptTimeout() {
        JsonNode node = getNode("timeouts.script");
        return node != null ? node.asInt() : 30;
    }

    /**
     * Obtiene la configuración de polling desde YAML
     */
    public static int getPollingInterval() {
        JsonNode node = getNode("timeouts.polling");
        return node != null ? node.asInt() : 500;
    }

    /**
     * Verifica si una característica está habilitada
     */
    public static boolean isFeatureEnabled(String feature) {
        JsonNode node = getNode("features." + feature);
        return node != null ? node.asBoolean() : false;
    }

    /**
     * Obtiene el nivel de logging configurado
     */
    public static String getLoggingLevel() {
        JsonNode node = getNode("logging.level");
        return node != null ? node.asText() : "INFO";
    }

    /**
     * Obtiene la ruta de reportes configurada
     */
    public static String getReportsPath() {
        JsonNode node = getNode("reporting.extentReports.path");
        return node != null ? node.asText() : "test-output/extent-reports/";
    }

    /**
     * Obtiene la ruta de capturas de pantalla
     */
    public static String getScreenshotsPath() {
        JsonNode node = getNode("reporting.screenshots.path");
        return node != null ? node.asText() : "test-output/screenshots/";
    }

    /**
     * Obtiene un nodo desde la configuración YAML usando notación de punto
     */
    private static JsonNode getNode(String key) {
        if (yamlConfig == null || key == null) {
            return null;
        }

        String[] keys = key.split("\\.");
        JsonNode current = yamlConfig;

        for (String k : keys) {
            if (current == null || !current.has(k)) {
                return null;
            }
            current = current.get(k);
        }

        return current;
    }

    /**
     * Obtiene una lista de strings desde la configuración YAML
     */
    private static List<String> getStringList(String key) {
        JsonNode node = getNode(key);
        List<String> list = new ArrayList<>();

        if (node != null && node.isArray()) {
            node.forEach(element -> list.add(element.asText()));
        }

        return list;
    }

    /**
     * Obtiene un mapa desde la configuración YAML
     */
    private static Map<String, Object> getMap(String key) {
        JsonNode node = getNode(key);
        Map<String, Object> map = new HashMap<>();

        if (node != null && node.isObject()) {
            node.fieldNames().forEachRemaining(fieldName -> {
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
                } else if (fieldNode.isObject()) {
                    map.put(fieldName, fieldNode.toString());
                }
            });
        }

        return map;
    }

    /**
     * Recarga la configuración YAML (útil para testing)
     */
    public static void reloadConfiguration() {
        loadYamlConfiguration();
        logger.info("Configuración YAML recargada");
    }

    /**
     * Verifica si la configuración YAML está cargada
     */
    public static boolean isConfigurationLoaded() {
        return yamlConfig != null;
    }
}