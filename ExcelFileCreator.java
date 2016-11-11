package recordTool;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Create and write Excel file in Java using Apache POI
 */
public class ExcelFileCreator extends FileCreator {

    /**
     * Create and save Excel File
     * @param data
     * @param fileName
     * @param fileType xls or xlsx
     * @return absolute file path of the saved Excel file
     */
    @Override
    public String createFile(Data data, String fileName, String fileType) {
        WorkbookFactory workbookFactory = new WorkbookFactory();

        try {
            ExcelWorkBook excelWorkbook = workbookFactory.getWorkbook(fileType);
            Workbook workbook = excelWorkbook.getWorkbook();
            int maxRowLimit = excelWorkbook.getMaxRowsLimit(); // use this to split records in to sheets...

            Sheet sheet = workbook.createSheet();

            // start with first row
            int rowCount = 0;

            // headers row
            Row headers = sheet.createRow(rowCount++);

            // start with first column
            int headerCount = 0;

            // fill the headers
            for (String field : data.getHeaders()) {
                Cell cell = headers.createCell(headerCount++);
                cell.setCellValue(field);
            }

            // fill the data rows
            for (String[] aRow : data.getRows()){

                // jump to a new sheet if maximum row limit reached
                if(rowCount>=15){
                    sheet = workbook.createSheet();
                    // start with first row
                    rowCount = 0;
                    // headers row
                    headers = sheet.createRow(rowCount++);
                    // start with first column
                    headerCount = 0;
                    // fill the headers
                    for (String field : data.getHeaders()) {
                        Cell cell = headers.createCell(headerCount++);
                        cell.setCellValue(field);
                    }
                }

                Row row = sheet.createRow(rowCount++);
                int columnCount = 0;
                for (String field : aRow) {
                    Cell cell = row.createCell(columnCount++);
                    cell.setCellValue(field);
                }

            }

            // Create a temp file
            File temp = File.createTempFile(fileName, "." + fileType, null);

            // Write file
            FileOutputStream outputStream = new FileOutputStream(temp);
            workbook.write(outputStream);

            outputStream.close();
            return temp.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}



/*
    .XLS   - version prior to 2007 e.g. MS Office 2000 and 2003. Row limit is 65,536 and 256 columns
    .XLSX  - Office 2007 onwards e.g. MS Office 2010 and 2013. 1,048,576 rows  and 16,384 columns

    Using Apache POI...

    HSSF (Horrible SpreadSheet Format)     - Microsoft Excel (XLS) format files.  >> HSSFWorkBook
    XSSF (XML SpreadSheet Format)          - Open Office XML (XLSX) format files. >> XSSFWorkBook

*/