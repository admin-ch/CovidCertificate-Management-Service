package ch.admin.bag.covidcertificate.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class AppController {

    @Autowired
    private Environment environment;

    @GetMapping(value = "/ping")
    public @ResponseBody
    String hello() {
        return String.format("Hello from %s", environment.getProperty("spring.application.name"));
    }
}
