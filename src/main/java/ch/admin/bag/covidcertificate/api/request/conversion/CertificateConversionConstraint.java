package ch.admin.bag.covidcertificate.api.request.conversion;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CertificateConversionValidation.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CertificateConversionConstraint {
    String message() default "Validation of conversion DTO failed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}