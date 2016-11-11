/**
 * Created by malith on 11/7/16.
 */

package recordTool;

public class Demo {

    // Implemented file types
    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";
    private static final String PDF = "pdf";


    public static void main(String []args) {
        // set dummy data
        Data data = new Data();

        // make RecordTool instant with file type
        DataToFileConverter dataToFileConverter = new DataToFileConverter(Demo.PDF);
        // make pdf landscape
        dataToFileConverter.makePdfLandscape();
        //set pdf font size
        dataToFileConverter.setPdfFontSize(8);

        // create file with data, save and returns path name
        String path = dataToFileConverter.createFile(data, "PhoneRecords" );

        System.out.println(path);
    }
}
