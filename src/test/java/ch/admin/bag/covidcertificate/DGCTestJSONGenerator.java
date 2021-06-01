package ch.admin.bag.covidcertificate;

import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.TestCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificateQrCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

@Ignore
@ExtendWith(MockitoExtension.class)
public class DGCTestJSONGenerator {
    private static final CertificateType TYPE = CertificateType.Vaccination;

    @Test
    public void createJSON() throws Exception {
        // Setting the test context.
        DGCTestJSONTestContext dgcTestJSONTestContext = new DGCTestJSONTestContext();
        dgcTestJSONTestContext.setVersion(1);
        dgcTestJSONTestContext.setJsonSchema("1.0.0");
        dgcTestJSONTestContext.setCertificate("TODO: Replace with public key.");
        dgcTestJSONTestContext.setValidationClock(OffsetDateTime.now(Clock.system(ZoneId.of("Europe/Zurich"))).withNano(0).toString());
        dgcTestJSONTestContext.setDescription(String.format("VALID %s", TYPE));

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

        // Setting the JSON.
        DGCTestJSON dgcTestJSON = new DGCTestJSON();
        dgcTestJSON.setTestContext(dgcTestJSONTestContext);
        dgcTestJSON.setExpectedResults(dgcTestJSONExpectedResults);

        // Getting the image as base64 string.
        File qrCodeFile = new File("target/dgc-testdata/1.png");
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
        String qrCodeContentWithoutPrefix = qrCodeContent.replaceFirst("HC1:", "");
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
        byte[] hcertCBORBytes = cosePayloadCBORObject.get(-260).get(1).EncodeToBytes();
        String hcertHexString = Hex.encodeHexString(hcertCBORBytes);
        dgcTestJSON.setCbor(hcertHexString);

        // Getting the certificate JSON as string.
        CBORObject hcertCBORObject = CBORObject.DecodeFromBytes(hcertCBORBytes);
        String hcert = hcertCBORObject.ToJSONString();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Object hcertObject = null;
        if (TYPE == CertificateType.Vaccination) {
            hcertObject = objectMapper.readValue(hcert, VaccinationCertificateQrCode.class);
        } else if (TYPE == CertificateType.Test) {
            hcertObject = objectMapper.readValue(hcert, TestCertificateQrCode.class);
        } else if (TYPE == CertificateType.Recovery) {
            hcertObject = objectMapper.readValue(hcert, RecoveryCertificateQrCode.class);
        }
        dgcTestJSON.setJson(hcertObject);

        // Creating the JSON file.
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.writeValue(new File("target/dgc-testdata/1.json"), dgcTestJSON);
    }

    private enum CertificateType {
        Vaccination,
        Test,
        Recovery
    }
}