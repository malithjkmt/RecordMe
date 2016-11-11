package recordTool;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by malith on 11/8/16.
 */
public class PdfFileCreator extends FileCreator {

    // Table configurations
    final float CELL_MARGIN=5f;
    final int MARGIN = 20;
    final int MARGIN_TOP = 10;
    final PDType1Font CONTENT_FONT = PDType1Font.HELVETICA;
    final PDType1Font HEADER_FONT = PDType1Font.HELVETICA_BOLD;

    private boolean isLandscape;
    private int fontSize = 12;

    /**
     * makes pdf document orientation landscape
     */
    public void makeLandscape() {
        this.isLandscape = true;
    }

    /**
     * set font size
     * @param fontSize
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * Draw line between two points
     * @param contentStream
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @throws IOException
     */
    private void drawLine(PDPageContentStream contentStream, float startX, float startY, float endX, float endY) throws IOException {

        // Move the current position to the given coordinates.
        contentStream.moveTo(startX, startY);
        // Draw a line from the current position to the given coordinates.
        contentStream.lineTo(endX, endY);
        // set stroke width
        contentStream.setLineWidth(1/5);
        // Stroke the path.
        contentStream.stroke();
    }

    /**
     * create PDF page according to the page orientation
     * @return
     */
    private PDPage createPage(){
        if(isLandscape){
            return new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
        }
        return new PDPage();
    }

    /**
     * create and save pdf document
     * @param data
     * @param fileName
     * @param fileType
     * @return
     */
    @Override
    public String createFile(Data data, String fileName, String fileType) {

        // create empty PDF Document
        PDDocument document = new PDDocument();
        String[][] content = data.getRows();
        String[] headers = data.getHeaders();

        // create empty page
        PDPage page = this.createPage();
        // add page to document
        document.addPage(page);

        float rowHeight = fontSize*5/3;
        float cellTopMargin = fontSize*4/3;
        float initialY = page.getMediaBox().getHeight() - MARGIN_TOP ;
        int cols = content[0].length;
        float tableWidth = page.getMediaBox().getWidth() - MARGIN*2;
        float colWidth = tableWidth/(float)cols;
        int wrappingLength = (int)(colWidth/(CONTENT_FONT.getAverageFontWidth()*fontSize/1000)); // number of characters (with spaces)
        float textX = MARGIN+CELL_MARGIN;
        float textY = initialY-cellTopMargin;

        try {
            // prepare content stream
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // draw header top line
            drawLine(contentStream, MARGIN, initialY, MARGIN + tableWidth, initialY);

            // draw headers
            contentStream.setFont( HEADER_FONT , fontSize );
            for(int h = 0; h < headers.length; h++){
                contentStream.beginText();
                contentStream.newLineAtOffset(textX, textY);
                contentStream.showText(headers[h]);
                contentStream.endText();
                textX += colWidth;
            }
            contentStream.setFont( CONTENT_FONT , fontSize );

            textX = MARGIN+CELL_MARGIN;
            textY-=rowHeight;
            // draw header bottom line
            drawLine(contentStream, MARGIN, textY + cellTopMargin, MARGIN + tableWidth, textY + cellTopMargin);

            // iterate through rows
            for(int i = 0; i < content.length; i++){
                float maxRowHeight = 0;

                // iterate through columns and check if any cell in this row overflows the page (this is an overhead)
                for(int j = 0 ; j < content[i].length; j++) {
                    String text = content[i][j];
                    String[] cellLines = null;

                    // wrap overflowing cell contents
                    cellLines = WordUtils.wrap(text, wrappingLength).split("\\r?\\n");

                    // add and jump in to a new page if the page is going to overflow
                    if(textY < cellLines.length*cellTopMargin){
                        // draw columns of current page
                        float nextX = MARGIN;
                        for (int p = 0; p <= cols; p++) {
                            drawLine(contentStream, nextX, initialY, nextX, textY + cellTopMargin);
                            nextX += colWidth;
                        }
                        contentStream.close();
                        // create empty page
                        page = this.createPage();
                        // add page to document
                        document.addPage(page);
                        // prepare content stream
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont( CONTENT_FONT , fontSize );
                        textX = MARGIN+CELL_MARGIN;
                        textY = initialY-cellTopMargin;
                        drawLine(contentStream, MARGIN, initialY, MARGIN + tableWidth, initialY);
                    }

                }

                // iterate through columns and fill the cells
                for(int j = 0 ; j < content[i].length; j++){
                    String text = content[i][j];
                    String[] cellLines = null;
                    String cellLineValue = null;

                    // wrap overflowing cell contents
                    cellLines = WordUtils.wrap(text, wrappingLength).split("\\r?\\n");

                    int cellLineNo = 0;
                    for (; cellLineNo< cellLines.length; cellLineNo++) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(textX, textY-cellLineNo*cellTopMargin);
                        cellLineValue = cellLines[cellLineNo];

                        // check if the cell content is larger that the cell width
                        float textWidth = CONTENT_FONT.getStringWidth(cellLineValue) / 1000 * fontSize;
                        if(textWidth >= colWidth){
                            throw new IllegalArgumentException("Font size is too large!! ");
                        }

                        contentStream.showText(cellLineValue);
                        contentStream.endText();
                    }

                    float cellHeight = (cellLineNo-1)*cellTopMargin;

                    if (cellHeight > maxRowHeight){
                        maxRowHeight = cellHeight;
                    }
                    textX += colWidth;

                }
                textY-=(rowHeight+ maxRowHeight);

                // draw raw bottom line
                drawLine(contentStream, MARGIN, textY + cellTopMargin, MARGIN + tableWidth, textY + cellTopMargin);

                textX = MARGIN+CELL_MARGIN;
            }

            // draw columns
            float nextX = MARGIN;
            for (int i = 0; i <= cols; i++) {
                drawLine(contentStream, nextX, initialY, nextX, textY + cellTopMargin);
                nextX += colWidth;
            }
            contentStream.close();

            // Create a temp file
            File temp = File.createTempFile(fileName, "." + fileType, null);

            // Write file
            FileOutputStream outputStream = new FileOutputStream(temp);
            document.save(outputStream);

            outputStream.close();
            return temp.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (StringIndexOutOfBoundsException e){
            System.out.println("Font size is not valid to generate the table! "+ e);
            e.printStackTrace();
        }
        return null;
    }
}
