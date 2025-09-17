package com.kernotec.qa.data;

import com.kernotec.qa.config.ConfigReader;
import com.kernotec.qa.utils.ExcelUtils;
import com.kernotec.qa.utils.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Clase para gestión de credenciales de usuarios de prueba.
 * Proporciona métodos para obtener y validar credenciales desde diferentes
 * fuentes.
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public class UserCredentials {

    private static final Logger logger = LogManager.getLogger(UserCredentials.class);

    // Roles de usuario predefinidos
    public enum UserRole {
        ADMIN("admin"),
        TESTUSER("testuser"),
        READONLY("readonly"),
        MANAGER("manager"),
        USER("user");

        private final String roleName;

        UserRole(String roleName) {
            this.roleName = roleName;
        }

        public String getRoleName() {
            return roleName;
        }
    }

    // Credenciales por defecto para testing
    private static final Map<UserRole, CredentialData> DEFAULT_CREDENTIALS = new HashMap<>();

    static {
        DEFAULT_CREDENTIALS.put(UserRole.ADMIN, new CredentialData(
                "admin@kernotec.com", "Admin123!", "Administrator",
                new String[] { "read", "write", "delete", "admin" }));

        DEFAULT_CREDENTIALS.put(UserRole.TESTUSER, new CredentialData(
                "testuser@kernotec.com", "Test123!", "Test User", new String[] { "read", "write" }));

        DEFAULT_CREDENTIALS.put(UserRole.READONLY, new CredentialData(
                "readonly@kernotec.com", "ReadOnly123!", "Read Only User", new String[] { "read" }));

        DEFAULT_CREDENTIALS.put(UserRole.MANAGER, new CredentialData(
                "manager@kernotec.com", "Manager123!", "Manager", new String[] { "read", "write", "manage" }));

        DEFAULT_CREDENTIALS.put(UserRole.USER, new CredentialData(
                "user@kernotec.com", "User123!", "Regular User", new String[] { "read" }));
    }

    /**
     * Obtiene credenciales para un rol específico
     *
     * @param role Rol del usuario
     * @return CredentialData con las credenciales
     */
    public static CredentialData getCredentials(UserRole role) {
        logger.info("Obteniendo credenciales para rol: {}", role.getRoleName());

        // Primero intentar obtener desde configuración YAML
        String username = ConfigReader.getUsername(role.getRoleName());
        String password = ConfigReader.getPassword(role.getRoleName());

        if (username != null && password != null) {
            logger.debug("Credenciales obtenidas desde configuración YAML");
            return new CredentialData(username, password, role.getRoleName(), getDefaultPermissions(role));
        }

        // Si no están en configuración, usar las por defecto
        CredentialData defaultCreds = DEFAULT_CREDENTIALS.get(role);
        if (defaultCreds != null) {
            logger.debug("Usando credenciales por defecto para rol: {}", role.getRoleName());
            return defaultCreds;
        }

        logger.warn("No se encontraron credenciales para el rol: {}", role.getRoleName());
        return null;
    }

    /**
     * Obtiene credenciales para un rol específico por nombre
     *
     * @param roleName Nombre del rol
     * @return CredentialData con las credenciales
     */
    public static CredentialData getCredentials(String roleName) {
        try {
            UserRole role = UserRole.valueOf(roleName.toUpperCase());
            return getCredentials(role);
        } catch (IllegalArgumentException e) {
            logger.error("Rol no válido: {}", roleName);
            return null;
        }
    }

    /**
     * Obtiene credenciales desde archivo Excel
     *
     * @param role Rol del usuario
     * @return CredentialData con las credenciales
     */
    public static CredentialData getCredentialsFromExcel(UserRole role) {
        logger.info("Obteniendo credenciales desde Excel para rol: {}", role.getRoleName());

        try {
            String excelPath = ConfigReader.getString("testing.dataProvider.path") + "users.xlsx";
            List<Map<String, String>> userData = ExcelUtils.readExcelData(excelPath, "Users");

            for (Map<String, String> user : userData) {
                String userRole = user.get("Role");
                if (role.getRoleName().equalsIgnoreCase(userRole)) {
                    String username = user.get("Username");
                    String password = user.get("Password");
                    String displayName = user.get("DisplayName");

                    logger.debug("Credenciales encontradas en Excel para rol: {}", role.getRoleName());
                    return new CredentialData(username, password, displayName, getDefaultPermissions(role));
                }
            }

            logger.warn("No se encontraron credenciales en Excel para el rol: {}", role.getRoleName());
            return null;

        } catch (Exception e) {
            logger.error("Error leyendo credenciales desde Excel: {}", e.getMessage());
            return getCredentials(role); // Fallback a credenciales por defecto
        }
    }

    /**
     * Obtiene todas las credenciales disponibles
     *
     * @return Mapa con todas las credenciales
     */
    public static Map<UserRole, CredentialData> getAllCredentials() {
        logger.info("Obteniendo todas las credenciales disponibles");

        Map<UserRole, CredentialData> allCredentials = new HashMap<>();

        for (UserRole role : UserRole.values()) {
            CredentialData creds = getCredentials(role);
            if (creds != null) {
                allCredentials.put(role, creds);
            }
        }

        logger.info("Se obtuvieron {} conjuntos de credenciales", allCredentials.size());
        return allCredentials;
    }

    /**
     * Valida credenciales
     *
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return true si las credenciales son válidas
     */
    public static boolean validateCredentials(String username, String password) {
        logger.info("Validando credenciales para usuario: {}", username);

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            logger.warn("Credenciales vacías o nulas");
            return false;
        }

        // Validar formato básico
        if (username.length() < 3 || password.length() < 6) {
            logger.warn("Credenciales no cumplen longitud mínima");
            return false;
        }

        // Verificar si las credenciales coinciden con algún rol conocido
        for (UserRole role : UserRole.values()) {
            CredentialData creds = getCredentials(role);
            if (creds != null && creds.getUsername().equals(username) && creds.getPassword().equals(password)) {
                logger.info("Credenciales válidas para rol: {}", role.getRoleName());
                return true;
            }
        }

        logger.warn("Credenciales no válidas para usuario: {}", username);
        return false;
    }

    /**
     * Obtiene el rol de usuario basado en las credenciales
     *
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Rol del usuario o null si no es válido
     */
    public static UserRole getUserRole(String username, String password) {
        logger.info("Determinando rol para usuario: {}", username);

        for (UserRole role : UserRole.values()) {
            CredentialData creds = getCredentials(role);
            if (creds != null && creds.getUsername().equals(username) && creds.getPassword().equals(password)) {
                logger.info("Rol determinado: {}", role.getRoleName());
                return role;
            }
        }

        logger.warn("No se pudo determinar el rol para usuario: {}", username);
        return null;
    }

    /**
     * Genera credenciales temporales para testing
     *
     * @param role Rol base para las credenciales
     * @return CredentialData con credenciales temporales
     */
    public static CredentialData generateTemporaryCredentials(UserRole role) {
        String timestamp = DateUtils.getTimestamp();
        String tempUsername = role.getRoleName() + "_temp_" + timestamp;
        String tempPassword = "TempPass123!" + timestamp.substring(8); // Últimos dígitos del timestamp

        logger.info("Generando credenciales temporales para rol: {}", role.getRoleName());

        return new CredentialData(
                tempUsername,
                tempPassword,
                "Temporary " + role.getRoleName(),
                getDefaultPermissions(role));
    }

    /**
     * Obtiene permisos por defecto para un rol
     *
     * @param role Rol del usuario
     * @return Array de permisos
     */
    private static String[] getDefaultPermissions(UserRole role) {
        switch (role) {
            case ADMIN:
                return new String[] { "read", "write", "delete", "admin", "manage" };
            case MANAGER:
                return new String[] { "read", "write", "manage" };
            case TESTUSER:
                return new String[] { "read", "write", "test" };
            case USER:
                return new String[] { "read" };
            case READONLY:
                return new String[] { "read" };
            default:
                return new String[] { "read" };
        }
    }

    /**
     * Clase para encapsular datos de credenciales
     */
    public static class CredentialData {
        private final String username;
        private final String password;
        private final String displayName;
        private final String[] permissions;

        public CredentialData(String username, String password, String displayName, String[] permissions) {
            this.username = username;
            this.password = password;
            this.displayName = displayName;
            this.permissions = permissions;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String[] getPermissions() {
            return permissions;
        }

        public boolean hasPermission(String permission) {
            for (String perm : permissions) {
                if (perm.equals(permission)) {
                    return true;
                }
            }
            return false;
        }

        public String getMaskedPassword() {
            if (password == null || password.length() < 4) {
                return "****";
            }
            return password.substring(0, 2) + "****" + password.substring(password.length() - 2);
        }

        @Override
        public String toString() {
            return String.format("CredentialData{username='%s', displayName='%s', permissions=%d}",
                    username, displayName, permissions.length);
        }
    }

    /**
     * Obtiene información resumida de todas las credenciales
     *
     * @return Información resumida
     */
    public static String getCredentialsSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== CREDENCIALES DISPONIBLES ===\n");

        for (UserRole role : UserRole.values()) {
            CredentialData creds = getCredentials(role);
            if (creds != null) {
                summary.append(String.format("- %s: %s (%s)\n",
                        role.getRoleName(), creds.getUsername(), creds.getDisplayName()));
            }
        }

        String summaryText = summary.toString();
        logger.info("Resumen de credenciales:\n{}", summaryText);
        return summaryText;
    }
}
