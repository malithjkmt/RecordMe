/**
 * Created by malith on 11/10/16.
 */
package recordTool;

import org.apache.poi.ss.usermodel.Workbook;

public class ExcelWorkBook {
    private Workbook workbook;
    private int maxRowsLimit;

    public ExcelWorkBook(Workbook workbook, int maxRowsLimit) {
        this.workbook = workbook;
        this.maxRowsLimit = maxRowsLimit;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public int getMaxRowsLimit() {
        return maxRowsLimit;
    }
}
