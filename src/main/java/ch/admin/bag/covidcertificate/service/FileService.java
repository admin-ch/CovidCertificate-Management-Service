package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_CSV;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    public static final String CSV_CONTENT_TYPE = "text/csv";
    public static final String EXCEL_CONTENT_TYPE = "application/vnd.ms-excel";

    public char getSeparator(MultipartFile file) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        var line = reader.readLine();
        reader.close();
        if (line.contains(",") && !line.contains("\t") && !line.contains(";")) {
            return ',';
        } else if (line.contains("\t") && !line.contains(",") && !line.contains(";")) {
            return '\t';
        } else if (line.contains(";") && !line.contains(",") && !line.contains("\t")) {
            return ';';
        } else {
            throw new CreateCertificateException(INVALID_CSV);
        }
    }
}
