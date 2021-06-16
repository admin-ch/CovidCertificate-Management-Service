package ch.admin.bag.covidcertificate.service.document;

import ch.admin.bag.covidcertificate.service.domain.AbstractCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.qrcode.EncodeHintType;
import com.itextpdf.text.pdf.qrcode.ErrorCorrectionLevel;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;
import se.digg.dgc.encoding.Barcode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ch.admin.bag.covidcertificate.api.Constants.LOCAL_DATE_FORMAT;
import static ch.admin.bag.covidcertificate.api.Constants.SWISS_TIMEZONE;

@Service
@Slf4j
public class CovidPdfCertificateGenerationService {

    protected static final float MARGIN_LEFT = 40f;
    protected static final float MARGIN_RIGHT = 40f;
    protected static final float MARGIN_TOP = 0f;
    protected static final float MARGIN_BOTTOM = 20f;

    private static final String METADATA = "CovidCertificate";
    private static final String DRAFT = "DRAFT";
    private static final String VACCINATION_DISEASE_MESSAGE_CODE = "vaccination.disease";
    private static final String VACCINATION_DISEASE_LABEL_KEY = "vaccination.disease.label";
    public static final int PADDING_LEFT = 30;

    private final Font fontRow;

    private final Font font8Row;

    private final Font font7Row;

    private final Font fontRowBold;

    private final Font fontEnglish;

    private final Font font8English;

    private final Font font7English;

    private final Font fontHeaderRed;

    private final Font fontHeaderBlack;

    private final Font fontWatermark;

    private final MessageSource messageSource;

    private final Chunk logoBund;
    private final Chunk logoApple;
    private final Chunk logoGoogle;
    private final Chunk logoApp;


    private final boolean addDraftWatermark;

    public CovidPdfCertificateGenerationService(ConfigurableEnvironment env) throws URISyntaxException, IOException, DocumentException {

        final BaseFont baseFont = BaseFont.createFont("arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true,
                    Files.readAllBytes(Path.of(Objects.requireNonNull(this.getClass().getClassLoader().getResource("templates/fonts/arial.ttf")).toURI())),
                    null);

        final BaseFont baseFontBold = BaseFont.createFont("arialbd.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true,
                    Files.readAllBytes(Path.of(Objects.requireNonNull(this.getClass().getClassLoader().getResource("templates/fonts/arialbd.ttf")).toURI())),
                    null);

        final BaseFont baseFontItalic = BaseFont.createFont("ariali.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true,
                    Files.readAllBytes(Path.of(Objects.requireNonNull(this.getClass().getClassLoader().getResource("templates/fonts/ariali.ttf")).toURI())),
                    null);


        fontRow = new Font(baseFont, 10, Font.NORMAL, BaseColor.BLACK);
        font7Row = new Font(baseFont, 7, Font.NORMAL, BaseColor.BLACK);
        font8Row = new Font(baseFont, 8, Font.NORMAL, BaseColor.BLACK);
        fontRowBold = new Font(baseFontBold, 10, Font.NORMAL, BaseColor.BLACK);
        fontEnglish = new Font(baseFontItalic, 9, Font.NORMAL, BaseColor.BLACK);
        font7English = new Font(baseFontItalic, 7, Font.NORMAL, BaseColor.BLACK);
        font8English = new Font(baseFontItalic, 8, Font.NORMAL, BaseColor.BLACK);
        fontHeaderRed = new Font(baseFont, 26, Font.NORMAL, new BaseColor(220, 0, 24));
        fontHeaderBlack = new Font(baseFontItalic, 16, Font.NORMAL, BaseColor.BLACK);

        fontWatermark = new Font(baseFontBold, 80, Font.NORMAL, new BaseColor(234, 234, 234));

        messageSource = messageSource();

        logoBund = getLogo("bund.png", 15);
        logoApple = getLogo("appstore.png", 49);
        logoGoogle = getLogo("googleplay.png", 50);
        logoApp = getLogo("appicon.png", 100);

        addDraftWatermark = Arrays.stream(env.getActiveProfiles()).noneMatch("prod"::equals);

    }

    private ResourceBundleMessageSource messageSource() {
        var source = new ResourceBundleMessageSource();
        source.setBasenames("templates/messages/document-messages");
        source.setUseCodeAsDefaultMessage(true);
        source.setDefaultEncoding("UTF-8");
        return source;
    }


