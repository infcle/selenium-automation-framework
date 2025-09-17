package com.kernotec.qa.pages;

import com.kernotec.qa.config.ConfigReader;
import com.kernotec.qa.config.DriverManager;
import com.kernotec.qa.utils.WebDriverUtils;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Clase base para todos los Page Objects Contiene funcionalidades comunes y métodos utilitarios
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public abstract class BasePage {

    protected static final Logger logger = LogManager.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected WebDriverUtils webDriverUtils;

    /**
     * Constructor que inicializa el driver y PageFactory
     */
    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        this.webDriverUtils = new WebDriverUtils();
        PageFactory.initElements(driver, this);
        logger.debug(
            "Página {} inicializada", this.getClass()
                .getSimpleName());
    }

    /**
     * Obtiene el título de la página actual
     *
     * @return Título de la página
     */
    public String getPageTitle() {
        String title = driver.getTitle();
        logger.info("Título de la página: {}", title);
        return title;
    }

    /**
     * Obtiene la URL actual
     *
     * @return URL actual
     */
    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        logger.info("URL actual: {}", url);
        return url;
    }

    /**
     * Verifica si la página está completamente cargada
     *
     * @return true si la página está cargada
     */
    public boolean isPageLoaded() {
        return webDriverUtils.isPageLoaded();
    }

    /**
     * Espera a que la página se cargue completamente
     *
     * @param timeoutSeconds Timeout en segundos
     */
    public void waitForPageLoad(int timeoutSeconds) {
        webDriverUtils.waitForPageLoad(timeoutSeconds);
        logger.info("Página cargada completamente");
    }

    /**
     * Hace clic en un elemento de forma segura
     *
     * @param element     Elemento a hacer clic
     * @param elementName Nombre descriptivo del elemento para logs
     */
    protected void safeClick(WebElement element, String elementName) {
        try {
            webDriverUtils.waitForElementToBeClickable(element);
            element.click();
            logger.info("Clic exitoso en elemento: {}", elementName);
        } catch (Exception e) {
            logger.error("Error haciendo clic en {}: {}", elementName, e.getMessage());
            throw new RuntimeException("No se pudo hacer clic en " + elementName, e);
        }
    }

    /**
     * Ingresa texto en un campo de forma segura
     *
     * @param element   Campo de texto
     * @param text      Texto a ingresar
     * @param fieldName Nombre descriptivo del campo
     */
    protected void safeType(WebElement element, String text, String fieldName) {
        try {
            webDriverUtils.waitForElementToBeVisible(element);
            element.clear();
            element.sendKeys(text);
            logger.info("Texto ingresado en campo {}: {}", fieldName,
                text.replaceAll(".", "*")
            ); // Enmascarar texto sensible en logs
        } catch (Exception e) {
            logger.error("Error ingresando texto en {}: {}", fieldName, e.getMessage());
            throw new RuntimeException("No se pudo ingresar texto en " + fieldName, e);
        }
    }

    /**
     * Obtiene el texto de un elemento de forma segura
     *
     * @param element     Elemento del cual obtener texto
     * @param elementName Nombre descriptivo del elemento
     * @return Texto del elemento
     */
    protected String safeGetText(WebElement element, String elementName) {
        try {
            webDriverUtils.waitForElementToBeVisible(element);
            String text = element.getText();
            logger.info("Texto obtenido de {}: {}", elementName, text);
            return text;
        } catch (Exception e) {
            logger.error("Error obteniendo texto de {}: {}", elementName, e.getMessage());
            throw new RuntimeException("No se pudo obtener texto de " + elementName, e);
        }
    }

    /**
     * Verifica si un elemento está visible
     *
     * @param element     Elemento a verificar
     * @param elementName Nombre descriptivo del elemento
     * @return true si está visible
     */
    protected boolean isElementVisible(WebElement element, String elementName) {
        try {
            boolean isVisible = webDriverUtils.isElementVisible(element);
            logger.info("Elemento {} está visible: {}", elementName, isVisible);
            return isVisible;
        } catch (Exception e) {
            logger.warn("Error verificando visibilidad de {}: {}", elementName, e.getMessage());
            return false;
        }
    }

    /**
     * Espera a que un elemento sea visible
     *
     * @param element     Elemento a esperar
     * @param elementName Nombre descriptivo del elemento
     * @return true si el elemento se vuelve visible
     */
    protected boolean waitForElementVisible(WebElement element, String elementName) {
        try {
            webDriverUtils.waitForElementToBeVisible(element);
            logger.info("Elemento {} ahora es visible", elementName);
            return true;
        } catch (Exception e) {
            logger.error("Timeout esperando visibilidad de {}: {}", elementName, e.getMessage());
            return false;
        }
    }

    /**
     * Espera a que un elemento sea clickeable
     *
     * @param element     Elemento a esperar
     * @param elementName Nombre descriptivo del elemento
     * @return true si el elemento se vuelve clickeable
     */
    protected boolean waitForElementClickable(WebElement element, String elementName) {
        try {
            webDriverUtils.waitForElementToBeClickable(element);
            logger.info("Elemento {} ahora es clickeable", elementName);
            return true;
        } catch (Exception e) {
            logger.error(
                "Timeout esperando que {} sea clickeable: {}", elementName, e.getMessage());
            return false;
        }
    }

    /**
     * Desplaza la página hasta un elemento
     *
     * @param element     Elemento hasta el cual desplazar
     * @param elementName Nombre descriptivo del elemento
     */
    protected void scrollToElement(WebElement element, String elementName) {
        try {
            webDriverUtils.scrollToElement(element);
            logger.info("Desplazado hasta elemento: {}", elementName);
        } catch (Exception e) {
            logger.error("Error desplazando hasta {}: {}", elementName, e.getMessage());
        }
    }

    /**
     * Toma una captura de pantalla de la página actual
     *
     * @param screenshotName Nombre para la captura
     * @return Ruta del archivo de captura
     */
    protected String takeScreenshot(String screenshotName) {
        return webDriverUtils.takeScreenshot(screenshotName);
    }

    /**
     * Actualiza la página actual
     */
    protected void refreshPage() {
        driver.navigate()
            .refresh();
        waitForPageLoad(ConfigReader.getPageLoadTimeout());
        logger.info("Página actualizada");
    }

    /**
     * Navega hacia atrás en el historial
     */
    protected void navigateBack() {
        driver.navigate()
            .back();
        waitForPageLoad(ConfigReader.getPageLoadTimeout());
        logger.info("Navegado hacia atrás");
    }

    /**
     * Navega hacia adelante en el historial
     */
    protected void navigateForward() {
        driver.navigate()
            .forward();
        waitForPageLoad(ConfigReader.getPageLoadTimeout());
        logger.info("Navegado hacia adelante");
    }

    /**
     * Método abstracto que cada página debe implementar para verificar que está cargada
     *
     * @return true si la página está cargada correctamente
     */
    public abstract boolean isPageDisplayed();

    /**
     * Método abstracto para obtener el identificador único de la página
     *
     * @return Identificador único de la página
     */
    public abstract String getPageIdentifier();
}