package com.kernotec.qa.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object para la página de Login Contiene todos los elementos y acciones relacionadas con
 * autenticación
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public class LoginPage extends BasePage {

    // Elementos de la página de login usando @FindBy
    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(xpath = "//button[@type='submit' or @id='login-button']")
    private WebElement loginButton;

    @FindBy(className = "error-message")
    private WebElement errorMessage;

    @FindBy(linkText = "Forgot Password?")
    private WebElement forgotPasswordLink;

    @FindBy(id = "remember-me")
    private WebElement rememberMeCheckbox;

    @FindBy(className = "login-form")
    private WebElement loginForm;

    @FindBy(xpath = "//h1[contains(text(),'Login') or contains(text(),'Sign In')]")
    private WebElement loginTitle;

    /**
     * Constructor de LoginPage
     */
    public LoginPage() {
        super();
        logger.info("LoginPage inicializada");
    }

    /**
     * Verifica si la página de login está desplegada correctamente
     *
     * @return true si la página está visible
     */
    @Override
    public boolean isPageDisplayed() {
        try {
            boolean isDisplayed = isElementVisible(loginForm, "Formulario de Login") &&
                isElementVisible(usernameField, "Campo Usuario") &&
                isElementVisible(passwordField, "Campo Contraseña") &&
                isElementVisible(loginButton, "Botón Login");
            logger.info("Página de Login desplegada: {}", isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            logger.error("Error verificando si LoginPage está desplegada: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el identificador único de la página
     *
     * @return Identificador de la página
     */
    @Override
    public String getPageIdentifier() {
        return "LOGIN_PAGE";
    }

    /**
     * Ingresa el nombre de usuario
     *
     * @param username Nombre de usuario
     * @return Instancia actual de LoginPage para method chaining
     */
    public LoginPage enterUsername(String username) {
        safeType(usernameField, username, "Campo Usuario");
        return this;
    }

    /**
     * Ingresa la contraseña
     *
     * @param password Contraseña
     * @return Instancia actual de LoginPage para method chaining
     */
    public LoginPage enterPassword(String password) {
        safeType(passwordField, password, "Campo Contraseña");
        return this;
    }

    /**
     * Hace clic en el botón de login
     *
     * @return Nueva instancia de HomePage después del login exitoso
     */
    public HomePage clickLoginButton() {
        safeClick(loginButton, "Botón Login");
        waitForPageLoad(5);
        return new HomePage();
    }

    /**
     * Realiza el proceso completo de login
     *
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Nueva instancia de HomePage
     */
    public HomePage login(String username, String password) {
        logger.info("Realizando login con usuario: {}", username);
        enterUsername(username)
            .enterPassword(password);
        return clickLoginButton();
    }

    /**
     * Realiza login con checkbox "Recordarme"
     *
     * @param username   Nombre de usuario
     * @param password   Contraseña
     * @param rememberMe true para marcar "recordarme"
     * @return Nueva instancia de HomePage
     */
    public HomePage loginWithRememberMe(String username, String password, boolean rememberMe) {
        enterUsername(username)
            .enterPassword(password);

        if (rememberMe) {
            toggleRememberMe();
        }

        return clickLoginButton();
    }

    /**
     * Marca/desmarca el checkbox "Recordarme"
     *
     * @return Instancia actual de LoginPage
     */
    public LoginPage toggleRememberMe() {
        if (isElementVisible(rememberMeCheckbox, "Checkbox Recordarme")) {
            safeClick(rememberMeCheckbox, "Checkbox Recordarme");
        }
        return this;
    }

    /**
     * Hace clic en el enlace "Olvidé mi contraseña"
     *
     * @return Nueva página de recuperación de contraseña
     */
    public void clickForgotPasswordLink() {
        safeClick(forgotPasswordLink, "Enlace Olvidé Contraseña");
    }

    /**
     * Obtiene el mensaje de error mostrado
     *
     * @return Texto del mensaje de error
     */
    public String getErrorMessage() {
        if (isElementVisible(errorMessage, "Mensaje de Error")) {
            return safeGetText(errorMessage, "Mensaje de Error");
        }
        return "";
    }

    /**
     * Verifica si hay un mensaje de error visible
     *
     * @return true si hay error visible
     */
    public boolean isErrorMessageDisplayed() {
        return isElementVisible(errorMessage, "Mensaje de Error");
    }

    /**
     * Obtiene el título de la página de login
     *
     * @return Título de la página
     */
    public String getLoginTitle() {
        if (isElementVisible(loginTitle, "Título Login")) {
            return safeGetText(loginTitle, "Título Login");
        }
        return getPageTitle();
    }

    /**
     * Limpia todos los campos del formulario
     *
     * @return Instancia actual de LoginPage
     */
    public LoginPage clearAllFields() {
        if (isElementVisible(usernameField, "Campo Usuario")) {
            usernameField.clear();
        }
        if (isElementVisible(passwordField, "Campo Contraseña")) {
            passwordField.clear();
        }
        logger.info("Campos de login limpiados");
        return this;
    }

    /**
     * Verifica si el botón de login está habilitado
     *
     * @return true si está habilitado
     */
    public boolean isLoginButtonEnabled() {
        try {
            waitForElementVisible(loginButton, "Botón Login");
            boolean isEnabled = loginButton.isEnabled();
            logger.info("Botón Login habilitado: {}", isEnabled);
            return isEnabled;
        } catch (Exception e) {
            logger.error("Error verificando estado del botón login: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el placeholder del campo usuario
     *
     * @return Placeholder del campo usuario
     */
    public String getUsernamePlaceholder() {
        return usernameField.getAttribute("placeholder");
    }

    /**
     * Obtiene el placeholder del campo contraseña
     *
     * @return Placeholder del campo contraseña
     */
    public String getPasswordPlaceholder() {
        return passwordField.getAttribute("placeholder");
    }

    /**
     * Verifica si el campo usuario está vacío
     *
     * @return true si está vacío
     */
    public boolean isUsernameFieldEmpty() {
        return usernameField.getAttribute("value")
            .isEmpty();
    }

    /**
     * Verifica si el campo contraseña está vacío
     *
     * @return true si está vacío
     */
    public boolean isPasswordFieldEmpty() {
        return passwordField.getAttribute("value")
            .isEmpty();
    }
}