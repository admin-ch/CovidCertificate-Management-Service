package ch.admin.bag.covidcertificate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Objects;

@ServletComponentScan
@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
@EnableCaching
@Slf4j
public class CCManagementServiceApplication {

	public static void main(String[] args) {

		String filePath= Objects.requireNonNull(Thread.currentThread()
				.getContextClassLoader().getResource("truststore.jks")).getFile();
		System.setProperty("javax.net.ssl.trustStore", filePath);
		System.setProperty("javax.net.ssl.trustStorePassword","changeit");

		Environment env = SpringApplication.run(CCManagementServiceApplication.class, args).getEnvironment();

		String protocol = "http";
		if (env.getProperty("server.ssl.key-store") != null) {
			protocol = "https";
		}
		log.info("\n----------------------------------------------------------\n\t" +
						"Yeah!!! {} is running! \n\t" +
						"\n" +
						"\tSwaggerUI: \t{}://localhost:{}/swagger-ui.html\n\t" +
						"Profile(s): \t{}" +
						"\n----------------------------------------------------------",
				env.getProperty("spring.application.name"),
				protocol,
				env.getProperty("server.port"),
				env.getActiveProfiles());

	}
}
