package com.kernotec.qa.utils;

import com.kernotec.qa.config.ConfigReader;
import com.kernotec.qa.config.DriverManager;
import java.time.Duration;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Clase utilitaria para operaciones comunes con WebDriver.
 * Proporciona métodos para waits, interacciones, navegación y validaciones.
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public class WebDriverUtils {

    private static final Logger logger = LogManager.getLogger(WebDriverUtils.class);
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Actions actions;
    private final JavascriptExecutor jsExecutor;

    public WebDriverUtils() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    // Métodos de espera (Waits)
    // =========================

    /**
     * Espera a que un elemento sea visible
     */
    public WebElement waitForElementToBeVisible(WebElement element) {
        try {
            return wait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            logger.error("Timeout esperando visibilidad del elemento: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Espera a que un elemento sea clickeable
     */
    public WebElement waitForElementToBeClickable(WebElement element) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            logger.error("Timeout esperando que el elemento sea clickeable: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Espera a que un elemento sea clickeable por locator
     */
    public WebElement waitForElementToBeClickable(By locator) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (Exception e) {
            logger.error("Timeout esperando que el elemento {} sea clickeable: {}", locator, e.getMessage());
            throw e;
        }
    }

    /**
     * Espera a que un elemento contenga texto específico
     */
    public boolean waitForTextToBePresent(WebElement element, String text) {
        try {
            return wait.until(ExpectedConditions.textToBePresentInElement(element, text));
        } catch (Exception e) {
            logger.error("Timeout esperando texto '{}' en elemento: {}", text, e.getMessage());
            return false;
        }
    }

    /**
     * Espera a que un elemento sea invisible
     */
    public boolean waitForElementToBeInvisible(WebElement element) {
        try {
            return wait.until(ExpectedConditions.invisibilityOf(element));
        } catch (Exception e) {
            logger.error("Timeout esperando que el elemento sea invisible: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Espera a que la página se cargue completamente
     */
    public void waitForPageLoad(int timeoutSeconds) {
        try {
            WebDriverWait pageWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            pageWait.until(webDriver -> 
                jsExecutor.executeScript("return document.readyState").equals("complete"));
            logger.debug("Página cargada completamente");
        } catch (Exception e) {
            logger.warn("Timeout esperando carga de página: {}", e.getMessage());
        }
    }

    /**
     * Espera a que una URL contenga texto específico
     */
    public boolean waitForUrlToContain(String urlFragment) {
        try {
            return wait.until(ExpectedConditions.urlContains(urlFragment));
        } catch (Exception e) {
            logger.error("Timeout esperando URL que contenga '{}': {}", urlFragment, e.getMessage());
            return false;
        }
    }

    /**
     * Espera a que el título de la página contenga texto específico
     */
    public boolean waitForTitleToContain(String title) {
        try {
            return wait.until(ExpectedConditions.titleContains(title));
        } catch (Exception e) {
            logger.error("Timeout esperando título que contenga '{}': {}", title, e.getMessage());
            return false;
        }
    }

    // Métodos de verificación
    // =======================

    /**
     * Verifica si un elemento está visible
     */
    public boolean isElementVisible(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            logger.debug("Elemento no está visible: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si un elemento está presente en el DOM
     */
    public boolean isElementPresent(WebElement element) {
        try {
            return element != null;
        } catch (Exception e) {
            logger.debug("Elemento no está presente: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si un elemento está habilitado
     */
    public boolean isElementEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (Exception e) {
            logger.debug("Elemento no está habilitado: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si un elemento está seleccionado
     */
    public boolean isElementSelected(WebElement element) {
        try {
            return element.isSelected();
        } catch (Exception e) {
            logger.debug("Elemento no está seleccionado: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si la página está completamente cargada
     */
    public boolean isPageLoaded() {
        try {
            String readyState = (String) jsExecutor.executeScript("return document.readyState");
            return "complete".equals(readyState);
        } catch (Exception e) {
            logger.error("Error verificando estado de la página: {}", e.getMessage());
            return false;
        }
    }

    // Métodos de interacción
    // ======================

    /**
     * Hace clic en un elemento usando JavaScript
     */
    public void clickWithJavaScript(WebElement element) {
        try {
            jsExecutor.executeScript("arguments[0].click();", element);
            logger.info("Clic realizado con JavaScript");
        } catch (Exception e) {
            logger.error("Error haciendo clic con JavaScript: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Ingresa texto usando JavaScript
     */
    public void typeWithJavaScript(WebElement element, String text) {
        try {
            jsExecutor.executeScript("arguments[0].value = arguments[1];", element, text);
            logger.info("Texto ingresado con JavaScript");
        } catch (Exception e) {
            logger.error("Error ingresando texto con JavaScript: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Desplaza hasta un elemento
     */
    public void scrollToElement(WebElement element) {
        try {
            jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
            logger.debug("Desplazado hasta elemento");
        } catch (Exception e) {
            logger.error("Error desplazando hasta elemento: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Desplaza hasta la parte superior de la página
     */
    public void scrollToTop() {
        try {
            jsExecutor.executeScript("window.scrollTo(0, 0);");
            logger.debug("Desplazado hasta la parte superior");
        } catch (Exception e) {
            logger.error("Error desplazando hacia arriba: {}", e.getMessage());
        }
    }

    /**
     * Desplaza hasta la parte inferior de la página
     */
    public void scrollToBottom() {
        try {
            jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            logger.debug("Desplazado hasta la parte inferior");
        } catch (Exception e) {
            logger.error("Error desplazando hacia abajo: {}", e.getMessage());
        }
    }

    /**
     * Desplaza una cantidad específica de píxeles
     */
    public void scrollBy(int x, int y) {
        try {
            jsExecutor.executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
            logger.debug("Desplazado {} píxeles horizontalmente y {} verticalmente", x, y);
        } catch (Exception e) {
            logger.error("Error en desplazamiento: {}", e.getMessage());
        }
    }

    // Métodos de acciones con mouse
    // =============================

    /**
     * Hace doble clic en un elemento
     */
    public void doubleClick(WebElement element) {
        try {
            actions.doubleClick(element).perform();
            logger.info("Doble clic realizado");
        } catch (Exception e) {
            logger.error("Error en doble clic: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Hace clic derecho en un elemento
     */
    public void rightClick(WebElement element) {
        try {
            actions.contextClick(element).perform();
            logger.info("Clic derecho realizado");
        } catch (Exception e) {
            logger.error("Error en clic derecho: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Mantiene presionado un elemento
     */
    public void clickAndHold(WebElement element) {
        try {
            actions.clickAndHold(element).perform();
            logger.info("Clic y mantener presionado realizado");
        } catch (Exception e) {
            logger.error("Error en clic y mantener: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Arrastra y suelta un elemento
     */
    public void dragAndDrop(WebElement source, WebElement target) {
        try {
            actions.dragAndDrop(source, target).perform();
            logger.info("Arrastrar y soltar realizado");
        } catch (Exception e) {
            logger.error("Error en arrastrar y soltar: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Mueve el mouse a un elemento
     */
    public void moveToElement(WebElement element) {
        try {
            actions.moveToElement(element).perform();
            logger.debug("Mouse movido al elemento");
        } catch (Exception e) {
            logger.error("Error moviendo mouse: {}", e.getMessage());
            throw e;
        }
    }

    // Métodos de select y dropdowns
    // =============================

    /**
     * Selecciona una opción por texto visible
     */
    public void selectByVisibleText(WebElement selectElement, String visibleText) {
        try {
            Select select = new Select(selectElement);
            select.selectByVisibleText(visibleText);
            logger.info("Opción '{}' seleccionada por texto visible", visibleText);
        } catch (Exception e) {
            logger.error("Error seleccionando por texto visible '{}': {}", visibleText, e.getMessage());
            throw e;
        }
    }

    /**
     * Selecciona una opción por valor
     */
    public void selectByValue(WebElement selectElement, String value) {
        try {
            Select select = new Select(selectElement);
            select.selectByValue(value);
            logger.info("Opción con valor '{}' seleccionada", value);
        } catch (Exception e) {
            logger.error("Error seleccionando por valor '{}': {}", value, e.getMessage());
            throw e;
        }
    }

    /**
     * Selecciona una opción por índice
     */
    public void selectByIndex(WebElement selectElement, int index) {
        try {
            Select select = new Select(selectElement);
            select.selectByIndex(index);
            logger.info("Opción en índice {} seleccionada", index);
        } catch (Exception e) {
            logger.error("Error seleccionando por índice {}: {}", index, e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene todas las opciones de un select
     */
    public List<WebElement> getAllOptions(WebElement selectElement) {
        try {
            Select select = new Select(selectElement);
            return select.getOptions();
        } catch (Exception e) {
            logger.error("Error obteniendo opciones del select: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene la opción seleccionada
     */
    public WebElement getSelectedOption(WebElement selectElement) {
        try {
            Select select = new Select(selectElement);
            return select.getFirstSelectedOption();
        } catch (Exception e) {
            logger.error("Error obteniendo opción seleccionada: {}", e.getMessage());
            throw e;
        }
    }

    // Métodos de alertas
    // ==================

    /**
     * Acepta una alerta
     */
    public void acceptAlert() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            logger.info("Alerta aceptada");
        } catch (Exception e) {
            logger.error("Error aceptando alerta: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Cancela una alerta
     */
    public void dismissAlert() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.dismiss();
            logger.info("Alerta cancelada");
        } catch (Exception e) {
            logger.error("Error cancelando alerta: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene el texto de una alerta
     */
    public String getAlertText() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String text = alert.getText();
            logger.info("Texto de alerta obtenido: {}", text);
            return text;
        } catch (Exception e) {
            logger.error("Error obteniendo texto de alerta: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Envía texto a una alerta
     */
    public void sendTextToAlert(String text) {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.sendKeys(text);
            logger.info("Texto enviado a alerta");
        } catch (Exception e) {
            logger.error("Error enviando texto a alerta: {}", e.getMessage());
            throw e;
        }
    }

    // Métodos de captura de pantalla
    // ==============================

    /**
     * Toma una captura de pantalla
     */
    public String takeScreenshot(String screenshotName) {
        return ScreenshotUtils.takeScreenshot(screenshotName);
    }

    // Métodos de JavaScript
    // =====================

    /**
     * Ejecuta JavaScript y retorna el resultado
     */
    public Object executeJavaScript(String script, Object... args) {
        try {
            Object result = jsExecutor.executeScript(script, args);
            logger.debug("JavaScript ejecutado: {}", script);
            return result;
        } catch (Exception e) {
            logger.error("Error ejecutando JavaScript: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Ejecuta JavaScript asíncrono
     */
    public Object executeAsyncJavaScript(String script, Object... args) {
        try {
            Object result = jsExecutor.executeAsyncScript(script, args);
            logger.debug("JavaScript asíncrono ejecutado: {}", script);
            return result;
        } catch (Exception e) {
            logger.error("Error ejecutando JavaScript asíncrono: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene el atributo de un elemento usando JavaScript
     */
    public String getAttributeWithJavaScript(WebElement element, String attributeName) {
        try {
            String value = (String) jsExecutor.executeScript(
                "return arguments[0].getAttribute(arguments[1]);", element, attributeName);
            logger.debug("Atributo '{}' obtenido: {}", attributeName, value);
            return value;
        } catch (Exception e) {
            logger.error("Error obteniendo atributo con JavaScript: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Verifica si un elemento está presente usando JavaScript
     */
    public boolean isElementPresentWithJavaScript(String cssSelector) {
        try {
            Boolean isPresent = (Boolean) jsExecutor.executeScript(
                "return document.querySelector(arguments[0]) !== null;", cssSelector);
            return isPresent != null ? isPresent : false;
        } catch (Exception e) {
            logger.error("Error verificando presencia con JavaScript: {}", e.getMessage());
            return false;
        }
    }
}
}