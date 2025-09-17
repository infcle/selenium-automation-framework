package com.kernotec.qa.pages;

import com.kernotec.qa.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import java.util.List;

/**
 * Page Object para la página de Dashboard.
 * Representa el panel de control principal después del login exitoso.
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public class DashboardPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(DashboardPage.class);

    // Elementos principales del dashboard
    @FindBy(className = "dashboard-header")
    private WebElement dashboardHeader;

    @FindBy(className = "dashboard-title")
    private WebElement dashboardTitle;

    @FindBy(className = "dashboard-content")
    private WebElement dashboardContent;

    @FindBy(xpath = "//div[@class='widget' or @class='card']")
    private List<WebElement> widgets;

    @FindBy(className = "metric-card")
    private List<WebElement> metricCards;

    @FindBy(className = "statistics-summary")
    private WebElement statisticsSummary;

    @FindBy(className = "recent-activities")
    private WebElement recentActivities;

    // Elementos de navegación del dashboard
    @FindBy(linkText = "Home")
    private WebElement homeLink;

    @FindBy(linkText = "Reports")
    private WebElement reportsLink;

    @FindBy(linkText = "Settings")
    private WebElement settingsLink;

    // Elementos de acciones
    @FindBy(id = "refresh-dashboard")
    private WebElement refreshDashboardButton;

    // Constantes para validación
    private static final String PAGE_IDENTIFIER = "DASHBOARD_PAGE";
    private static final String EXPECTED_URL_FRAGMENT = "/dashboard";

    /**
     * Constructor de DashboardPage
     */
    public DashboardPage() {
        super();
        logger.info("DashboardPage inicializada");
    }

    /**
     * Verifica si la página de dashboard está completamente cargada
     */
    @Override
    public boolean isPageDisplayed() {
        try {
            boolean isDisplayed = isElementVisible(dashboardHeader, "Encabezado del dashboard") &&
                    isElementVisible(dashboardContent, "Contenido del dashboard") &&
                    (widgets.size() > 0 || isElementVisible(statisticsSummary, "Resumen de estadísticas"));

            logger.info("Dashboard desplegado: {}", isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            logger.error("Error verificando si DashboardPage está desplegada: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el identificador único de la página
     */
    @Override
    public String getPageIdentifier() {
        return PAGE_IDENTIFIER;
    }

    /**
     * Obtiene el título del dashboard
     */
    public String getDashboardTitle() {
        String title = safeGetText(dashboardTitle, "Título del dashboard");
        logger.info("Título del dashboard: {}", title);
        return title;
    }

    /**
     * Obtiene el número de widgets visibles en el dashboard
     */
    public int getWidgetsCount() {
        int count = widgets.size();
        logger.info("Número de widgets en dashboard: {}", count);
        return count;
    }

    /**
     * Obtiene el número de tarjetas de métricas
     */
    public int getMetricCardsCount() {
        int count = metricCards.size();
        logger.debug("Número de tarjetas de métricas: {}", count);
        return count;
    }

    /**
     * Navega de vuelta a la página home
     */
    public HomePage navigateToHome() {
        logger.info("Navegando a Home desde Dashboard");
        safeClick(homeLink, "Enlace Home");
        waitForPageLoad(ConfigReader.getPageLoadTimeout());
        return new HomePage();
    }

    /**
     * Navega a la sección de reportes
     */
    public void navigateToReports() {
        logger.info("Navegando a Reports desde Dashboard");
        safeClick(reportsLink, "Enlace Reports");
        waitForPageLoad(ConfigReader.getPageLoadTimeout());
    }

    /**
     * Navega a configuraciones
     */
    public void navigateToSettings() {
        logger.info("Navegando a Settings desde Dashboard");
        safeClick(settingsLink, "Enlace Settings");
        waitForPageLoad(ConfigReader.getPageLoadTimeout());
    }

    /**
     * Refresca el dashboard
     */
    public void refreshDashboard() {
        logger.info("Refrescando dashboard");
        if (isElementVisible(refreshDashboardButton, "Botón refrescar dashboard")) {
            safeClick(refreshDashboardButton, "Botón refrescar dashboard");
            waitForPageLoad(ConfigReader.getPageLoadTimeout());
        } else {
            refreshPage();
        }
    }

    /**
     * Verifica si el resumen de estadísticas está visible
     */
    public boolean isStatisticsSummaryVisible() {
        boolean isVisible = isElementVisible(statisticsSummary, "Resumen de estadísticas");
        logger.debug("Resumen de estadísticas visible: {}", isVisible);
        return isVisible;
    }

    /**
     * Verifica si las actividades recientes están visibles
     */
    public boolean isRecentActivitiesVisible() {
        boolean isVisible = isElementVisible(recentActivities, "Actividades recientes");
        logger.debug("Actividades recientes visibles: {}", isVisible);
        return isVisible;
    }

    /**
     * Valida que el dashboard esté correctamente cargado
     */
    public void validateDashboard() {
        logger.info("Validando dashboard");

        Assert.assertTrue(isPageDisplayed(), "El dashboard no está cargado correctamente");
        Assert.assertTrue(getCurrentUrl().contains(EXPECTED_URL_FRAGMENT),
                "No se está en la página de dashboard correcta");

        Assert.assertTrue(getWidgetsCount() > 0 || isElementVisible(dashboardContent, "Contenido del dashboard"),
                "El dashboard no tiene contenido visible");

        logger.info("Dashboard validado exitosamente");
    }

    /**
     * Obtiene información resumida del dashboard
     */
    public String getDashboardSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Dashboard Summary:\n");
        summary.append("- Widgets: ").append(getWidgetsCount()).append("\n");
        summary.append("- Metric Cards: ").append(getMetricCardsCount()).append("\n");
        summary.append("- Statistics Visible: ").append(isStatisticsSummaryVisible()).append("\n");
        summary.append("- Recent Activities Visible: ").append(isRecentActivitiesVisible()).append("\n");

        String summaryText = summary.toString();
        logger.info("Resumen del dashboard:\n{}", summaryText);
        return summaryText;
    }
}