package ch.admin.bag.covidcertificate;

import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.TestCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificateQrCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.upokecenter.cbor.CBORObject;
import org.apache.commons.codec.binary.Hex;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Base64Utils;
import se.digg.dgc.encoding.Base45;
import se.digg.dgc.encoding.Zlib;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Ignore("Util for automated JSON file generation for EU tests.")
@ExtendWith(MockitoExtension.class)
public class DGCTestJSONGenerator {
    private static final String PATH = "target/dgc-testdata/";
    private static final String PNG = ".png";
    private static final String JSON = ".json";
    private static final Integer VERSION = 1;
    private static final String JSON_SCHEMA = "1.2.1";
    private static final String CERTIFICATE = "TODO: Replace with public key.";
    private static final String ZONE_ID = "Europe/Zurich";
    private static final String DESCRIPTION = "VALID ";
    private static final String PREFIX = "HC1:";
    private static final String IDENTIFIER_VACCINATION = "\"v\":";
    private static final String IDENTIFIER_TEST = "\"t\":";
    private static final String IDENTIFIER_RECOVERY = "\"r\":";

    @Test
    public void createJSONFiles() throws Exception {
        for (String qrCodeFilename : Objects.requireNonNull(new File(PATH).list())) {
            if (qrCodeFilename.endsWith(PNG)) {
                createJSONFile(qrCodeFilename);
            }
        }
    }

    private void createJSONFile(String qrCodeFilename) throws Exception {
        DGCTestJSON dgcTestJSON = new DGCTestJSON();

        // Getting the image as base64 string.
        File qrCodeFile = new File(PATH + qrCodeFilename);
        byte[] qrCodeBytes = Files.readAllBytes(qrCodeFile.toPath());
        byte[] qrCodeBase64Bytes = Base64Utils.encode(qrCodeBytes);
        String qrCodeBase64String = new String(qrCodeBase64Bytes);
        dgcTestJSON.setCode2d(qrCodeBase64String);

        // Getting the content of the QR code.
        BufferedImage qrCodeBufferedImage = ImageIO.read(qrCodeFile);
        LuminanceSource qrCodeSource = new BufferedImageLuminanceSource(qrCodeBufferedImage);
        BinaryBitmap qrCodeBitmap = new BinaryBitmap(new HybridBinarizer(qrCodeSource));
        Result qrCodeResult = new MultiFormatReader().decode(qrCodeBitmap);
        String qrCodeContent = qrCodeResult.getText();
        dgcTestJSON.setPrefix(qrCodeContent);

        // Getting the content of the QR code without prefix.
        String qrCodeContentWithoutPrefix = qrCodeContent.replaceFirst(PREFIX, "");
        dgcTestJSON.setBase45(qrCodeContentWithoutPrefix);

        // Getting the content of the QR code base45 decoded.
        byte[] qrCodeContentBase45DecodedBytes = Base45.getDecoder().decode(qrCodeContentWithoutPrefix);
        String qrCodeContentBase45DecodedHexString = Hex.encodeHexString(qrCodeContentBase45DecodedBytes);
        dgcTestJSON.setCompressed(qrCodeContentBase45DecodedHexString);

        // Getting the content of the QR code ZLib decompressed.
        byte[] qrCodeContentDecompressedBytes = Zlib.decompress(qrCodeContentBase45DecodedBytes, true);
        String qrCodeContentDecompressedHexString = Hex.encodeHexString(qrCodeContentDecompressedBytes);
        dgcTestJSON.setCose(qrCodeContentDecompressedHexString);

        // Getting the certificate JSON as CBOR encoded.
        CBORObject coseCBORObject = CBORObject.DecodeFromBytes(qrCodeContentDecompressedBytes);
        byte[] cosePayloadBytes = coseCBORObject.get(2).GetByteString();
        CBORObject cosePayloadCBORObject = CBORObject.DecodeFromBytes(cosePayloadBytes);
        byte[] hCertCBORBytes = cosePayloadCBORObject.get(-260).get(1).EncodeToBytes();
        String hCertHexString = Hex.encodeHexString(hCertCBORBytes);
        dgcTestJSON.setCbor(hCertHexString);

        // Getting the certificate JSON as string.
        CBORObject hCertCBORObject = CBORObject.DecodeFromBytes(hCertCBORBytes);
        String hCert = hCertCBORObject.ToJSONString();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Object hCertObject = null;
        String certificateType = null;
        if (hCert.contains(IDENTIFIER_VACCINATION)) {
            hCertObject = objectMapper.readValue(hCert, VaccinationCertificateQrCode.class);
            certificateType = "Vaccination";
        } else if (hCert.contains(IDENTIFIER_TEST)) {
            hCertObject = objectMapper.readValue(hCert, TestCertificateQrCode.class);
            certificateType = "Test";
        } else if (hCert.contains(IDENTIFIER_RECOVERY)) {
            hCertObject = objectMapper.readValue(hCert, RecoveryCertificateQrCode.class);
            certificateType = "Recovery";
        }
        dgcTestJSON.setJson(hCertObject);

        // Setting the test context.
        DGCTestJSONTestContext dgcTestJSONTestContext = new DGCTestJSONTestContext();
        dgcTestJSONTestContext.setVersion(VERSION);
        dgcTestJSONTestContext.setJsonSchema(JSON_SCHEMA);
        dgcTestJSONTestContext.setCertificate(CERTIFICATE);
        dgcTestJSONTestContext.setValidationClock(OffsetDateTime.now(Clock.system(ZoneId.of(ZONE_ID))).withNano(0).toString());
        dgcTestJSONTestContext.setDescription(DESCRIPTION + certificateType);
        dgcTestJSON.setTestContext(dgcTestJSONTestContext);

        // Setting the expected results.
        DGCTestJSONExpectedResults dgcTestJSONExpectedResults = new DGCTestJSONExpectedResults();
        dgcTestJSONExpectedResults.setExpectedValidObject(true);
        dgcTestJSONExpectedResults.setExpectedSchemaValidation(true);
        dgcTestJSONExpectedResults.setExpectedEncode(true);
        dgcTestJSONExpectedResults.setExpectedDecode(true);
        dgcTestJSONExpectedResults.setExpectedVerify(true);
        dgcTestJSONExpectedResults.setExpectedCompression(true);
        dgcTestJSONExpectedResults.setExpectedKeyUsage(true);
        dgcTestJSONExpectedResults.setExpectedUnPrefix(true);
        dgcTestJSONExpectedResults.setExpectedValidJSON(true);
        dgcTestJSONExpectedResults.setExpectedB45Decode(true);
        dgcTestJSONExpectedResults.setExpectedPictureDecode(true);
        dgcTestJSONExpectedResults.setExpectedExpirationCheck(true);
        dgcTestJSON.setExpectedResults(dgcTestJSONExpectedResults);

        // Creating the JSON file.
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.writeValue(new File(PATH + qrCodeFilename.replace(PNG, JSON)), dgcTestJSON);
    }
}
