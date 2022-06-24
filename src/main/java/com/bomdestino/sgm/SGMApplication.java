package com.bomdestino.sgm;

import com.bomdestino.sgm.util.DBLoadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Log4j2
@SpringBootApplication
@RequiredArgsConstructor
public class SGMApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SGMApplication.class, args);
        logApplicationStartup();
    }

    private final DBLoadService dbHelper;

    @Override
    public void run(String... args) {
        dbHelper.initData();
    }

    private static void logApplicationStartup() {
        String protocol = "http";
        String serverPort = "8080";
        String contextPath = "/";
        String hostAddress = "localhost";

        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                        "\n\tApplication SGM is running! Access URLs:\n\t" +
                        "Local: \t\t{}://localhost:{}{}\n\t" +
                        "External: \t{}://{}:{}{}\n\t" +
                        "\n----------------------------------------------------------",
                protocol,
                serverPort,
                contextPath,
                protocol,
                hostAddress,
                serverPort,
                contextPath);
    }

}
