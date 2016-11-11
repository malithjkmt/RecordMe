/**
 * Created by malith on 11/7/16.
 */
package recordTool;

/**
 * Create fileCreator according to file type.. eg:- xls, xlcx, pdf, txt, json....
 */
public class FileCreatorFactory {

    public FileCreator getFileCreator(String fileType) {
        if (fileType == null) {
            return null;
        }
        if (fileType.equals("xls") || fileType.equals("xlsx")) {
            return new ExcelFileCreator();
        }
        if(fileType.equals("pdf")){
            return new PdfFileCreator();
        }
        else {
            throw new IllegalArgumentException("The specified file is not supported");
        }
    }
}