    public byte[] generateCovidCertificate(AbstractCertificatePdf data, Barcode barcode) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            Document document = new Document();

            final Locale locale = getLocale(data.getLanguage());

            PdfWriter writer = PdfWriter.getInstance(document, stream);

            document.open();

            addMetadata(document);

            document.setMargins(MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP, MARGIN_BOTTOM);

            document.add(headerTable(locale));

            Image qrCode = renderQRCode(writer, barcode.getPayload());

            document.add(mainTable(locale, data, qrCode));

            document.add(issuerTable(locale));

            document.add(infoTable(locale));

            document.add(footerTable(locale));

            document.close();

            if (addDraftWatermark) {
                return addWatermark(stream.toByteArray());
            }

            return stream.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Locale getLocale(String language) {
        switch (language) {
            case "fr":
                return Locale.FRENCH;
            case "it":
                return Locale.ITALIAN;
            case "rm":
                return Locale.forLanguageTag("rm");
            default:
                return Locale.GERMAN;
        }
    }

    protected void addMetadata(Document document) {
        document.addTitle(METADATA);
        document.addSubject(METADATA);
        document.addKeywords(METADATA);
        document.addAuthor(METADATA);
        document.addCreator(METADATA);
    }

    private PdfPTable headerTable(Locale locale) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setPaddingTop(0);

        PdfPCell cell = new PdfPCell();
        cell.addElement(logoBund);
        cell.setPaddingLeft(-5);
        cell.setPaddingTop(30);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight(70);
        cell.setRowspan(2);
        cell.setVerticalAlignment(Rectangle.TOP);
        table.addCell(cell);

        PdfPCell valueCell = new PdfPCell(new Phrase(messageSource.getMessage("document.title", null, locale), fontHeaderRed));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setVerticalAlignment(Rectangle.TOP);
        valueCell.setPaddingTop(0);
        table.addCell(valueCell);

        PdfPCell valueCell2 = new PdfPCell(new Phrase(messageSource.getMessage("document.title", null, Locale.ENGLISH), fontHeaderBlack));
        valueCell2.setBorder(Rectangle.NO_BORDER);
        valueCell2.setVerticalAlignment(Rectangle.TOP);
        valueCell2.setPaddingTop(0);
        table.addCell(valueCell2);

