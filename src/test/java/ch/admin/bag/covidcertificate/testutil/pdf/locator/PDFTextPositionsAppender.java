package ch.admin.bag.covidcertificate.testutil.pdf.locator;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.List;

public class PDFTextPositionsAppender extends PDFTextStripper {

    private final IPdfTextListener pdfTextListener;

    public PDFTextPositionsAppender(IPdfTextListener pdfTextListener) throws IOException {
        this.pdfTextListener = pdfTextListener;
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositionList) {
        pdfTextListener.onNewTextRead(string, textPositionList);
    }
}