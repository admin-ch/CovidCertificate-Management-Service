package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.Issuable;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_VACCINATION;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEDICINAL_PRODUCT;

@Service
@Slf4j
@RequiredArgsConstructor
public class CovidCertificateVaccinationValidationService {

    private static final String SWITZERLAND = "CH";

    private final ValueSetsService valueSetsService;

    public void validateProductAndCountry(VaccinationCertificateCreateDto createDto) {
        final boolean isCountryCH = SWITZERLAND.equalsIgnoreCase(createDto.getVaccinationInfo().get(0).getCountryOfVaccination());
        final String productCode = createDto.getVaccinationInfo().get(0).getMedicinalProductCode();

        switch (createDto.getSystemSource()) {
            case WebUI: {
                var issuableVaccine = retrieveProduct(productCode, valueSetsService.getWebUiIssuableVaccines());
                throwExceptionIfIssuableIsViolated(isCountryCH, issuableVaccine.getIssuable());
                break;
            }
            case ApiGateway:
            case CsvUpload: {
                if (!isCountryCH) {
                    throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
                }
                var issuableVaccine = retrieveProduct(productCode, valueSetsService.getApiGatewayIssuableVaccines());
                throwExceptionIfIssuableIsViolated(true, issuableVaccine.getIssuable());
                break;
            }
            case ApiPlatform: {
                // this source requires foreign countries
                if (isCountryCH) {
                    throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
                }
                break;
            }
            default:
                throw new IllegalStateException("Attribute systemSource is invalid. Check Request implementation and/or Dto Validation.");
        }
    }

    private IssuableVaccineDto retrieveProduct(String productCode, List<IssuableVaccineDto> issuableVaccineDtoList) {
        var issuableVaccinesOpt = issuableVaccineDtoList.stream()
                .filter(issuableVaccine -> issuableVaccine.getProductCode().equals(productCode)).findFirst();
        // the product is not available for this source
        if (issuableVaccinesOpt.isEmpty()) {
            throw new CreateCertificateException(INVALID_MEDICINAL_PRODUCT);
        }
        return issuableVaccinesOpt.get();
    }

    private void throwExceptionIfIssuableIsViolated(boolean isCountryCH, Issuable issuable) {
        switch(issuable) {
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
