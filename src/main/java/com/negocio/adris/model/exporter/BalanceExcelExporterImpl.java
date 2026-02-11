package com.negocio.adris.model.exporter;

import com.negocio.adris.model.entities.BalanceDiario;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BalanceExcelExporterImpl implements BalanceExcelExporter {

    @Override
    public void exportar(Map<YearMonth, List<BalanceDiario>> datos) {

        if (datos == null || datos.isEmpty()) {
            throw new IllegalArgumentException("No hay datos para exportar");
        }

        try (Workbook workbook = new XSSFWorkbook()) {

            CellStyle headerStyle = crearHeaderStyle(workbook);
            CellStyle currencyStyle = crearCurrencyStyle(workbook);
            CellStyle totalStyle = crearTotalStyle(workbook);
            CellStyle dateStyle = crearDateStyle(workbook);

            Integer anio = null;

            for (Map.Entry<YearMonth, List<BalanceDiario>> entry : datos.entrySet()) {

                YearMonth yearMonth = entry.getKey();
                List<BalanceDiario> balances = entry.getValue();

                if (anio == null) {
                    anio = yearMonth.getYear();
                }

                String mesNombre = yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()).toUpperCase();

                Sheet sheet = workbook.createSheet(mesNombre);

                crearEncabezados(sheet, headerStyle);
                cargarDatos(sheet, balances, currencyStyle, totalStyle, dateStyle);
                SizeColumnas(sheet);
            }

            String home = System.getProperty("user.home");

            Path ruta = Path.of(home, "balance_" + anio + ".xlsx");

            try (OutputStream os = Files.newOutputStream(ruta)) {
                workbook.write(os);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al exportar Excel", e);
        }
    }

    private void crearEncabezados(Sheet sheet, CellStyle headerStyle) {
        Row header = sheet.createRow(2);

        crearCelda(header, 2, "Fecha", headerStyle);
        crearCelda(header, 3, "Facturación", headerStyle);
        crearCelda(header, 4, "Egresos", headerStyle);
        crearCelda(header, 5, "Diferencia", headerStyle);

    }

    private void cargarDatos(Sheet sheet, List<BalanceDiario> balances, CellStyle currencyStyle, CellStyle totalStyle, CellStyle dateStyle) {
        int filaIndex = 3;

        for (BalanceDiario balance : balances) {

            Row fila = sheet.createRow(filaIndex++);

            Cell fechaCell = fila.createCell(2);
            fechaCell.setCellValue(java.sql.Date.valueOf(balance.getFecha()));
            fechaCell.setCellStyle(dateStyle);

            crearCeldaNumerica(fila, 3, balance.getFacturacion(), currencyStyle);
            crearCeldaNumerica(fila, 4, balance.getEgresos(), currencyStyle);
            crearCeldaConFormulaDif(fila, 5, filaIndex, currencyStyle);
        }

        crearFilaTotal(sheet, filaIndex, totalStyle);
    }

    private void crearFilaTotal(Sheet sheet, int filaIndex, CellStyle totalStyle) {
        Row total = sheet.createRow(filaIndex);
        total.setHeightInPoints(25);

        int startRow = 4; // primera fila de datos en Excel
        int endRow = filaIndex; // última fila de datos

        crearCelda(total, 2, "TOTAL", totalStyle);

        Cell totalFact = total.createCell(3);
        totalFact.setCellFormula("SUM(D" + startRow + ":D" + endRow + ")");
        totalFact.setCellStyle(totalStyle);

        Cell totalEgr = total.createCell(4);
        totalEgr.setCellFormula("SUM(E" + startRow + ":E" + endRow + ")");
        totalEgr.setCellStyle(totalStyle);

        Cell totalDiff = total.createCell(5);
        totalDiff.setCellFormula("D" + (filaIndex + 1) + "-E" + (filaIndex + 1));
        totalDiff.setCellStyle(totalStyle);

    }


    // Estilos
    private CellStyle crearHeaderStyle(Workbook workbook){
        Font font = workbook.createFont();
        font.setBold(true);

        CellStyle style = crearBordeStyle(workbook);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }

    private CellStyle crearCurrencyStyle(Workbook workbook){
        CellStyle style = crearBordeStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("\"$\" #,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);

        return style;
    }

    private CellStyle crearTotalStyle(Workbook workbook){
        Font font = workbook.createFont();
        font.setBold(true);

        CellStyle style = crearBordeStyle(workbook);
        style.setFont(font);

        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("\"$\" #,##0.00" ));
        style.setAlignment(HorizontalAlignment.RIGHT);

        return style;
    }

    private CellStyle crearDateStyle(Workbook workbook) {
        CellStyle style = crearBordeStyle(workbook);
        CreationHelper helper = workbook.getCreationHelper();
        style.setDataFormat(helper.createDataFormat().getFormat("dd/MM/yyyy"));


        return style;
    }

    private CellStyle crearBordeStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    // Helpers
    private static void SizeColumnas(Sheet sheet) {
        sheet.setColumnWidth(2, 5000); // Fecha
        sheet.setColumnWidth(3, 6000); // Facturación
        sheet.setColumnWidth(4, 6000); // Egresos
        sheet.setColumnWidth(5, 6000); // Diferencia
    }

    private void crearCelda(Row fila, int col, String value, CellStyle style){
        Cell cell = fila.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void crearCeldaNumerica(Row fila, int col, BigDecimal value, CellStyle style){
        Cell cell = fila.createCell(col);
        cell.setCellValue(value.doubleValue());
        cell.setCellStyle(style);
    }

    private void crearCeldaConFormulaDif(Row fila, int col, int filaIndex, CellStyle style){
        Cell diffCell = fila.createCell(col);
        diffCell.setCellFormula("D" + (filaIndex) + "-E" + (filaIndex));
        diffCell.setCellStyle(style);
    }
}
