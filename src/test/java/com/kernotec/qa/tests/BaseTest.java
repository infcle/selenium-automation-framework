package com.kernotec.qa.tests;

import com.kernotec.qa.config.ConfigReader;
import com.kernotec.qa.config.DriverManager;
import com.kernotec.qa.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Configuracion base para todos los tests E2E.
 */
public class BaseTest {

    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    @Parameters({"browser", "headless", "environment"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("chrome") String browser,
        @Optional("false") String headless,
        @Optional("test") String environment)
    {
        String resolvedBrowser = System.getProperty("browser", browser);
        String resolvedHeadless = System.getProperty("headless", headless);
        String resolvedEnvironment = System.getProperty(
            "environment",
            System.getProperty("test.environment", environment)
        );
        boolean headlessEnabled = Boolean.parseBoolean(resolvedHeadless);

        System.setProperty("test.environment", resolvedEnvironment);

        DriverManager.initializeDriver(resolvedBrowser, headlessEnabled);

        String baseUrl = ConfigReader.getBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("Base URL no configurada en config/application.yml");
        }

        DriverManager.navigateToUrl(baseUrl);
        logger.info(
            "Entorno listo. browser={}, headless={}, env={}, url={}",
            resolvedBrowser,
            headlessEnabled,
            resolvedEnvironment,
            baseUrl
        );
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE && ConfigReader.takeScreenshotOnFailure()) {
                ScreenshotUtils.takeFailureScreenshot(result.getMethod().getMethodName());
            }
        } finally {
            DriverManager.quitDriver();
        }
    }
}
