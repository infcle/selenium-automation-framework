package com.kernotec.qa;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Author: ecoronel Created on: 16/09/2025 Project: Default (Template) Project File: ${NAME}.java
 * Version: 1.0 Last modified: 16/09/2025 Description:
 */
public class Main {

    public static void main(String[] args) {

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.google.com");

        WebElement searchBox = driver.findElement(By.name("q"));
        searchBox.sendKeys("Selenium con Java");
        searchBox.submit();

        WebElement result = driver.findElement(By.id("search"));
        assert  result.isDisplayed();

        driver.quit();
    }
}