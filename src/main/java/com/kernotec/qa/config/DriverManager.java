package com.kernotec.qa.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Clase para gestión centralizada de WebDrivers Implementa Factory Pattern para crear diferentes
 * tipos de drivers Actualizada para usar configuración YAML
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public class DriverManager {

    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    private DriverManager() {
        // Constructor privado para implementar Singleton
    }

    /**
     * Obtiene la instancia del WebDriver para el thread actual
     *
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        return driver.get();
    }

    /**
     * Inicializa el WebDriver basado en el browser especificado
     *
     * @param browser  Nombre del browser (chrome, firefox, edge)
     * @param headless Ejecutar en modo headless
     */
    public static void initializeDriver(String browser, boolean headless) {
        logger.info(
            "Inicializando driver para browser: {} en modo headless: {}", browser, headless);

        WebDriver webDriver;

        switch (browser.toLowerCase()) {
            case "chrome":
                webDriver = createChromeDriver(headless);
                break;
            case "firefox":
                webDriver = createFirefoxDriver(headless);
                break;
            case "edge":
                webDriver = createEdgeDriver(headless);
                break;
            default:
                logger.warn("Browser {} no reconocido, usando Chrome por defecto", browser);
                webDriver = createChromeDriver(headless);
        }

        // Configuraciones globales del driver
        webDriver.manage()
            .window()
            .maximize();
        webDriver.manage()
            .timeouts()
            .implicitlyWait(Duration.ofSeconds(
                YamlConfigReader.getImplicitWait()));
        webDriver.manage()
            .timeouts()
            .pageLoadTimeout(Duration.ofSeconds(
                YamlConfigReader.getPageLoadTimeout()));

        driver.set(webDriver);
        logger.info("Driver inicializado exitosamente");
    }

    /**
     * Crea una instancia de ChromeDriver con opciones configuradas desde YAML
     */
    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver()
            .setup();
        ChromeOptions options = new ChromeOptions();

        if (headless) {
            options.addArguments("--headless");
        }

        // Obtener opciones desde configuración YAML
        List<String> chromeOptions = YamlConfigReader.getChromeOptions();
        if (chromeOptions != null && !chromeOptions.isEmpty()) {
            chromeOptions.forEach(options::addArguments);
        } else {
            // Opciones por defecto si no están en YAML
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-popup-blocking");
            options.addArguments("--disable-notifications");
            options.addArguments("--remote-allow-origins=*");
        }

        return new ChromeDriver(options);
    }

    /**
     * Crea una instancia de FirefoxDriver con opciones configuradas desde YAML
     */
    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver()
            .setup();
        FirefoxOptions options = new FirefoxOptions();

        if (headless) {
            options.addArguments("--headless");
        }

        // Obtener preferencias desde configuración YAML
        Map<String, Object> firefoxPrefs = YamlConfigReader.getFirefoxPreferences();
        if (firefoxPrefs != null && !firefoxPrefs.isEmpty()) {
            firefoxPrefs.forEach((key, value) -> {
                if (value instanceof Boolean) {
                    options.addPreference(key, (Boolean) value);
                } else if (value instanceof Integer) {
                    options.addPreference(key, (Integer) value);
                } else if (value instanceof String) {
                    options.addPreference(key, (String) value);
                }
            });
        } else {
            // Preferencias por defecto
            options.addPreference("dom.webnotifications.enabled", false);
            options.addPreference("media.navigator.permission.disabled", true);
        }

        return new FirefoxDriver(options);
    }

    /**
     * Crea una instancia de EdgeDriver con opciones configuradas
     */
    private static WebDriver createEdgeDriver(boolean headless) {
        WebDriverManager.edgedriver()
            .setup();
        EdgeOptions options = new EdgeOptions();

        if (headless) {
            options.addArguments("--headless");
        }

        // Opciones similares a Chrome
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        return new EdgeDriver(options);
    }

    /**
     * Cierra el driver actual y limpia el ThreadLocal
     */
    public static void quitDriver() {
        WebDriver currentDriver = driver.get();
        if (currentDriver != null) {
            logger.info("Cerrando driver...");
            currentDriver.quit();
            driver.remove();
            logger.info("Driver cerrado exitosamente");
        }
    }

    /**
     * Navega a la URL especificada
     *
     * @param url URL de destino
     */
    public static void navigateToUrl(String url) {
        logger.info("Navegando a URL: {}", url);
        getDriver().get(url);
    }
}