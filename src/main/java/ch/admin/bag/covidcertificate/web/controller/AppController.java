package ch.admin.bag.covidcertificate.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class AppController {

    @GetMapping(value = "/ping")
    public @ResponseBody
    String hello() {
        return "Hello from CH Covid Certificate Management Service";
    }
}
