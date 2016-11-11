package recordTool;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;


public class WorkbookFactory {

    /**
     *
     * @param excelFileType xls or xlsx
     * @return ExcelWorkBook object with XSSFWorkbook or HSSFWorkbook and maximum rowsLimit fields
     * @throws IOException
     */
    public ExcelWorkBook getWorkbook(String excelFileType)throws IOException {
        ExcelWorkBook workbook = null;

        if (excelFileType.equals("xlsx")) {
            workbook = new ExcelWorkBook(new XSSFWorkbook(), 1048576);
        } else if (excelFileType.equals("xls")) {
            workbook = new ExcelWorkBook(new HSSFWorkbook(), 65536);
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }

        return workbook;
    }
}
