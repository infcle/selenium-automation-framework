package com.kernotec.qa.utils;

import com.kernotec.qa.config.ConfigReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Clase utilitaria para manejo de archivos Excel. Proporciona métodos para leer, escribir y
 * manipular datos en archivos .xlsx.
 *
 * @author QA Team Kernotec
 * @version 1.0
 */
public class ExcelUtils {

    private static final Logger logger = LogManager.getLogger(ExcelUtils.class);

    /**
     * Lee datos de un archivo Excel y los retorna como lista de mapas
     */
    public static List<Map<String, String>> readExcelData(String filePath, String sheetName) {
        List<Map<String, String>> dataList = new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(filePath);
            Workbook workbook = WorkbookFactory.create(fileInputStream))
        {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                logger.error("Hoja '{}' no encontrada en el archivo: {}", sheetName, filePath);
                return dataList;
            }

            // Obtener encabezados de la primera fila
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                logger.warn("No se encontraron encabezados en la hoja: {}", sheetName);
                return dataList;
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValueAsString(cell));
            }

            // Leer datos de las filas siguientes
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Map<String, String> rowData = new HashMap<>();

                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j);
                        String value = getCellValueAsString(cell);
                        rowData.put(headers.get(j), value);
                    }

                    dataList.add(rowData);
                }
            }

            logger.info(
                "Datos leídos exitosamente: {} filas desde '{}'", dataList.size(), filePath);

        } catch (IOException e) {
            logger.error("Error leyendo archivo Excel '{}': {}", filePath, e.getMessage());
            throw new RuntimeException("Error leyendo archivo Excel", e);
        }

        return dataList;
    }

    /**
     * Lee datos de la primera hoja de un archivo Excel
     */
    public static List<Map<String, String>> readExcelData(String filePath) {
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
            Workbook workbook = WorkbookFactory.create(fileInputStream))
        {

            Sheet sheet = workbook.getSheetAt(0);
            return readExcelData(filePath, sheet.getSheetName());

        } catch (IOException e) {
            logger.error("Error leyendo archivo Excel '{}': {}", filePath, e.getMessage());
            throw new RuntimeException("Error leyendo archivo Excel", e);
        }
    }

    /**
     * Escribe datos en un archivo Excel
     */
    public static void writeExcelData(String filePath, String sheetName,
        List<Map<String, String>> data)
    {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet(sheetName);

            if (data.isEmpty()) {
                logger.warn("No hay datos para escribir en '{}'", filePath);
                return;
            }

            // Escribir encabezados
            Row headerRow = sheet.createRow(0);
            Map<String, String> firstRow = data.get(0);
            List<String> headers = new ArrayList<>(firstRow.keySet());

            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
            }

            // Escribir datos
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Map<String, String> rowData = data.get(i);

                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.createCell(j);
                    String value = rowData.get(headers.get(j));
                    cell.setCellValue(value != null ? value : "");
                }
            }

            // Autoajustar columnas
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Crear directorio si no existe
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Escribir archivo
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                workbook.write(fileOutputStream);
            }

            logger.info("Datos escritos exitosamente: {} filas en '{}'", data.size(), filePath);

        } catch (IOException e) {
            logger.error("Error escribiendo archivo Excel '{}': {}", filePath, e.getMessage());
            throw new RuntimeException("Error escribiendo archivo Excel", e);
        }
    }

    /**
     * Obtiene el valor de una celda como String
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                double numericValue = cell.getNumericCellValue();
                if (numericValue == Math.floor(numericValue)) {
                    return String.valueOf((long) numericValue);
                } else {
                    return String.valueOf(numericValue);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue();
                }
            case BLANK:
            default:
                return "";
        }
    }

    /**
     * Lee datos de test desde el archivo de configuración por defecto CORREGIDO: Usa la nueva
     * estructura de configuración
     */
    public static List<Map<String, String>> readTestData(String sheetName) {
        String testDataPath = ConfigReader.getString(
            "testData.path", "src/test/resources/testdata/");
        String fileName = ConfigReader.getString("testData.files.userCredentials", "users.xlsx");
        String fullPath = testDataPath + fileName;
        return readExcelData(fullPath, sheetName);
    }

    /**
     * Lee datos específicos de usuarios para testing
     */
    public static List<Map<String, String>> readUserTestData() {
        return readTestData("Users");
    }

    /**
     * Lee datos específicos de escenarios de test
     */
    public static List<Map<String, String>> readScenarioTestData(String sheetName) {
        String testDataPath = ConfigReader.getString(
            "testData.path", "src/test/resources/testdata/");
        return readExcelData(testDataPath + "test-scenarios.xlsx", sheetName);
    }
}