/**
 * Created by malith on 11/7/16.
 */
package recordTool;

public abstract class FileCreator {


    public abstract String createFile(Data data, String fileName, String fileType);
    public void makeLandscape(){}
    public void setFontSize(int fontSize){}
}
