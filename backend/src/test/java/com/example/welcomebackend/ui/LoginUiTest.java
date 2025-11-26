package com.example.welcomebackend.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba UI sencilla con Selenium sobre el frontend Angular.
 *
 * Requisitos:
 * - Tener Google Chrome (o Chromium) instalado.
 * - Tener el frontend Angular ejecutándose en http://localhost:4200
 *   (por ejemplo con `npm start` o `ng serve` dentro de la carpeta frontend).
 *
 * Está deshabilitada por defecto para que `mvn test` no falle en entornos sin frontend.
 */
@Disabled("Habilita este test cuando tengas el frontend y Chrome disponibles")
public class LoginUiTest {

    private WebDriver driver;

    @BeforeAll
    static void setupClass() {
        // Descarga y configura automáticamente el driver de Chrome
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        // Modo headless para que pueda ejecutarse en CI o sin abrir ventana
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void loginPageLoads() {
        // Ajusta la URL si tu ruta de login es distinta
        driver.get("http://localhost:4200/login");

        // Ejemplo: comprobamos que existe el botón de login o un título
        WebElement body = driver.findElement(By.tagName("body"));
        String text = body.getText().toLowerCase();

        assertTrue(text.contains("login") || text.contains("ingresar") || text.contains("iniciar sesión"),
                "La página de login no parece haberse cargado correctamente");
    }

    @Test
    void loginFormValidationShowsErrorWhenEmpty() {
        driver.get("http://localhost:4200/login");

        // Click en el botón de login sin llenar los campos
        WebElement loginButton = driver.findElement(By.cssSelector("button.login-btn"));
        loginButton.click();

        // Esperamos a que aparezca el mensaje de error
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".error"))
        );

        String errorText = error.getText();
        assertTrue(errorText.toLowerCase().contains("por favor completa todos los campos"),
                "No se mostró el mensaje de validación esperado");
    }

    @Test
    void registerButtonNavigatesToRegister() {
        driver.get("http://localhost:4200/login");

        WebElement registerButton = driver.findElement(By.cssSelector("button.register-btn"));
        registerButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        boolean navigated = wait.until(d ->
                d.getCurrentUrl().toLowerCase().contains("/register")
        );

        assertTrue(navigated, "La navegación a la página de registro no se realizó correctamente");
    }
}


