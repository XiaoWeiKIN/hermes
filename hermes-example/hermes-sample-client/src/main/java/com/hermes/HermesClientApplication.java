package com.hermes;

import com.bolt.transport.Client;
import com.hermes.api.SimpleRequestBody;
import com.hermes.api.SimpleResponseBody;
import com.hermes.autoconfigure.EnableHermes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableHermes
@RestController
public class HermesClientApplication {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Client client;

    public static void main(String[] args) {
        SpringApplication.run(HermesClientApplication.class, args);
    }

    @GetMapping("/hermes/rest")
    public void rest() {
        SimpleRequestBody requestBody = new SimpleRequestBody("criss", 25, 17731352346L);
        SimpleResponseBody responseBody = client.request(requestBody);
        logger.info("Client Recv: " + responseBody.toString());
    }
}
