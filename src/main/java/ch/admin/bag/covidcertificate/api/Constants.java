package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.AuthorizationError;
import ch.admin.bag.covidcertificate.api.exception.ConvertCertificateError;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateError;
import ch.admin.bag.covidcertificate.api.exception.CsvError;
import ch.admin.bag.covidcertificate.api.exception.FeatureToggleError;
import ch.admin.bag.covidcertificate.api.exception.RevocationError;
import ch.admin.bag.covidcertificate.api.exception.ValueSetError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    // Utils
    public static final String EMPTY_STRING = "";

    public static final int MIN_NB_OF_DOSES = 1;
    public static final int MAX_NB_OF_DOSES = 9;

    public static final String VERSION = "1.3.0";
    public static final String DEFAULT_DISEASE_OR_AGENT_TARGETED = "840539006";
    public static final String DEFAULT_DISEASE_OR_AGENT_SYSTEM = "2.16.840.1.113883.6.96";
    public static final String ISSUER = "Bundesamt für Gesundheit (BAG)";
    public static final int MAX_STRING_LENGTH = 80;
    public static final int DAYS_UNTIL_RECOVERY_VALID = 10;
    public static final int RECOVERY_CERTIFICATE_VALIDITY_IN_DAYS = 179;
    public static final int ANTIBODY_CERTIFICATE_VALIDITY_IN_DAYS = 89;
    public static final String ISO_3166_1_ALPHA_2_CODE_SWITZERLAND = "CH";

    // KPI system keys
    public static final String KPI_CREATE_CERTIFICATE_SYSTEM_KEY = "cc";
    public static final String KPI_REVOKE_CERTIFICATE_SYSTEM_KEY = "re";
    public static final String KPI_MASS_REVOKE_CERTIFICATE_SYSTEM_KEY = "mre";

    public static final String KPI_REVOCATION_LIST_REDUCTION_SYSTEM_KEY = "re-d";
    public static final String KPI_OTP_SYSTEM_KEY = "otp";

    // KPI Type Logs constants
    public static final String KPI_TYPE_VACCINATION = "v";
    public static final String KPI_TYPE_VACCINATION_TOURIST = "vt";
    public static final String KPI_TYPE_TEST = "t";
    public static final String KPI_TYPE_RECOVERY = "r";
    public static final String KPI_TYPE_RECOVERY_RAT_EU = "rreu";
    public static final String KPI_TYPE_ANTIBODY = "a";
    public static final String KPI_TYPE_EXCEPTIONAL = "me";
    public static final String KPI_TYPE_REVOCATION = KPI_REVOKE_CERTIFICATE_SYSTEM_KEY;

    public static final String KPI_TYPE_REVOCATION_LIST_REDUCTION = KPI_REVOCATION_LIST_REDUCTION_SYSTEM_KEY;
    public static final String KPI_TYPE_IN_APP_DELIVERY = "ad";
    public static final String KPI_TYPE_MASS_REVOCATION_SUCCESS = "mre-s";
    public static final String KPI_TYPE_MASS_REVOCATION_FAILURE = "mre-f";
    public static final String KPI_TYPE_MASS_REVOCATION_REDUNDANT = "mre-r";
    public static final String KPI_TYPE_CERTIFICATE_CONVERSION = "cc";

    // Other KPI Logs constants
    public static final String KPI_IN_APP_DELIVERY_CODE_KEY = "code";
    public static final String KPI_UVCI_KEY = "uvci";
    public static final String KPI_CONVERSION_OLD_UVCI_KEY = "old_uvci";
    public static final String USER_EXT_ID_CLAIM_KEY = "userExtId";
    public static final String PREFERRED_USERNAME_CLAIM_KEY = "preferred_username";
    public static final String USER_ROLES_CLAIM_KEY = "userroles";
    public static final String KPI_UUID_KEY = "uuid";
    public static final String KPI_TIMESTAMP_KEY = "ts";
    public static final String KPI_TYPE_KEY = "type";
    public static final String KPI_DETAILS = "details";
    public static final String KPI_COUNTRY = "country";
    public static final String KPI_USED_KEY_IDENTIFIER = "usedKID";
    public static final String KPI_DELIVERY_INFO = "delivery";
    public static final String KPI_SYSTEM_UI = "ui";

    // Time, date and relevant formatting
    public static final LocalDate MIN_DATE_OF_BIRTH = LocalDate.of(1900, Month.JANUARY, 1);
    public static final LocalDate MAX_DATE_OF_BIRTH = LocalDate.of(2099, Month.DECEMBER, 31);
    public static final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter LOG_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static final ZoneId SWISS_TIMEZONE = ZoneId.of("Europe/Zurich");

    // Errors
    public static final CreateCertificateError MISSING_PROPERTY = new CreateCertificateError(432, "Property %s is missing!", HttpStatus.BAD_REQUEST);

    public static final CreateCertificateError NO_VACCINATION_DATA = new CreateCertificateError(451, "No vaccination data was specified", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError NO_PERSON_DATA = new CreateCertificateError(452, "No person data was specified", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_DATE_OF_BIRTH = new CreateCertificateError(453, "Invalid dateOfBirth! Must be younger than 1900-01-01", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_MEDICINAL_PRODUCT = new CreateCertificateError(454, "Invalid medicinal product", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_DOSES = new CreateCertificateError(455, "Invalid number of doses", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_VACCINATION_DATE = new CreateCertificateError(456, "Invalid vaccination date! Date cannot be in the future", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_COUNTRY_OF_VACCINATION = new CreateCertificateError(457, "Invalid country of vaccination", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_GIVEN_NAME = new CreateCertificateError(458, "Invalid given name! Must not exceed 80 chars and/or not contain any invalid chars", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_FAMILY_NAME = new CreateCertificateError(459, "Invalid family name! Must not exceed 80 chars and/or not contain any invalid chars", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError NO_TEST_DATA = new CreateCertificateError(460, "No test data was specified", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_MEMBER_STATE_OF_TEST = new CreateCertificateError(461, "Invalid member state of test", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_TYP_OF_TEST = new CreateCertificateError(462, "Invalid type of test and manufacturer code combination! Must either be a PCR Test type and no manufacturer code or give a manufacturer code and the antigen test type code.", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError ONLY_RAPID_TEST_SUPPORTED = new CreateCertificateError(462, "Only rapid antigen test is supported.", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_TEST_CENTER = new CreateCertificateError(463, "Invalid testing center or facility", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_SAMPLE_DATE_TIME = new CreateCertificateError(464, "Invalid sample date time! Sample date must be before current date time", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError NO_RECOVERY_DATA = new CreateCertificateError(465, "No recovery data specified", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT = new CreateCertificateError(466, "Invalid date of first positive test result", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_COUNTRY_OF_TEST = new CreateCertificateError(467, "Invalid country of test", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_LANGUAGE = new CreateCertificateError(469, "The given language does not match any of the supported languages: de, it, fr, rm!", HttpStatus.BAD_REQUEST);

    public static final RevocationError INVALID_UVCI = new RevocationError(470, "Invalid UVCI format.", HttpStatus.BAD_REQUEST);
    public static final RevocationError INVALID_SIZE_OF_UVCI_LIST = new RevocationError(472, "Invalid size of UVCI List.", HttpStatus.BAD_REQUEST);
    public static final RevocationError INVALID_FRAUD_FLAG = new RevocationError(473, "No fraud flag was specified.", HttpStatus.BAD_REQUEST);

    public static final CreateCertificateError INVALID_ADDRESS = new CreateCertificateError(474, "Paper-based delivery requires a valid address.", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError DUPLICATE_DELIVERY_METHOD = new CreateCertificateError(475, "Delivery method can either be InApp or print, but not both.", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError UNKNOWN_APP_CODE = new CreateCertificateError(476, "Unknown app code.", HttpStatus.NOT_FOUND);
    public static final CreateCertificateError INVALID_STANDARDISED_GIVEN_NAME = new CreateCertificateError(477, "Invalid given name! The standardised given name exceeds 80 chars", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_STANDARDISED_FAMILY_NAME = new CreateCertificateError(478, "Invalid family name! The standardised family name exceeds 80 chars", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_APP_CODE = new CreateCertificateError(479, "App code is in an invalid format.", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_PRINT_FOR_TEST = new CreateCertificateError(488, "Print is not available for test certificates", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_DATE_OF_BIRTH_IN_FUTURE = new CreateCertificateError(489, "Invalid dateOfBirth! Date cannot be in the future", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError NO_ANTIBODY_DATA = new CreateCertificateError(490, "No antibody data specified", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_ANTIBODY_SAMPLE_DATE_TIME = new CreateCertificateError(491, "Date of sample collection must not be before 16.11.2021", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError NO_EXCEPTIONAL_INFO = new CreateCertificateError(492,
                                                                                                "No exceptional data specified",
                                                                                                HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_EXCEPTIONAL_VALID_FROM_DATE = new CreateCertificateError(493,
                                                                                                                "Invalid date for valid from field",
                                                                                                                HttpStatus.BAD_REQUEST);

    public static final CreateCertificateError DATE_CANT_BE_BEFORE = new CreateCertificateError(494, "Date can't be before %s!", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError DATE_CANT_BE_AFTER = new CreateCertificateError(495, "Date can't be after %s!", HttpStatus.BAD_REQUEST);

    public static final CreateCertificateError TEXT_INVALID_LENGTH = new CreateCertificateError(496, "Length of property '%s'(string) can't exceed %d characters!", HttpStatus.BAD_REQUEST);

    public static final RevocationError DUPLICATE_UVCI = new RevocationError(480, "Duplicate UVCI.", HttpStatus.CONFLICT);

    public static final CsvError INVALID_CSV = new CsvError(481, "The CSV can not be read!", HttpStatus.BAD_REQUEST);
    public static final CsvError INVALID_CSV_SIZE = new CsvError(482, "The CSV has an invalid size! Must contain 1 to 100 entries.", HttpStatus.BAD_REQUEST);
    public static final CsvError NOT_A_CSV = new CsvError(483, "The sent file is not a CSV file.", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_CERTIFICATE_TYPE = new CreateCertificateError(484, "Invalid certificate type! 'vaccination', 'test', and 'recovery' are allowed", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_CREATE_REQUESTS = new CreateCertificateError(485, "One or more of the requests in the CSV contain invalid data. For more detailed error messages check the returned CSV", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_APP_CODE_CHECKSUM = new CreateCertificateError(486, "Invalid app code, check input.", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError INVALID_APP_CODE_LENGTH = new CreateCertificateError(487, "Incorrect input, the code consists of 9 characters.", HttpStatus.BAD_REQUEST);

    public static final FeatureToggleError FEATURE_DEACTIVATED = new FeatureToggleError(488, "Feature with uri %s is deactivated", HttpStatus.FORBIDDEN);

    public static final AuthorizationError NO_FUNCTION_CONFIGURED = new AuthorizationError(489, "Function with uri %s is not configured", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final AuthorizationError TOO_MANY_FUNCTIONS_CONFIGURED = new AuthorizationError(490, "Function with uri %s and HTTP-Method %s is configured more then once", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final AuthorizationError FORBIDDEN = new AuthorizationError(491, "No sufficient roles for feature with uri %s", HttpStatus.FORBIDDEN);
    public static final AuthorizationError ACCESS_DENIED_FOR_HIN_WITH_CH_LOGIN = new AuthorizationError(492, "Access denied for HIN with CH-Login", HttpStatus.FORBIDDEN);


    public static final RevocationError ALREADY_REVOKED_UVCI = new RevocationError(496, "Uvci is already revoked.",
                                                                                   HttpStatus.BAD_REQUEST);
    public static final RevocationError DUPLICATE_UVCI_IN_REQUEST = new RevocationError(497,
                                                                                        "Same UVCI is duplicated in request.",
                                                                                        HttpStatus.BAD_REQUEST);

    public static final CreateCertificateError CREATE_COSE_PROTECTED_HEADER_FAILED = new CreateCertificateError(550,
                                                                                                                "Creating COSE protected header failed.",
                                                                                                                HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError CREATE_COSE_PAYLOAD_FAILED = new CreateCertificateError(551,
                                                                                                       "Creating COSE payload failed.",
                                                                                                       HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError CREATE_COSE_SIGNATURE_DATA_FAILED = new CreateCertificateError(552,
                                                                                                              "Creating COSE signature data failed.",
                                                                                                              HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError CREATE_SIGNATURE_FAILED = new CreateCertificateError(553,
                                                                                                    "Creating signature failed.",
                                                                                                    HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError CREATE_COSE_SIGN1_FAILED = new CreateCertificateError(554,
                                                                                                     "Creating COSE_Sign1 failed.",
                                                                                                     HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError CREATE_BARCODE_FAILED = new CreateCertificateError(555,
                                                                                                  "Creating barcode failed.",
                                                                                                  HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError PRINTING_FAILED = new CreateCertificateError(556,
                                                                                            "Printing failed due to a technical error.",
                                                                                            HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CsvError WRITING_RETURN_CSV_FAILED = new CsvError(557, "Writing CSV failed.",
                                                                          HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError APP_DELIVERY_FAILED = new CreateCertificateError(558,
                                                                                                "App delivery failed due to a technical error.",
                                                                                                HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError SIGNING_CERTIFICATE_MISSING = new CreateCertificateError(559,
                                                                                                        "No signing certificate was found.",
                                                                                                        HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError AMBIGUOUS_SIGNING_CERTIFICATE = new CreateCertificateError(560,
                                                                                                          "Ambiguous signing certificate. Multiple signing certificates were found.",
                                                                                                          HttpStatus.INTERNAL_SERVER_ERROR);

    public static final ValueSetError UNSUPPORTED_LANGUAGE = new ValueSetError(901,
                                                                               "The requested language does not match any of the supported languages: de, it, fr, rm, en!",
                                                                               HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError CREATE_PDF_FAILED = new CreateCertificateError(561,
                                                                                              "Creating PDF failed.",
                                                                                              HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError CREATE_UVCI_FAILED = new CreateCertificateError(562,
                                                                                               "Creating UVCI failed.",
                                                                                               HttpStatus.INTERNAL_SERVER_ERROR);

    public static final ConvertCertificateError CONVERSION_DTO_VALIDATION_FAILED = new ConvertCertificateError(1001,
                                                                                                               "Validation of conversion DTO failed",
                                                                                                               HttpStatus.BAD_REQUEST);
    public static final ConvertCertificateError CONVERSION_UVCI_ALREADY_REVOKED = new ConvertCertificateError(1002,
                                                                                                              "The UVCI: %s sent for conversion is already revoked.",
                                                                                                              HttpStatus.BAD_REQUEST);
    public static final ConvertCertificateError CREATE_CONVERTED_UVCI_FAILED = new ConvertCertificateError(1003,
                                                                                                           "Converting UVCI failed.",
                                                                                                           HttpStatus.INTERNAL_SERVER_ERROR);

    public static final CreateCertificateError DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE = new CreateCertificateError(1004, "Invalid dateOfBirth! Must be before the certificate date", HttpStatus.BAD_REQUEST);

    public static final String VACCINATION_TOURIST_PRODUCT_CODE_SUFFIX = "_T";

    public static final Integer EXPIRATION_PERIOD_24_MONTHS = 24;
    public static final Integer EXPIRATION_PERIOD_30_DAYS = 30;
}
