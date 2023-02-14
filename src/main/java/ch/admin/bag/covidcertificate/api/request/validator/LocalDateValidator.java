package ch.admin.bag.covidcertificate.api.request.validator;

import java.time.LocalDate;

public final class LocalDateValidator {

    private LocalDateValidator() {
    }
    public static boolean isDateNotInTheFuture(LocalDate date) {
        if (date == null) return true;
        LocalDate now = LocalDate.now();
        return !date.isAfter(now);
    }

    public static boolean isDateNotBeforeTheLimitDate(LocalDate date, LocalDate limitDate) {
        if (date == null) return true;
        if (limitDate == null) return true;
        return !date.isBefore(limitDate);
    }
}
