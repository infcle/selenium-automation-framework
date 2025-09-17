package com.kernotec.qa.pages;

import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object para la página principal/home después del login
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public class HomePage extends BasePage {

    // Elementos de navegación
    @FindBy(className = "navbar")
    private WebElement navigationBar;

    @FindBy(id = "user-menu")
    private WebElement userMenu;

    @FindBy(xpath = "//a[contains(text(),'Logout') or contains(text(),'Sign Out')]")
    private WebElement logoutButton;

    @FindBy(className = "welcome-message")
    private WebElement welcomeMessage;

    @FindBy(id = "dashboard")
    private WebElement dashboardSection;

    // Enlaces de navegación principal
    @FindBy(linkText = "Dashboard")
    private WebElement dashboardLink;

    @FindBy(linkText = "Profile")
    private WebElement profileLink;

    @FindBy(linkText = "Settings")
    private WebElement settingsLink;

    @FindBy(linkText = "Reports")
    private WebElement reportsLink;

    // Elementos de contenido
    @FindBy(className = "main-content")
    private WebElement mainContent;

    @FindBy(xpath = "//div[@class='card' or @class='widget']")
    private List<WebElement> dashboardCards;

    @FindBy(className = "search-box")
    private WebElement searchBox;

    @FindBy(className = "notifications")
    private WebElement notificationsArea;

    /**
     * Constructor de HomePage
     */
    public HomePage() {
        super();
        logger.info("HomePage inicializada");
    }

    /**
     * Verifica si la página principal está desplegada correctamente
     *
     * @return true si la página está visible
     */
    @Override
    public boolean isPageDisplayed() {
        try {
            waitForPageLoad(10);
            boolean isDisplayed = isElementVisible(navigationBar, "Barra de Navegación") ||
                isElementVisible(mainContent, "Contenido Principal") ||
                isElementVisible(welcomeMessage, "Mensaje de Bienvenida");

            logger.info("HomePage desplegada: {}", isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            logger.error("Error verificando si HomePage está desplegada: {}", e.getMessage());
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
        return "HOME_PAGE";
    }

    /**
     * Obtiene el mensaje de bienvenida
     *
     * @return Texto del mensaje de bienvenida
     */
    public String getWelcomeMessage() {
        if (isElementVisible(welcomeMessage, "Mensaje de Bienvenida")) {
            return safeGetText(welcomeMessage, "Mensaje de Bienvenida");
        }
        return "";
    }

    /**
     * Hace clic en el menú de usuario
     *
     * @return Instancia actual de HomePage
     */
    public HomePage clickUserMenu() {
        safeClick(userMenu, "Menú Usuario");
        return this;
    }

    /**
     * Realiza logout de la aplicación
     *
     * @return Nueva instancia de LoginPage
     */
    public LoginPage logout() {
        logger.info("Realizando logout...");

        if (isElementVisible(userMenu, "Menú Usuario")) {
            clickUserMenu();
        }

        safeClick(logoutButton, "Botón Logout");
        waitForPageLoad(5);

        logger.info("Logout completado");
        return new LoginPage();
    }

    /**
     * Navega al Dashboard
     *
     * @return Nueva instancia de DashboardPage
     */
    public DashboardPage goToDashboard() {
        safeClick(dashboardLink, "Enlace Dashboard");
        return new DashboardPage();
    }

    /**
     * Navega a la página de perfil
     */
    public void goToProfile() {
        safeClick(profileLink, "Enlace Perfil");
        waitForPageLoad(5);
    }

    /**
     * Navega a configuraciones
     */
    public void goToSettings() {
        safeClick(settingsLink, "Enlace Configuraciones");
        waitForPageLoad(5);
    }

    /**
     * Navega a reportes
     */
    public void goToReports() {
        safeClick(reportsLink, "Enlace Reportes");
        waitForPageLoad(5);
    }

    /**
     * Realiza una búsqueda usando el campo de búsqueda
     *
     * @param searchTerm Término a buscar
     */
    public void performSearch(String searchTerm) {
        if (isElementVisible(searchBox, "Campo de Búsqueda")) {
            safeType(searchBox, searchTerm, "Campo de Búsqueda");
            searchBox.submit();
            logger.info("Búsqueda realizada con término: {}", searchTerm);
        }
    }

    /**
     * Obtiene el número de tarjetas/widgets en el dashboard
     *
     * @return Número de tarjetas
     */
    public int getDashboardCardsCount() {
        try {
            waitForPageLoad(5);
            int count = dashboardCards.size();
            logger.info("Número de tarjetas en dashboard: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("Error obteniendo conteo de tarjetas: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Verifica si hay notificaciones visibles
     *
     * @return true si hay notificaciones
     */
    public boolean hasNotifications() {
        return isElementVisible(notificationsArea, "Área de Notificaciones");
    }

    /**
     * Verifica si la barra de navegación está visible
     *
     * @return true si está visible
     */
    public boolean isNavigationBarVisible() {
        return isElementVisible(navigationBar, "Barra de Navegación");
    }

    /**
     * Verifica si el usuario está logueado (verificando elementos que solo aparecen cuando está
     * logueado)
     *
     * @return true si está logueado
     */
    public boolean isUserLoggedIn() {
        return isElementVisible(userMenu, "Menú Usuario") ||
            isElementVisible(logoutButton, "Botón Logout");
    }

    /**
     * Obtiene el texto de todas las tarjetas del dashboard
     *
     * @return Lista con el contenido de las tarjetas
     */
    public List<String> getDashboardCardsText() {
        return dashboardCards.stream()
            .map(card -> safeGetText(card, "Tarjeta Dashboard"))
            .toList();
    }

    /**
     * Verifica si un enlace específico está visible en la navegación
     *
     * @param linkText Texto del enlace
     * @return true si el enlace está visible
     */
    public boolean isNavigationLinkVisible(String linkText) {
        try {
            WebElement link = driver.findElement(org.openqa.selenium.By.linkText(linkText));
            return isElementVisible(link, "Enlace " + linkText);
        } catch (Exception e) {
            logger.warn("Enlace '{}' no encontrado: {}", linkText, e.getMessage());
            return false;
        }
    }

    /**
     * Toma una captura de la página principal
     *
     * @return Ruta del archivo de captura
     */
    public String takeHomepageScreenshot() {
        return takeScreenshot("homepage_" + System.currentTimeMillis());
    }
}