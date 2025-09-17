package com.kernotec.qa.data;

import com.kernotec.qa.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;


/**
 * Clase para proveer datos de test a los métodos de testing Centraliza la gestión de datos de
 * prueba
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public class TestDataProvider {

    private static final Logger logger = LogManager.getLogger(TestDataProvider.class);

    // Credenciales válidas - En producción estos deberían venir de archivos seguros
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "admin123";

    /**
     * Obtiene el usuario válido para tests
     *
     * @return Username válido
     */
    public static String getValidUsername() {
        // Usar configuración YAML
        String username = UserCredentials.getCredentials(UserCredentials.UserRole.ADMIN)
            .getUsername();
        if (username == null) {
            username = VALID_USERNAME;
        }
        logger.debug("Usuario válido obtenido: {}", username);
        return username;
    }

    /**
     * Obtiene la contraseña válida para tests
     *
     * @return Password válido
     */
    public static String getValidPassword() {
        // Usar configuración YAML
        String password = UserCredentials.getCredentials(UserCredentials.UserRole.ADMIN)
            .getPassword();
        if (password == null) {
            password = VALID_PASSWORD;
        }
        logger.debug("Contraseña válida obtenida (longitud: {})", password.length());
        return password;
    }

    /**
     * Obtiene credenciales de usuario administrador
     *
     * @return Array con [username, password]
     */
    public static String[] getAdminCredentials() {
        UserCredentials.CredentialData adminCreds = UserCredentials.getCredentials(
            UserCredentials.UserRole.ADMIN);
        return new String[]{
            adminCreds.getUsername(),
            adminCreds.getPassword()
        };
    }

    /**
     * Obtiene credenciales de usuario regular
     *
     * @return Array con [username, password]
     */
    public static String[] getRegularUserCredentials() {
        UserCredentials.CredentialData testUserCreds = UserCredentials
            .getCredentials(UserCredentials.UserRole.TESTUSER);
        return new String[]{
            testUserCreds.getUsername(),
            testUserCreds.getPassword()
        };
    }

    /**
     * Data Provider para credenciales inválidas Provee múltiples combinaciones de credenciales
     * incorrectas
     */
    @DataProvider(name = "invalidCredentials")
    public static Object[][] getInvalidCredentials() {
        logger.info("Generando datos de credenciales inválidas");

        return new Object[][]{
            // {username, password, expected_error_fragment}
            {"", "", "required"}, // Campos vacíos
            {"admin", "", "password"}, // Contraseña vacía
            {"", "admin123", "username"}, // Usuario vacío
            {"invalid_user", "admin123", "invalid"}, // Usuario inválido
            {"admin", "wrong_pass", "invalid"}, // Contraseña incorrecta
            {"user@domain.com", "123456", "invalid"}, // Email como usuario
            {"admin123", "admin123", "invalid"}, // Usuario con números
            {"admin", "123", "invalid"}, // Contraseña muy corta
            {"admin", "ADMIN123", "invalid"}, // Contraseña en mayúsculas
            {"ADMIN", "admin123", "invalid"}, // Usuario en mayúsculas
            {"admin ", "admin123", "invalid"}, // Usuario con espacios
            {"admin", " admin123", "invalid"}, // Contraseña con espacios
            {"special!@#", "admin123", "invalid"}, // Usuario con caracteres especiales
            {"admin", "special!@#", "invalid"}, // Contraseña con caracteres especiales
            {"very_long_username_that_exceeds_normal_limits", "admin123", "invalid"},
            // Usuario muy largo
            {"admin", "very_long_password_that_exceeds_normal_character_limits_for_testing",
                "invalid"} // Contraseña muy larga
        };
    }

    /**
     * Data Provider para datos de búsqueda
     */
    @DataProvider(name = "searchTerms")
    public static Object[][] getSearchTerms() {
        return new Object[][]{
            {"Java", true}, // Término válido que debería encontrar resultados
            {"Selenium", true}, // Término técnico válido
            {"Testing", true}, // Término relacionado con QA
            {"xyzabc123", false}, // Término que no debería encontrar resultados
            {"", false}, // Búsqueda vacía
            {"@#$%^", false}, // Caracteres especiales
            {"123456", false}, // Solo números
            {"a", false}, // Una sola letra
            {"test automation framework", true} // Frase completa
        };
    }

    /**
     * Data Provider para diferentes browsers
     */
    @DataProvider(name = "browsers")
    public static Object[][] getBrowsers() {
        return new Object[][]{
            {"chrome"},
            {"firefox"},
            {"edge"}
        };
    }

    /**
     * Data Provider para diferentes resoluciones de pantalla
     */
    @DataProvider(name = "screenResolutions")
    public static Object[][] getScreenResolutions() {
        return new Object[][]{
            {1920, 1080}, // Full HD
            {1366, 768}, // Laptop común
            {1280, 720}, // HD
            {1024, 768}, // Resolución antigua
            {360, 640} // Mobile portrait
        };
    }

    /**
     * Data Provider para datos de registro de usuario
     */
    @DataProvider(name = "userRegistrationData")
    public static Object[][] getUserRegistrationData() {
        long timestamp = System.currentTimeMillis();

        return new Object[][]{
            // {firstName, lastName, email, username, password, expectedResult}
            {"Juan", "Pérez", "juan.perez" + timestamp + "@test.com", "jperez" + timestamp,
                "Password123!", true},
            {"María", "González", "maria.gonzalez" + timestamp + "@test.com",
                "mgonzalez" + timestamp, "SecurePass456!", true},
            {"Carlos", "López", "carlos.lopez" + timestamp + "@test.com", "clopez" + timestamp,
                "MyPassword789!", true},
            {"", "Apellido", "email@test.com", "username", "Password123!", false}, // Nombre vacío
            {"Nombre", "", "email@test.com", "username", "Password123!", false}, // Apellido vacío
            {"Nombre", "Apellido", "", "username", "Password123!", false}, // Email vacío
            {"Nombre", "Apellido", "email@test.com", "", "Password123!", false}, // Username vacío
            {"Nombre", "Apellido", "email@test.com", "username", "", false}, // Password vacía
            {"Nombre", "Apellido", "invalid-email", "username", "Password123!", false},
            // Email inválido
            {"Nombre", "Apellido", "email@test.com", "user", "123", false} // Password muy corta
        };
    }

    /**
     * Data Provider para testing de formularios
     */
    @DataProvider(name = "formValidationData")
    public static Object[][] getFormValidationData() {
        return new Object[][]{
            // {fieldName, value, isValid, expectedMessage}
            {"email", "valid@email.com", true, ""},
            {"email", "invalid-email", false, "Please enter a valid email"},
            {"email", "", false, "Email is required"},
            {"phone", "123-456-7890", true, ""},
            {"phone", "invalid-phone", false, "Please enter a valid phone number"},
            {"phone", "", false, "Phone number is required"},
            {"zipcode", "12345", true, ""},
            {"zipcode", "1234", false, "Zip code must be 5 digits"},
            {"zipcode", "abcde", false, "Zip code must contain only numbers"}
        };
    }

    /**
     * Genera datos únicos de test basados en timestamp
     *
     * @param prefix Prefijo para el dato
     * @return Dato único
     */
    public static String generateUniqueData(String prefix) {
        return prefix + "_" + System.currentTimeMillis();
    }

    /**
     * Genera email único para testing
     *
     * @param prefix Prefijo del email
     * @return Email único
     */
    public static String generateUniqueEmail(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "@test.kernotec.com";
    }

    /**
     * Genera username único para testing
     *
     * @param prefix Prefijo del username
     * @return Username único
     */
    public static String generateUniqueUsername(String prefix) {
        return (prefix + "_" + System.currentTimeMillis()).toLowerCase();
    }

    /**
     * Obtiene datos específicos según el ambiente de testing
     *
     * @param environment Ambiente (dev, test, prod)
     * @return Datos específicos del ambiente
     */
    public static TestEnvironmentData getEnvironmentData(String environment) {
        switch (environment.toLowerCase()) {
            case "dev":
                return new TestEnvironmentData(
                    "dev_user",
                    "dev_pass123",
                    "https://dev.kernotec.com",
                    "Development Environment"
                );
            case "test":
                return new TestEnvironmentData(
                    "test_user",
                    "test_pass123",
                    "https://test.kernotec.com",
                    "Test Environment"
                );
            case "staging":
                return new TestEnvironmentData(
                    "staging_user",
                    "staging_pass123",
                    "https://staging.kernotec.com",
                    "Staging Environment"
                );
            default:
                logger.warn("Ambiente desconocido '{}', usando datos por defecto", environment);
                return new TestEnvironmentData(
                    getValidUsername(),
                    getValidPassword(),
                    ConfigReader.getBaseUrl(),
                    "Default Environment"
                );
        }
    }

    /**
     * Clase interna para datos específicos del ambiente
     */
    public static class TestEnvironmentData {

        private final String username;
        private final String password;
        private final String baseUrl;
        private final String description;

        public TestEnvironmentData(String username, String password, String baseUrl,
            String description)
        {
            this.username = username;
            this.password = password;
            this.baseUrl = baseUrl;
            this.description = description;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public String getDescription() {
            return description;
        }
    }
}