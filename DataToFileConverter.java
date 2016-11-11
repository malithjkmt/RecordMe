/**
 * Created by malith on 11/7/16.
 */
package recordTool;

public class DataToFileConverter {
    private FileCreator fileCreator;
    private String fileType;

    public DataToFileConverter(String fileType) {
        this.fileType = fileType;
        this.fileCreator = new FileCreatorFactory().getFileCreator(fileType);
    }

    // step 1
    public String createFile(Data data, String fileName){
        return fileCreator.createFile(data, fileName, fileType);
    }

    public void makePdfLandscape(){
        fileCreator.makeLandscape();
    }

    public void setPdfFontSize(int fontSize){
        fileCreator.setFontSize(fontSize);
    }

    // step 2
    public void registerForClickEvent(){

    }
}
