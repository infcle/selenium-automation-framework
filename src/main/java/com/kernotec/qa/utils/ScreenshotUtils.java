package com.kernotec.qa.utils;

import com.kernotec.qa.config.ConfigReader;
import com.kernotec.qa.config.DriverManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Clase utilitaria para gestión de capturas de pantalla. Proporciona métodos para tomar, guardar y
 * organizar screenshots.
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public class ScreenshotUtils {

    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(
        "yyyyMMdd_HHmmss");

    /**
     * Toma una captura de pantalla con nombre personalizado
     *
     * @param screenshotName Nombre para la captura
     * @return Ruta completa del archivo de captura
     */
    public static String takeScreenshot(String screenshotName) {
        try {
            WebDriver driver = DriverManager.getDriver();
            if (driver == null) {
                logger.warn("No hay driver disponible para tomar captura");
                return null;
            }

            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            byte[] screenshotBytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);

            String fileName = generateFileName(screenshotName);
            String filePath = saveScreenshot(screenshotBytes, fileName);

            logger.info("Captura de pantalla guardada: {}", filePath);
            return filePath;

        } catch (Exception e) {
            logger.error(
                "Error tomando captura de pantalla '{}': {}", screenshotName, e.getMessage());
            return null;
        }
    }

    /**
     * Toma una captura de pantalla con timestamp automático
     *
     * @return Ruta completa del archivo de captura
     */
    public static String takeScreenshot() {
        String timestamp = LocalDateTime.now()
            .format(TIMESTAMP_FORMATTER);
        return takeScreenshot("screenshot_" + timestamp);
    }

    /**
     * Toma una captura de pantalla en caso de fallo
     *
     * @param testName Nombre del test que falló
     * @return Ruta completa del archivo de captura
     */
    public static String takeFailureScreenshot(String testName) {
        if (!ConfigReader.isScreenshotsEnabled()) {
            logger.debug("Capturas de pantalla deshabilitadas");
            return null;
        }

        String fileName = "FAILURE_" + testName + "_" + LocalDateTime.now()
            .format(TIMESTAMP_FORMATTER);
        return takeScreenshot(fileName);
    }

    /**
     * Toma una captura de pantalla en caso de éxito
     *
     * @param testName Nombre del test que pasó
     * @return Ruta completa del archivo de captura
     */
    public static String takeSuccessScreenshot(String testName) {
        if (!ConfigReader.isScreenshotsEnabled()) {
            logger.debug("Capturas de pantalla deshabilitadas");
            return null;
        }

        String fileName = "SUCCESS_" + testName + "_" + LocalDateTime.now()
            .format(TIMESTAMP_FORMATTER);
        return takeScreenshot(fileName);
    }

    /**
     * Toma una captura de pantalla de un elemento específico
     *
     * @param element     Elemento a capturar
     * @param elementName Nombre del elemento
     * @return Ruta completa del archivo de captura
     */
    public static String takeElementScreenshot(org.openqa.selenium.WebElement element,
        String elementName)
    {
        try {
            WebDriver driver = DriverManager.getDriver();
            if (driver == null) {
                logger.warn("No hay driver disponible para tomar captura de elemento");
                return null;
            }

            byte[] elementScreenshot = element.getScreenshotAs(OutputType.BYTES);
            String fileName = "ELEMENT_" + elementName + "_" + LocalDateTime.now()
                .format(TIMESTAMP_FORMATTER);
            String filePath = saveScreenshot(elementScreenshot, fileName + ".png");

            logger.info("Captura de elemento guardada: {}", filePath);
            return filePath;

        } catch (Exception e) {
            logger.error("Error tomando captura de elemento '{}': {}", elementName, e.getMessage());
            return null;
        }
    }

    /**
     * Genera un nombre de archivo único con timestamp
     *
     * @param baseName Nombre base del archivo
     * @return Nombre de archivo con timestamp
     */
    private static String generateFileName(String baseName) {
        String timestamp = LocalDateTime.now()
            .format(TIMESTAMP_FORMATTER);
        String cleanName = baseName.replaceAll("[^a-zA-Z0-9_-]", "_");
        return cleanName + "_" + timestamp + ".png";
    }

    /**
     * Guarda la captura de pantalla en el directorio configurado
     *
     * @param screenshotBytes Bytes de la imagen
     * @param fileName        Nombre del archivo
     * @return Ruta completa del archivo guardado
     */
    private static String saveScreenshot(byte[] screenshotBytes, String fileName) {
        try {
            String screenshotsDir = getScreenshotsDirectory();
            createDirectoryIfNotExists(screenshotsDir);

            Path filePath = Paths.get(screenshotsDir, fileName);
            Files.write(filePath, screenshotBytes);

            return filePath.toString();

        } catch (IOException e) {
            logger.error("Error guardando captura de pantalla: {}", e.getMessage());
            throw new RuntimeException("No se pudo guardar la captura de pantalla", e);
        }
    }

    /**
     * Obtiene el directorio de capturas de pantalla desde la configuración
     *
     * @return Ruta del directorio de capturas
     */
    private static String getScreenshotsDirectory() {
        // Intentar obtener desde configuración, sino usar default
        String configPath = ConfigReader.getString("reporting.screenshots.path");
        if (configPath != null && !configPath.trim()
            .isEmpty())
        {
            return configPath;
        }
        return "test-output/screenshots/";
    }

    /**
     * Crea el directorio si no existe
     *
     * @param directoryPath Ruta del directorio
     */
    private static void createDirectoryIfNotExists(String directoryPath) {
        try {
            Path path = Paths.get(directoryPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.debug("Directorio creado: {}", directoryPath);
            }
        } catch (IOException e) {
            logger.error("Error creando directorio '{}': {}", directoryPath, e.getMessage());
            throw new RuntimeException("No se pudo crear el directorio de capturas", e);
        }
    }

    /**
     * Limpia capturas de pantalla antiguas (más de X días)
     *
     * @param daysToKeep Número de días a mantener
     */
    public static void cleanupOldScreenshots(int daysToKeep) {
        try {
            String screenshotsDir = getScreenshotsDirectory();
            Path dirPath = Paths.get(screenshotsDir);

            if (!Files.exists(dirPath)) {
                logger.debug("Directorio de capturas no existe: {}", screenshotsDir);
                return;
            }

            long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24L * 60L * 60L * 1000L);
            int deletedCount = 0;

            Files.walk(dirPath)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString()
                    .toLowerCase()
                    .endsWith(".png"))
                .filter(path -> {
                    try {
                        return Files.getLastModifiedTime(path)
                            .toMillis() < cutoffTime;
                    } catch (IOException e) {
                        logger.warn(
                            "Error obteniendo fecha de modificación de {}: {}", path,
                            e.getMessage()
                        );
                        return false;
                    }
                })
                .forEach(path -> {
                    try {
                        Files.delete(path);
                        logger.debug("Captura antigua eliminada: {}", path.getFileName());
                    } catch (IOException e) {
                        logger.warn(
                            "Error eliminando captura antigua {}: {}", path, e.getMessage());
                    }
                });

            logger.info("Limpieza completada. {} capturas eliminadas", deletedCount);

        } catch (Exception e) {
            logger.error("Error en limpieza de capturas: {}", e.getMessage());
        }
    }

    /**
     * Obtiene información sobre el directorio de capturas
     *
     * @return Información del directorio
     */
    public static String getScreenshotsDirectoryInfo() {
        try {
            String screenshotsDir = getScreenshotsDirectory();
            Path dirPath = Paths.get(screenshotsDir);

            if (!Files.exists(dirPath)) {
                return "Directorio no existe: " + screenshotsDir;
            }

            long fileCount = Files.walk(dirPath)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString()
                    .toLowerCase()
                    .endsWith(".png"))
                .count();

            return String.format("Directorio: %s, Archivos: %d", screenshotsDir, fileCount);

        } catch (Exception e) {
            logger.error("Error obteniendo información del directorio: {}", e.getMessage());
            return "Error obteniendo información del directorio";
        }
    }

    /**
     * Verifica si las capturas están habilitadas
     *
     * @return true si están habilitadas
     */
    public static boolean isScreenshotEnabled() {
        return ConfigReader.isScreenshotsEnabled();
    }

    /**
     * Configura el formato de timestamp para nombres de archivo
     *
     * @param pattern Patrón de formato
     */
    public static void setTimestampPattern(String pattern) {
        try {
            DateTimeFormatter.ofPattern(pattern);
            logger.info("Patrón de timestamp configurado: {}", pattern);
        } catch (Exception e) {
            logger.warn("Patrón de timestamp inválido '{}': {}", pattern, e.getMessage());
        }
    }
}