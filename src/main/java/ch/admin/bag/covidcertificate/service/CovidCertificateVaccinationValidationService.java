package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.domain.enums.Issuable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_VACCINATION;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEDICINAL_PRODUCT;

@Service
@Slf4j
@RequiredArgsConstructor
public class CovidCertificateVaccinationValidationService {

    private final ValueSetsService valueSetsService;

    public void validateProductAndCountry(VaccinationCertificateCreateDto createDto) {
        final boolean isCountryCH = Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND.equalsIgnoreCase(createDto.getCertificateData().get(0).getCountryOfVaccination());
        final String productCode = createDto.getCertificateData().get(0).getMedicinalProductCode();

        switch (createDto.getSystemSource()) {
            case WebUI: {
                var issuableVaccine = retrieveProduct(productCode, valueSetsService.getWebUiIssuableVaccines());
                throwExceptionIfIssuableIsViolated(isCountryCH, issuableVaccine.getIssuable());
                break;
            }
            case ApiGateway, CsvUpload: {
                var issuableVaccine = retrieveProduct(productCode, valueSetsService.getApiGatewayIssuableVaccines());
                throwExceptionIfIssuableIsViolated(isCountryCH, issuableVaccine.getIssuable());
                break;
            }
            case ApiPlatform: {
                var issuableVaccine = retrieveProduct(productCode, valueSetsService.getApiPlatformIssuableVaccines());
                throwExceptionIfIssuableIsViolated(isCountryCH, issuableVaccine.getIssuable());
                break;
            }
            default:
                throw new IllegalStateException("Attribute systemSource is invalid. Check Request implementation and/or Dto Validation.");
        }
    }

    public void validateProductAndCountryForVaccinationTourist(VaccinationTouristCertificateCreateDto createDto) {
        validateCountryIsNotSwitzerland(createDto.getVaccinationTouristInfo().get(0).getCountryOfVaccination());
        // Vaccination Tourist Certificates cannot be be generated for vaccinations in Switzerland
        final String productCode = createDto.getVaccinationTouristInfo().get(0).getMedicinalProductCode();
        final IssuableVaccineDto issuableVaccine = retrieveProduct(productCode, valueSetsService.getApiGatewayIssuableVaccines());
        // Only WHO vaccines can be used for the generation of Vaccination Tourist Certificates
        if (!issuableVaccine.isTouristVaccine()) throw new CreateCertificateException(INVALID_MEDICINAL_PRODUCT);
    }

    /**
     * Validate that a given string is not blank and does not represent the Switzerland country code (case-insensitive).
     *
     * @param isoCountryCode the ISO 3166-1 Alpha-2 Code of a country
     * @throws CreateCertificateException if country code is blank or equal to {@value ch.admin.bag.covidcertificate.api.Constants#ISO_3166_1_ALPHA_2_CODE_SWITZERLAND}.
     */
    public void validateCountryIsNotSwitzerland(String isoCountryCode) {
        if (StringUtils.isBlank(isoCountryCode)) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
        }
        final boolean isCountryCH = Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND.equalsIgnoreCase(isoCountryCode);
        if (isCountryCH) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
        }
    }


    private IssuableVaccineDto retrieveProduct(String productCode, List<IssuableVaccineDto> issuableVaccineDtoList) {
        var issuableVaccinesOpt = issuableVaccineDtoList.stream()
                .filter(issuableVaccine -> issuableVaccine.getProductCode()
                        .equalsIgnoreCase(
                                productCode))
                .findFirst();
        // the product is not available for this source
        if (issuableVaccinesOpt.isEmpty()) {
            throw new CreateCertificateException(INVALID_MEDICINAL_PRODUCT);
        }
        return issuableVaccinesOpt.get();
    }

    private void throwExceptionIfIssuableIsViolated(boolean isCountryCH, Issuable issuable) {
        switch (issuable) {
            case CH_ONLY: {
                if (!isCountryCH) {
                    // a product issueable only in switzerland has been used
                    throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
                }
                break;
            }
            case ABROAD_ONLY: {
                if (isCountryCH) {
                    // a product not issueable in switzerland has been used
                    throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
                }
                break;
            }
            case CH_AND_ABROAD: {
                // a product is everywhere issuable
                break;
            }
            case UNDEFINED: {
                // issuable is not defined for this product
                throw new IllegalStateException("Issuable of Vaccines is undefined. ");
            }
        }
    }
}
