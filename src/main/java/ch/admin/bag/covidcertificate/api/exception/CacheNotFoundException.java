package ch.admin.bag.covidcertificate.api.exception;

import ch.admin.bag.covidcertificate.web.controller.CachesController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.NestedRuntimeException;

import java.util.Arrays;

public class CacheNotFoundException extends NestedRuntimeException {

    public CacheNotFoundException(IllegalArgumentException exception) {
        super(
                String.format("Cache %s not found. \n" +
                                "Available Caches: %s",
                        StringUtils.substringAfterLast(exception.getMessage(), "."),
                        Arrays.toString(CachesController.Cache.values())
                ),
                exception.getCause());
    }

}
