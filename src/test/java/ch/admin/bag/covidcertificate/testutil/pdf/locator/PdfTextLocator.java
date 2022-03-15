package ch.admin.bag.covidcertificate.testutil.pdf.locator;

import lombok.Getter;
import org.apache.commons.collections4.list.TreeList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.text.TextPosition;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PdfTextLocator implements IPdfTextListener {
    private static final float LINE_WIDTH = 0.5f;

    private final List<TextPosition> textPositions = new TreeList<>();
    private final StringBuilder stringBuilder = new StringBuilder();
    private final String filePath;
    private final String fileName;
    private final float pageHeight;
    private final PDDocument document;
    private final PDPage pdfPage;
    private PDPageContentStream contentStream;


    private float correction = 0.1f;

    public PdfTextLocator(String filePath, String fileName, int pageNumber) throws Exception {
        this(PDDocument.load(new File(filePath, fileName)), filePath, fileName, pageNumber);
    }

    public PdfTextLocator(byte[] pdfInputStream, String filePath, String fileName, int pageNumber) throws Exception {
        this(PDDocument.load(pdfInputStream), filePath, fileName, pageNumber);
    }

    private PdfTextLocator(PDDocument document, String filePath, String fileName, int pageNumber) throws Exception {
        this.document = document;

        this.filePath = filePath;
        this.fileName = fileName;

        validatePageNumber(pageNumber);
        int zeroBasedPageNumber = pageNumber - 1;

        pdfPage = document.getPage(zeroBasedPageNumber);

        Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());

        PDFTextPositionsAppender pdfTextPositionsAppender = new PDFTextPositionsAppender(this);
        pdfTextPositionsAppender.setSortByPosition(true);
        pdfTextPositionsAppender.setStartPage(zeroBasedPageNumber);
        pdfTextPositionsAppender.setEndPage(zeroBasedPageNumber + 1);
        pdfTextPositionsAppender.writeText(document, dummy);

        validateAllPageHeightAreEquals();
        this.pageHeight = textPositions.stream().findAny().orElseThrow().getPageHeight();
    }

    public LocalisationDataGroup getLocalisation(String text) {
        int indexCursor = 0;
        List<LocalisationData> localisationDataList = new ArrayList<>();

        while (indexCursor != -1) {
            indexCursor = stringBuilder.toString().indexOf(text, indexCursor);
            if (indexCursor == -1) continue;
            List<TextPosition> textPositionGroup = textPositions.subList(indexCursor, indexCursor + text.length());
            localisationDataList.add(getLocalisation(textPositionGroup));
            indexCursor += text.length();
        }
        return new LocalisationDataGroup(text, localisationDataList);
    }

    public LocalisationDataGroup getLocalisation(String text, Color color) throws IOException {
        LocalisationDataGroup localisationDataGroup = getLocalisation(text);
        drawRectangle(localisationDataGroup.getLocalisationDataList(), color);
        return localisationDataGroup;
    }

    public String readText(LocalisationData localisationData, Color color) throws IOException {
        String text = this.readText(localisationData);
        drawRectangle(Collections.singletonList(localisationData), color);
        return text;
    }

    public String readText(LocalisationData localisationData) {
        try {
            PDFTextStripperByArea stripper;
            stripper = new PDFTextStripperByArea();

            // origin is top-left (x->right, y->bottom)
            Rectangle2D.Float rect = new Rectangle2D.Float(
                    localisationData.getTlx(),
                    localisationData.getTly(),
                    localisationData.getBrx() - localisationData.getTlx(),
                    localisationData.getBry() - localisationData.getTly());

            stripper.addRegion(localisationData.getText(), rect);
            stripper.extractRegions(pdfPage);
            return stripper.getTextForRegion(localisationData.getText()).replace("\n", "").replace("\r", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void drawRectangle(List<LocalisationData> localisationDataList, Color color) throws IOException {
        contentStream = new PDPageContentStream(document, pdfPage, PDPageContentStream.AppendMode.APPEND, false, false);
        contentStream.setStrokingColor(color);
        contentStream.setLineWidth(LINE_WIDTH);

        localisationDataList.forEach(localisationData -> {
            // origin is bottom-left (x->right, y->top)
            float blx = localisationData.getTlx();
            float bly = pageHeight - localisationData.getBry();
            float width = localisationData.getBrx() - localisationData.getTlx() + LINE_WIDTH;
            float height = localisationData.getBry() - localisationData.getTly() + LINE_WIDTH;
            try {
                contentStream.addRect(blx, bly, width, height);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        contentStream.stroke();
        contentStream.close();
    }

    public void saveAndClosePdfDocument() throws IOException {
        document.save(new File(this.filePath, fileName.replace(".pdf", "-WITH_REGIONS.pdf")));
        //document.save(new File("/home/dev/Desktop/TEMP", fileName.replace(".pdf", "-WITH_REGIONS.pdf")));
        document.close();
    }

    @Override
    public void onNewTextRead(String string, List<TextPosition> textPositionList) {
        this.textPositions.addAll(textPositionList);
        stringBuilder.append(string);
    }

    private void validatePageNumber(int pageNumber) {
        if (pageNumber < 1) throw new IllegalArgumentException("Page number can't be smaller than 1.");
    }

    private void validateAllPageHeightAreEquals() throws Exception {
        if (textPositions.stream()
                .mapToDouble(TextPosition::getPageHeight)
                .distinct()
                .count() != 1) throw new Exception("TextPosition have distinctive page height.");
    }

    private LocalisationData getLocalisation(List<TextPosition> textPositionList) {

        String text = textPositionList.stream()
                .map(TextPosition::getUnicode)
                .collect(Collectors.joining(""));

        float tlx = textPositionList.stream()
                .min(Comparator.comparingDouble(TextPosition::getX))
                .map(TextPosition::getX).orElseThrow() - correction;

        float tly = (float) textPositionList.stream()
                .mapToDouble(tp -> tp.getY() - tp.getHeight())
                .min().orElseThrow() - correction;

        float brx = textPositionList.stream()
                .max(Comparator.comparingDouble(TextPosition::getEndX))
                .map(TextPosition::getEndX).orElseThrow() + correction;

        float bry = pageHeight - textPositionList.stream()
                .min(Comparator.comparingDouble(TextPosition::getEndY))
                .map(TextPosition::getEndY).orElseThrow() + correction;

        return new LocalisationData(text, tlx, tly, brx, bry);
    }

    public static class LocalisationData {

        @Getter
        private final String text;
        @Getter
        private final float tlx;
        @Getter
        private final float tly;
        @Getter
        private final float brx;
        @Getter
        private final float bry;

        @Getter
        private final Rectangle2D.Float rectangle;

        public LocalisationData(String text, float tlx, float tly, float brx, float bry) {
            this.text = text;
            this.tlx = tlx;
            this.tly = tly;
            this.brx = brx;
            this.bry = bry;
            this.rectangle = new Rectangle2D.Float(tlx, bry, brx - tlx, bry - tly);
        }

        public LocalisationData(float tlx, float tly, float brx, float bry) {
            this("", tlx, tly, brx, bry);
        }

        @Override
        public String toString() {
            return String.format("[tlx=%1$f, tly=%2$f, brx=%3$f, bry=%4$f (%1$ff, %2$ff, %3$ff, %4$ff)]",
                    this.tlx,
                    this.tly,
                    this.brx,
                    this.bry);
        }
    }

    public static class LocalisationDataGroup {
        @Getter
        private final List<LocalisationData> localisationDataList;
        private final String text;

        public LocalisationDataGroup(String text, List<LocalisationData> localisationDataList) {
            this.text = text;
            this.localisationDataList = localisationDataList;
        }

        @Override
        public String toString() {
            return text + ":\n" +
                    (localisationDataList.isEmpty() ? "Oupssss, no occurence of the text was found !!!" :
                            localisationDataList
                                    .stream()
                                    .map(localisationData -> (Objects.equals(localisationData.text, text) ? "" : localisationData.getText() + " - ") + localisationData)
                                    .collect(Collectors.joining("\n")));
        }
    }
}