package com.kernotec.qa.tests;

import com.kernotec.qa.config.DriverManager;
import com.kernotec.qa.config.TestConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Clase base para todas las pruebas del framework. Se encarga de la configuración del WebDriver
 * antes de cada prueba y de su cierre al finalizar, asegurando un entorno de prueba limpio y
 * consistente.
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public class BaseTest {

    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    /**
     * Configura el entorno de prueba antes de cada método de prueba (@Test).
     * <p>
     * 1. Inicializa el WebDriver de forma dinámica. 2. Maximiza la ventana del navegador. 3. Navega
     * a la URL base de la aplicación.
     */
    @BeforeMethod(alwaysRun = true)
    public void setup() {
        logger.info("Iniciando la configuración del entorno de prueba...");

        // El DriverManager se inicializa y configura el navegador y los timeouts.
        DriverManager.getDriver();

        // Obtiene la URL del ambiente de prueba desde TestConfig.
        String baseUrl = TestConfig.getBaseUrl();
        if (baseUrl == null) {
            logger.error(
                "La URL base no está configurada en 'application.yml'. Terminando la prueba.");
            throw new IllegalStateException(
                "Base URL is not configured. Please check your application.yml file.");
        }

        DriverManager.navigateToUrl(baseUrl);
        logger.info("Configuración completada. Navegador listo.");
    }

    /**
     * Realiza la limpieza del entorno de prueba después de cada método de prueba (@Test).
     * <p>
     * 1. Cierra el WebDriver y finaliza la sesión del navegador.
     */
    @AfterMethod(alwaysRun = true)
    public void teardown() {
        logger.info("Iniciando la limpieza del entorno de prueba...");
        DriverManager.quitDriver();
        logger.info("Limpieza completada. Driver cerrado.");
    }
}