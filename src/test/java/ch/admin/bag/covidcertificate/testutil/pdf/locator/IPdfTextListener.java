package ch.admin.bag.covidcertificate.testutil.pdf.locator;

import org.apache.pdfbox.text.TextPosition;

import java.util.List;

interface IPdfTextListener {
        void onNewTextRead(String string, List<TextPosition> textPositionList);
    }