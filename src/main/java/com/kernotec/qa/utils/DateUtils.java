package com.kernotec.qa.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Clase utilitaria para manejo de fechas y tiempos.
 * Proporciona métodos para formateo, cálculos y conversiones de fechas.
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public class DateUtils {

    private static final Logger logger = LogManager.getLogger(DateUtils.class);

    // Formatos de fecha comunes
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";
    public static final String DISPLAY_DATE_FORMAT = "dd/MM/yyyy";
    public static final String DISPLAY_DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    /**
     * Obtiene la fecha actual en formato String
     */
    public static String getCurrentDate() {
        return getCurrentDate(DEFAULT_DATE_FORMAT);
    }

    /**
     * Obtiene la fecha actual en formato específico
     */
    public static String getCurrentDate(String pattern) {
        try {
            LocalDate now = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            String formattedDate = now.format(formatter);
            logger.debug("Fecha actual obtenida: {}", formattedDate);
            return formattedDate;
        } catch (Exception e) {
            logger.error("Error obteniendo fecha actual con patrón '{}': {}", pattern, e.getMessage());
            return LocalDate.now().toString();
        }
    }

    /**
     * Obtiene la fecha y hora actual en formato String
     */
    public static String getCurrentDateTime() {
        return getCurrentDateTime(DEFAULT_DATETIME_FORMAT);
    }

    /**
     * Obtiene la fecha y hora actual en formato específico
     */
    public static String getCurrentDateTime(String pattern) {
        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            String formattedDateTime = now.format(formatter);
            logger.debug("Fecha y hora actual obtenida: {}", formattedDateTime);
            return formattedDateTime;
        } catch (Exception e) {
            logger.error("Error obteniendo fecha y hora actual con patrón '{}': {}", pattern, e.getMessage());
            return LocalDateTime.now().toString();
        }
    }

    /**
     * Obtiene un timestamp único para nombres de archivo
     */
    public static String getTimestamp() {
        return getCurrentDateTime(TIMESTAMP_FORMAT);
    }

    /**
     * Obtiene una fecha futura agregando días a la fecha actual
     */
    public static String getFutureDate(int daysToAdd) {
        return getFutureDate(daysToAdd, DEFAULT_DATE_FORMAT);
    }

    /**
     * Obtiene una fecha futura agregando días a la fecha actual
     */
    public static String getFutureDate(int daysToAdd, String pattern) {
        try {
            LocalDate futureDate = LocalDate.now().plusDays(daysToAdd);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            String formattedDate = futureDate.format(formatter);
            logger.debug("Fecha futura calculada: {} ({} días)", formattedDate, daysToAdd);
            return formattedDate;
        } catch (Exception e) {
            logger.error("Error calculando fecha futura: {}", e.getMessage());
            return LocalDate.now().plusDays(daysToAdd).toString();
        }
    }

    /**
     * Obtiene una fecha pasada restando días a la fecha actual
     */
    public static String getPastDate(int daysToSubtract) {
        return getPastDate(daysToSubtract, DEFAULT_DATE_FORMAT);
    }

    /**
     * Obtiene una fecha pasada restando días a la fecha actual
     */
    public static String getPastDate(int daysToSubtract, String pattern) {
        try {
            LocalDate pastDate = LocalDate.now().minusDays(daysToSubtract);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            String formattedDate = pastDate.format(formatter);
            logger.debug("Fecha pasada calculada: {} ({} días)", formattedDate, daysToSubtract);
            return formattedDate;
        } catch (Exception e) {
            logger.error("Error calculando fecha pasada: {}", e.getMessage());
            return LocalDate.now().minusDays(daysToSubtract).toString();
        }
    }

    /**
     * Calcula la diferencia en días entre dos fechas
     */
    public static long getDaysDifference(String startDate, String endDate) {
        return getDaysDifference(startDate, endDate, DEFAULT_DATE_FORMAT);
    }

    /**
     * Calcula la diferencia en días entre dos fechas
     */
    public static long getDaysDifference(String startDate, String endDate, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);

            long difference = ChronoUnit.DAYS.between(start, end);
            logger.debug("Diferencia entre '{}' y '{}': {} días", startDate, endDate, difference);
            return difference;
        } catch (Exception e) {
            logger.error("Error calculando diferencia de días entre '{}' y '{}': {}",
                    startDate, endDate, e.getMessage());
            return 0;
        }
    }

    /**
     * Convierte una fecha de un formato a otro
     */
    public static String convertDateFormat(String dateString, String fromPattern, String toPattern) {
        try {
            DateTimeFormatter fromFormatter = DateTimeFormatter.ofPattern(fromPattern);
            DateTimeFormatter toFormatter = DateTimeFormatter.ofPattern(toPattern);

            LocalDate date = LocalDate.parse(dateString, fromFormatter);
            String convertedDate = date.format(toFormatter);

            logger.debug("Fecha convertida de '{}' a '{}': {}", dateString, toPattern, convertedDate);
            return convertedDate;
        } catch (Exception e) {
            logger.error("Error convirtiendo formato de fecha '{}': {}", dateString, e.getMessage());
            return dateString;
        }
    }

    /**
     * Verifica si una fecha es válida según el patrón especificado
     */
    public static boolean isValidDate(String dateString, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDate.parse(dateString, formatter);
            logger.debug("Fecha '{}' es válida para el patrón '{}'", dateString, pattern);
            return true;
        } catch (Exception e) {
            logger.debug("Fecha '{}' no es válida para el patrón '{}': {}", dateString, pattern, e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el mes actual en formato String
     */
    public static String getCurrentMonth() {
        return getCurrentDate("MM");
    }

    /**
     * Obtiene el año actual en formato String
     */
    public static String getCurrentYear() {
        return getCurrentDate("yyyy");
    }

    /**
     * Obtiene el trimestre actual
     */
    public static int getCurrentQuarter() {
        int month = LocalDate.now().getMonthValue();
        int quarter = (month - 1) / 3 + 1;
        logger.debug("Trimestre actual: {}", quarter);
        return quarter;
    }
}