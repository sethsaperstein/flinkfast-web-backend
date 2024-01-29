package com.example.helloworld.services;

import com.example.helloworld.HelloWorldApplication;
import com.example.helloworld.clients.Auth0ApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class FlinkServiceTest {
    @Autowired
    private FlinkService flinkService;

    @MockBean
    private Auth0ApiClient auth0ApiClient;

    @Test
    public void testTemplateSqlGatewayService() {
        String templatedSqlGatewayService = flinkService.templateSqlGatewayService("foo");
        String expectedTemplatedSqlGatewayService = readExpectedTemplateFile("flink-sql-gateway-svc-templated.yaml");

        assertEquals(expectedTemplatedSqlGatewayService, templatedSqlGatewayService);
    }

    @Test
    public void testTemplateFlinkDeployment() {
        String templatedFlinkDeployment = flinkService.templateFlinkDeployment("foo", false);
        String expectedTemplatedFlinkDeployment = readExpectedTemplateFile("flink-deployment-templated.yaml");

        assertEquals(expectedTemplatedFlinkDeployment, templatedFlinkDeployment);
    }

    private String readExpectedTemplateFile(String file) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(file);

        return new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
    }
}
