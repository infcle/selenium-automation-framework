package com.kernotec.qa.tests;

import com.kernotec.qa.config.DriverManager;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SmokeTests extends BaseTest {

    @Test(groups = {"smoke"})
    public void baseUrlShouldLoad() {
        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        String title = DriverManager.getDriver().getTitle();

        Assert.assertTrue(currentUrl.startsWith("http"), "La URL cargada no es valida");
        Assert.assertFalse(title == null || title.isBlank(), "La pagina debe tener titulo");
    }
}