        return table;
    }

    private PdfPTable mainTable(Locale locale, AbstractCertificatePdf data, Image qrCode) {
        float[] pointColumnWidths = {50F, 20F, 30F};
        PdfPTable table = new PdfPTable(pointColumnWidths);
        table.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell(addLeftColumn(locale, qrCode, data));
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setRowspan(30);
        cell.setFixedHeight(450);
        table.addCell(cell);

        if (data instanceof VaccinationCertificatePdf) {
            addVaccineData(locale, (VaccinationCertificatePdf) data, table);
        } else if (data instanceof RecoveryCertificatePdf) {
            addRecoveryData(locale, (RecoveryCertificatePdf) data, table);
        } else {
            addTestData(locale, (TestCertificatePdf) data, table);
        }
        return table;
    }

    private void addVaccineData(Locale locale, VaccinationCertificatePdf data, PdfPTable table) {
        addIssuerRow(table, locale, "vaccination.title", 0, true, 0);
        addRow(table, locale, VACCINATION_DISEASE_LABEL_KEY, messageSource.getMessage(VACCINATION_DISEASE_MESSAGE_CODE, null, locale));
        addRow(table, locale, "vaccination.dosis.label", data.getNumberOfDoses() + "/" + data.getTotalNumberOfDoses());
        addRow(table, locale, "vaccination.type.label", data.getVaccineProphylaxis());
        addRow(table, locale, "vaccination.product.label", data.getMedicinalProduct());
        addRow(table, locale, "vaccination.manufacturer.label", data.getMarketingAuthorizationHolder());
        addRow(table, locale, "vaccination.date.label", data.getVaccinationDate().format(LOCAL_DATE_FORMAT));
        addLocaleAndEnglishRow(table, locale, "vaccination.country.label", data.getCountryOfVaccination(), data.getCountryOfVaccinationEn());
    }

    private void addRecoveryData(Locale locale, RecoveryCertificatePdf data, PdfPTable table) {
        addIssuerRow(table, locale, "recovery.title", 0, true, 0);
        addRow(table, locale, VACCINATION_DISEASE_LABEL_KEY, messageSource.getMessage(VACCINATION_DISEASE_MESSAGE_CODE, null, locale));
        addRow(table, locale, "recovery.firstPositiveTestResult.label", data.getDateOfFirstPositiveTestResult().format(LOCAL_DATE_FORMAT));
        addRow(table, locale, "recovery.validFrom", data.getValidFrom().format(LOCAL_DATE_FORMAT));
        addRow(table, locale, "recovery.validUntil", data.getValidUntil().format(LOCAL_DATE_FORMAT));
        addLocaleAndEnglishRow(table, locale, "recovery.countryOfTest.label", data.getCountryOfTest(), data.getCountryOfTestEn());
    }

    private void addTestData(Locale locale, TestCertificatePdf data, PdfPTable table) {
        addIssuerRow(table, locale, "test.title", 0, true, 0);
        addRow(table, locale, VACCINATION_DISEASE_LABEL_KEY, messageSource.getMessage(VACCINATION_DISEASE_MESSAGE_CODE, null, locale));
        addLocaleAndEnglishRow(table, locale, "test.result.label", messageSource.getMessage("test.result.value", null, locale), messageSource.getMessage("test.result.value", null, Locale.ENGLISH));
        addRow(table, locale, "test.type.label", data.getTypeOfTest());
        addRow(table, locale, "test.name.label", data.getTestName());
        if (StringUtils.isNotBlank(data.getTestManufacturer())) {
            addRow(table, locale, "test.manufacturer.label", data.getTestManufacturer());
        }
        addRow(table, locale, "test.date.label", data.getSampleDateTime().withZoneSameInstant(SWISS_TIMEZONE).format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")));
        addRow(table, locale, "test.conducted.label", data.getTestingCentreOrFacility());
        addLocaleAndEnglishRow(table, locale, "test.country.label", data.getMemberStateOfTest(), data.getMemberStateOfTestEn());
    }

    private PdfPTable addLeftColumn(Locale locale, Image qrCode, AbstractCertificatePdf data) {
        float[] pointColumnWidths = {50F, 50F};
        PdfPTable table = new PdfPTable(pointColumnWidths);

        PdfPCell cell = new PdfPCell(qrCode);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(2);
        cell.setPaddingTop(-7);
        cell.setPaddingLeft(22);
        table.addCell(cell);

        PdfPCell cell2 = new PdfPCell(new Phrase(data.getIdentifier(), fontRow));
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setColspan(2);
        cell2.setPaddingLeft(PADDING_LEFT);
        cell2.setPaddingTop(5);
        table.addCell(cell2);

        addQrLabelCell(table, locale, LocalDateTime.now());

        addIssuerRow(table, locale, "personalData.title", 20, true, PADDING_LEFT);
        addNameRow(table, locale, "personalData.name.label", data.getFamilyName() + " " + data.getGivenName(), PADDING_LEFT);
        addRow(table, locale, "personalData.date.label", data.getDateOfBirth().format(LOCAL_DATE_FORMAT), PADDING_LEFT);

        return table;
    }

    private void addRow(PdfPTable table, Locale locale, String key, String value) {
        addRow(table, locale, key, value, 0);
    }

    private void addRow(PdfPTable table, Locale locale, String key, String value, float paddingLeft) {
        PdfPCell titleCell = new PdfPCell(new Phrase(messageSource.getMessage(key, null, locale), fontRow));
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPaddingTop(15);
        titleCell.setPaddingLeft(paddingLeft);
        table.addCell(titleCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, fontRow));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingTop(15);
        valueCell.setRowspan(2);
        table.addCell(valueCell);

        PdfPCell titleEnglishCell = new PdfPCell(new Phrase(messageSource.getMessage(key, null, Locale.ENGLISH), fontEnglish));
        titleEnglishCell.setBorder(Rectangle.NO_BORDER);
        titleEnglishCell.setPaddingTop(0);
        titleEnglishCell.setHorizontalAlignment(Rectangle.LEFT);
        titleEnglishCell.setPaddingLeft(paddingLeft);
        table.addCell(titleEnglishCell);

    }

    private void addNameRow(PdfPTable table, Locale locale, String key, String value, float paddingLeft) {
        PdfPCell titleCell = new PdfPCell(new Phrase(messageSource.getMessage(key, null, locale), fontRow));
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPaddingTop(15);
        titleCell.setPaddingLeft(paddingLeft);
        table.addCell(titleCell);

        Font font = fontRow;

        if (value.length() > 80) {
            font = font7Row;
        }

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingTop(15);
        valueCell.setRowspan(2);
        table.addCell(valueCell);

        PdfPCell titleEnglishCell = new PdfPCell(new Phrase(messageSource.getMessage(key, null, Locale.ENGLISH), fontEnglish));
        titleEnglishCell.setBorder(Rectangle.NO_BORDER);
        titleEnglishCell.setPaddingTop(0);
        titleEnglishCell.setHorizontalAlignment(Rectangle.LEFT);
        titleEnglishCell.setPaddingLeft(paddingLeft);
        table.addCell(titleEnglishCell);

    }

    private void addLocaleAndEnglishRow(PdfPTable table, Locale locale, String key, String value, String valueEn) {
        PdfPCell titleCell = new PdfPCell(new Phrase(messageSource.getMessage(key, null, locale), fontRow));
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPaddingTop(15);
        titleCell.setPaddingLeft(0);
        table.addCell(titleCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, fontRow));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingTop(15);
        table.addCell(valueCell);

        PdfPCell titleEnglishCell = new PdfPCell(new Phrase(messageSource.getMessage(key, null, Locale.ENGLISH), fontEnglish));
        titleEnglishCell.setBorder(Rectangle.NO_BORDER);
        titleEnglishCell.setPaddingTop(0);
        titleEnglishCell.setHorizontalAlignment(Rectangle.LEFT);
        titleEnglishCell.setPaddingLeft(0);
        table.addCell(titleEnglishCell);

        PdfPCell valueEnglishCell = new PdfPCell(new Phrase(valueEn, fontEnglish));
        valueEnglishCell.setBorder(Rectangle.NO_BORDER);
        valueEnglishCell.setPaddingTop(0);
        table.addCell(valueEnglishCell);
    }

    private void addIssuerRow(PdfPTable table, Locale locale) {
        addIssuerRow(table, locale, "issuer.title", 15, false, (float) CovidPdfCertificateGenerationService.PADDING_LEFT);
    }

    private void addIssuerRow(PdfPTable table, Locale locale, String key, int padding, boolean title, float paddingLeft) {

        PdfPCell titleCell = new PdfPCell(new Phrase(messageSource.getMessage(key, null, locale), title ? fontRowBold : fontRow));
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPaddingTop(padding);
        titleCell.setColspan(2);
        titleCell.setPaddingLeft(paddingLeft);
        table.addCell(titleCell);

        PdfPCell issuerCell = new PdfPCell(new Phrase(messageSource.getMessage(key, null, Locale.ENGLISH), fontEnglish));
        issuerCell.setBorder(Rectangle.NO_BORDER);
        issuerCell.setPaddingTop(0);
        issuerCell.setColspan(2);
        issuerCell.setPaddingLeft(paddingLeft);
        table.addCell(issuerCell);
    }

    private void addQrLabelCell(PdfPTable table, Locale locale, LocalDateTime dateTime) {
        String date = dateTime.format(LOCAL_DATE_FORMAT);
        String time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        PdfPCell first = new PdfPCell(new Phrase(messageSource.getMessage("qrCode.label", new String[]{date, time}, locale), font8Row));
        first.setBorder(Rectangle.NO_BORDER);
        first.setColspan(2);
        first.setPaddingLeft(PADDING_LEFT);
        table.addCell(first);

        PdfPCell second = new PdfPCell(new Phrase(messageSource.getMessage("qrCode.label", new String[]{date, time}, Locale.ENGLISH), font8English));
        second.setBorder(Rectangle.NO_BORDER);
        second.setPaddingTop(0);
        second.setColspan(2);
        second.setPaddingLeft(PADDING_LEFT);
        table.addCell(second);

        PdfPCell third = new PdfPCell(new Phrase(messageSource.getMessage("qrCode.date.label", null, Locale.ENGLISH), font8English));
        third.setBorder(Rectangle.NO_BORDER);
        third.setPaddingTop(0);
        third.setColspan(2);
        third.setPaddingLeft(PADDING_LEFT);
        table.addCell(third);
    }


    private Chunk getLogo(String name, int scale) {

        URL imageUrl = this.getClass().getClassLoader().getResource("templates/" + name);

        Image logo;
        try {
            logo = Image.getInstance(Objects.requireNonNull(imageUrl));
            logo.scalePercent(scale);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return new Chunk(logo, 0, 0);
    }

    private PdfPTable issuerTable(Locale locale) {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        addIssuerRow(table, locale);
        addIssuerRow(table, locale, "issuer.issuer", 5, true, PADDING_LEFT);

        return table;
    }

    private PdfPTable infoTable(Locale locale) {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(95);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setSpacingBefore((float) 10);

        PdfPCell cell = new PdfPCell();
        addInfoCell(cell, messageSource.getMessage("info.info1", null, locale), font7Row, 0);
        addInfoCell(cell, messageSource.getMessage("info.info2", null, locale), font7Row, 0);
        addInfoCell(cell, messageSource.getMessage("info.info1", null, Locale.ENGLISH), font7English, 5);
        addInfoCell(cell, messageSource.getMessage("info.info2", null, Locale.ENGLISH), font7English, 0);
        cell.setBackgroundColor(new BaseColor(252, 231, 232));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(10);
        table.addCell(cell);

        return table;
    }

    private void addInfoCell(PdfPCell cell, String text, Font font, float spacingBefore) {
        Paragraph paragraph = new Paragraph(new Phrase(text, font));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingBefore(spacingBefore);
        cell.addElement(paragraph);

    }

    private PdfPTable footerTable(Locale locale) {
        float[] pointColumnWidths = {60F, 30F, 10F};
        PdfPTable table = new PdfPTable(pointColumnWidths);
        table.setWidthPercentage(100);
        table.setSpacingBefore((float) 5);

        PdfPCell cell = new PdfPCell();
        cell.addElement(new Paragraph(new Phrase(messageSource.getMessage("footer.app", null, locale), fontRow)));
        cell.addElement(new Phrase(messageSource.getMessage("footer.app", null, Locale.ENGLISH), fontEnglish));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingLeft(PADDING_LEFT);
        table.addCell(cell);

        PdfPCell logoCell = new PdfPCell();
        Paragraph paragraphLogo = new Paragraph();
        paragraphLogo.add(logoApp);
        paragraphLogo.add(Chunk.NEWLINE);
        paragraphLogo.add(new Phrase("Covid Certificate", font8Row));
        paragraphLogo.setAlignment(Element.ALIGN_CENTER);
        logoCell.addElement(paragraphLogo);
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_CENTER);
        logoCell.setPaddingTop(20);
        logoCell.setRowspan(2);
        table.addCell(logoCell);

        PdfPCell storeCell = new PdfPCell();
        storeCell.addElement(logoApple);
        storeCell.addElement(logoGoogle);
        storeCell.setBorder(Rectangle.NO_BORDER);
        storeCell.setVerticalAlignment(Element.ALIGN_RIGHT);
        storeCell.setRowspan(2);
        storeCell.setPaddingTop(5);
        table.addCell(storeCell);

        PdfPCell infoCell = new PdfPCell(new Phrase(messageSource.getMessage("footer.infoline", null, locale), fontRowBold));
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoCell.setPaddingLeft(PADDING_LEFT);
        infoCell.setPaddingTop(10);
        table.addCell(infoCell);

        return table;
    }

    protected byte[] addWatermark(byte[] input) throws DocumentException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            PdfReader reader = new PdfReader(input);
            PdfStamper stamper = new PdfStamper(reader, stream);
            stamper.setRotateContents(false);
            PdfContentByte canvas = stamper.getUnderContent(1);

            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(DRAFT, fontWatermark), 330, 400, 45);
            stamper.close();

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return stream.toByteArray();
    }

    private Image renderQRCode(PdfWriter writer, String content) throws BadElementException {
        PdfContentByte cb = writer.getDirectContent();

        // Create QR code object with error correction level "Q" (25%)
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
        BarcodeQRCode qrCode = new BarcodeQRCode(content, 1, 1, hints);

        float widthHeight = 200;

        // Get number of rectangles of QR code
        Rectangle size = qrCode.getBarcodeSize();

        // Scale total size by a factor
        PdfTemplate template = cb.createTemplate(widthHeight, widthHeight);

        // Place QR code in template and wrap in image
        qrCode.placeBarcode(template, BaseColor.BLACK, widthHeight / size.getWidth());
        return Image.getInstance(template);
    }

}
