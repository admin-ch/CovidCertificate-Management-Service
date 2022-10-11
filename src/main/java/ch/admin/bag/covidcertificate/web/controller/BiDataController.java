package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.response.BiDataResponseDto;
import ch.admin.bag.covidcertificate.service.BiDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/bi-data/{fromDate}/{toDate}")
@RequiredArgsConstructor
@Slf4j
public class BiDataController {

    private final BiDataService biDataService;

    @GetMapping()
    public BiDataResponseDto loadBiData(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        return biDataService.loadBiData(fromDate, toDate);
    }
}
