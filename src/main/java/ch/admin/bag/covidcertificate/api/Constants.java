package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateError;
import ch.admin.bag.covidcertificate.api.exception.RevocationError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final String VERSION = "1.3.0";
    public static final String DEFAULT_DISEASE_OR_AGENT_TARGETED = "840539006";
    public static final String DEFAULT_DISEASE_OR_AGENT_SYSTEM = "2.16.840.1.113883.6.96";
    public static final String ISSUER = "Bundesamt für Gesundheit (BAG)";
    public static final String PCR_TYPE_CODE = "LP6464-4";
    public static final String NONE_PCR_TYPE_CODE = "LP217198-3";
    public static final int MAX_STRING_LENGTH = 80;
    public static final int DAYS_UNTIL_RECOVERY_VALID = 10;
    public static final int RECOVERY_CERTIFICATE_VALIDITY_IN_DAYS = 179;

    // KPI Logs constants
    public static final String KPI_TYPE_VACCINATION = "v";
    public static final String KPI_TYPE_TEST = "t";
    public static final String KPI_TYPE_RECOVERY = "r";
    public static final String KPI_TYPE_INAPP_DELIVERY = "ad";
    public static final String USER_EXT_ID_CLAIM_KEY = "userExtId";
    public static final String KPI_UUID_KEY = "uuid";
    public static final String KPI_TIMESTAMP_KEY = "ts";
    public static final String KPI_TYPE_KEY = "type";
    public static final String KPI_CREATE_CERTIFICATE_SYSTEM_KEY = "cc";
    public static final String KPI_SYSTEM_UI = "ui";
    public static final String KPI_REVOKE_CERTIFICATE_SYSTEM_KEY = "re";
    public static final String KPI_OTP_SYSTEM_KEY = "otp";

    public static final LocalDate MIN_DATE_OF_BIRTH = LocalDate.of(1900, Month.JANUARY, 1);
    public static final LocalDate MAX_DATE_OF_BIRTH = LocalDate.of(2099, Month.DECEMBER, 31);
    public static final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter LOG_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static final ZoneId SWISS_TIMEZONE = ZoneId.of("Europe/Zurich");

    public static final CreateCertificateError NO_VACCINATION_DATA = new CreateCertificateError(451, "No vaccination data was specified", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError NO_PERSON_DATA = new CreateCertificateError(452, "No person data was specified", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_DATE_OF_BIRTH = new CreateCertificateError(453, "Invalid dateOfBirth! Must be younger than 1900-01-01", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_MEDICINAL_PRODUCT = new CreateCertificateError(454, "Invalid medicinal product", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_DOSES = new CreateCertificateError(455, "Invalid number of doses", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_VACCINATION_DATE = new CreateCertificateError(456, "Invalid vaccination date! Date cannot be in the future", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_COUNTRY_OF_VACCINATION = new CreateCertificateError(457, "Invalid country of vaccination", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_GIVEN_NAME = new CreateCertificateError(458, "Invalid given name! Must not exceed 80 chars", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_FAMILY_NAME = new CreateCertificateError(459, "Invalid family name! Must not exceed 80 chars", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError NO_TEST_DATA = new CreateCertificateError(460, "No test data was specified", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_MEMBER_STATE_OF_TEST = new CreateCertificateError(461, "Invalid member state of test", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_TYP_OF_TEST = new CreateCertificateError(462, "Invalid type of test and manufacturer code combination! Must either be a PCR Test type and no manufacturer code or give a manufacturer code and the antigen test type code.", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_TEST_CENTER = new CreateCertificateError(463, "Invalid testing center or facility", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_SAMPLE_OR_RESULT_DATE_TIME = new CreateCertificateError(464, "Invalid sample date time! Sample date must be before current date time", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError NO_RECOVERY_DATA = new CreateCertificateError(465, "No recovery data specified", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT = new CreateCertificateError(466, "Invalid date of first positive test result", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_COUNTRY_OF_TEST = new CreateCertificateError(467, "Invalid country of test", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_LANGUAGE = new CreateCertificateError(469, "The given language does not match any of the supported languages: de, it, fr, rm!", HttpStatus.BAD_REQUEST);
    public static final RevocationError INVALID_UVCI = new RevocationError(470, "Invalid UVCI format.", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_ADDRESS = new CreateCertificateError(474, "Paper-based delivery requires a valid address.", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError DUPLICATE_DELIVERY_METHOD = new CreateCertificateError(475, "Delivery method can either be InApp or print, but not both.", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError UNKNOWN_APP_CODE = new CreateCertificateError(476, "Unknown app code.", HttpStatus.NOT_FOUND);
    public static final CreateCertificateError INVALID_STANDARDISED_GIVEN_NAME = new CreateCertificateError(477, "Invalid given name! The standardised given name exceeds 80 chars", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_STANDARDISED_FAMILY_NAME = new CreateCertificateError(478, "Invalid family name! The standardised family name exceeds 80 chars", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_APP_CODE = new CreateCertificateError(479, "App code is in an invalid format.", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_PRINT_FOR_TEST = new CreateCertificateError(488, "Print is not available for test certificates", HttpStatus.BAD_REQUEST);

    public static final RevocationError DUPLICATE_UVCI = new RevocationError(480, "Duplicate UVCI.", HttpStatus.CONFLICT);

    public static final CreateCertificateError INVALID_CSV = new CreateCertificateError(481, "The CSV can not be read!", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_CSV_SIZE = new CreateCertificateError(482, "The CSV has an ivalid size! Must contain 1 to 100 entries.", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError NOT_A_CSV = new CreateCertificateError(483, "The sent file is not a CSV file.", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_CERTIFICATE_TYPE = new CreateCertificateError(484, "Invalid certificate type! 'vaccination', 'test', and 'recovery' are allowed", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_CREATE_REQUESTS = new CreateCertificateError(485, "One or more of the requests in the CSV contain invalid data. For more detailed error messages check the returned CSV", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_APP_CODE_CHECKSUM = new CreateCertificateError(486, "Invalid app code, check input.", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_APP_CODE_LENGTH = new CreateCertificateError(487, "Incorrect input, the code consists of 9 characters.", HttpStatus.BAD_REQUEST);

    public static final CreateCertificateError CREATE_COSE_PROTECTED_HEADER_FAILED = new CreateCertificateError(550, "Creating COSE protected header failed.", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError CREATE_COSE_PAYLOAD_FAILED = new CreateCertificateError(551, "Creating COSE payload failed.", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError CREATE_COSE_SIGNATURE_DATA_FAILED = new CreateCertificateError(552, "Creating COSE signature data failed.", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError CREATE_SIGNATURE_FAILED = new CreateCertificateError(553, "Creating signature failed.", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError CREATE_COSE_SIGN1_FAILED = new CreateCertificateError(554, "Creating COSE_Sign1 failed.", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError CREATE_BARCODE_FAILED = new CreateCertificateError(555, "Creating barcode failed.", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError PRINTING_FAILED = new CreateCertificateError(556, "Printing failed due to a technical error.", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError WRITING_RETURN_CSV_FAILED = new CreateCertificateError(557, "Writing CSV failed.", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError APP_DELIVERY_FAILED = new CreateCertificateError(558, "App delivery failed due to a technical error.", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError SIGNING_CERTIFICATE_MISSING = new CreateCertificateError(559, "No signing certificate was found.", HttpStatus.INTERNAL_SERVER_ERROR);
}
