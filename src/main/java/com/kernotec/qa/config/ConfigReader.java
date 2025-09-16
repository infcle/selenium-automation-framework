package com.kernotec.qa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Clase utilitaria para leer archivos de configuración
 * Implementa Singleton pattern para manejo único de configuraciones
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public class ConfigReader {
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static Properties properties;
    private static ConfigReader configReader;

    // Rutas de archivos de configuración
    private static final String CONFIG_FILE_PATH = "src/test/resources/config/application.properties";
    private static final String TEST_CONFIG_FILE_PATH = "src/test/resources/config/test-config.properties";

    private ConfigReader() {
        loadProperties();
    }

    /**
     * Obtiene la instancia única de ConfigReader (Singleton)
     * @return Instancia de ConfigReader
     */
    public static ConfigReader getInstance() {
        if (configReader == null) {
            synchronized (ConfigReader.class) {
                if (configReader == null) {
                    configReader = new ConfigReader();
                }
            }
        }
        return configReader;
    }

    /**
     * Carga las propiedades desde los archivos de configuración
     */
    private void loadProperties() {
        properties = new Properties();

        try {
            // Cargar configuración principal
            FileInputStream configFile = new FileInputStream(CONFIG_FILE_PATH);
            properties.load(configFile);
            configFile.close();

            // Cargar configuración de tests
            FileInputStream testConfigFile = new FileInputStream(TEST_CONFIG_FILE_PATH);
            properties.load(testConfigFile);
            testConfigFile.close();

            logger.info("Archivos de configuración cargados exitosamente");

        } catch (IOException e) {
            logger.error("Error cargando archivos de configuración: {}", e.getMessage());
            throw new RuntimeException("No se pudieron cargar los archivos de configuración", e);
        }
    }

    /**
     * Obtiene el valor de una propiedad
     * @param key Clave de la propiedad
     * @return Valor de la propiedad
     */
    public static String getProperty(String key) {
        getInstance();
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Propiedad '{}' no encontrada en archivos de configuración", key);
        }
        return value;
    }

    /**
     * Obtiene el valor de una propiedad con valor por defecto
     * @param key Clave de la propiedad
     * @param defaultValue Valor por defecto si la propiedad no existe
     * @return Valor de la propiedad o valor por defecto
     */
    public static String getProperty(String key, String defaultValue) {
        getInstance();
        return properties.getProperty(key, defaultValue);
    }

    // Métodos específicos para propiedades comunes

    /**
     * Obtiene la URL base de la aplicación
     * @return URL base
     */
    public static String getBaseUrl() {
        return getProperty("app.base.url", "https://example.com");
    }

    /**
     * Obtiene el browser por defecto
     * @return Nombre del browser
     */
    public static String getDefaultBrowser() {
        return getProperty("default.browser", "chrome");
    }

    /**
     * Obtiene el timeout implícito en segundos
     * @return Timeout en segundos
     */
    public static int getImplicitWait() {
        return Integer.parseInt(getProperty("implicit.wait", "10"));
    }

    /**
     * Obtiene el timeout explícito en segundos
     * @return Timeout en segundos
     */
    public static int getExplicitWait() {
        return Integer.parseInt(getProperty("explicit.wait", "30"));
    }

    /**
     * Obtiene el timeout de carga de página en segundos
     * @return Timeout en segundos
     */
    public static int getPageLoadTimeout() {
        return Integer.parseInt(getProperty("page.load.timeout", "60"));
    }

    /**
     * Determina si se debe ejecutar en modo headless
     * @return true si headless, false si no
     */
    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless.mode", "false"));
    }

    /**
     * Obtiene el ambiente de ejecución
     * @return Ambiente (dev, test, prod)
     */
    public static String getEnvironment() {
        return getProperty("test.environment", "test");
    }

    /**
     * Obtiene la URL según el ambiente
     * @return URL del ambiente
     */
    public static String getEnvironmentUrl() {
        String environment = getEnvironment();
        return getProperty("app.url." + environment, getBaseUrl());
    }

    /**
     * Determina si se debe tomar screenshot en fallos
     * @return true si se debe tomar screenshot
     */
    public static boolean takeScreenshotOnFailure() {
        return Boolean.parseBoolean(getProperty("screenshot.on.failure", "true"));
    }

    /**
     * Obtiene el directorio para screenshots
     * @return Ruta del directorio
     */
    public static String getScreenshotDirectory() {
        return getProperty("screenshot.directory", "test-output/screenshots");
    }

    /**
     * Obtiene el directorio para reportes
     * @return Ruta del directorio
     */
    public static String getReportsDirectory() {
        return getProperty("reports.directory", "test-output/reports");
    }

    /**
     * Determina si la ejecución debe ser paralela
     * @return true si paralela
     */
    public static boolean isParallelExecution() {
        return Boolean.parseBoolean(getProperty("parallel.execution", "false"));
    }

    /**
     * Obtiene el número de threads para ejecución paralela
     * @return Número de threads
     */
    public static int getThreadCount() {
        return Integer.parseInt(getProperty("thread.count", "1"));
    }
}
