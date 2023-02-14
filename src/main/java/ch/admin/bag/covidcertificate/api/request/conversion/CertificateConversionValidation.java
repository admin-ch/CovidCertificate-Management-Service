package ch.admin.bag.covidcertificate.api.request.conversion;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CertificateConversionValidation implements
        ConstraintValidator<CertificateConversionConstraint, ConversionReason> {

    @Override
    public void initialize(CertificateConversionConstraint conversionReason) {
    }

    @Override
    public boolean isValid(ConversionReason conversionReason,
                           ConstraintValidatorContext cxt) {
        if (conversionReason == null) return false;
        return conversionReason == ConversionReason.VACCINATION_CONVERSION;
    }
}