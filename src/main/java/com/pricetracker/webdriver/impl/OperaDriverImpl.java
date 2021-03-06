package com.pricetracker.webdriver.impl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;

import com.pricetracker.core.WebDriverConst;
import com.pricetracker.webdriver.IWebDriver;

public class OperaDriverImpl implements IWebDriver {
    @Override
    public WebDriver execute() {
        System.setProperty("webdriver.opera.driver", WebDriverConst.OPERA_PATH);
        System.out.println("webdriver.opera.driver: " + System.getProperty("webdriver.opera.driver"));

        OperaOptions operaOptions = new OperaOptions();

        OperaDriver webDriver = new OperaDriver(operaOptions);

        webDriver.manage().window().maximize();

        return webDriver;
    }
}
