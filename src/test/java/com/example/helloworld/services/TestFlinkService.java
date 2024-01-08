package com.example.helloworld.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TestFlinkService {
    @Autowired
    private FlinkService flinkService;

    @Test
    public void testTemplateSqlGatewayService() {
        String templatedSqlGatewayService = flinkService.templateSqlGatewayService("foo");
        String expectedTemplatedSqlGatewayService = readExpectedTemplateFile("flink-sql-gateway-svc-templated.yaml");

        assertEquals(expectedTemplatedSqlGatewayService, templatedSqlGatewayService);
    }

    @Test
    public void testTemplateFlinkDeployment() {
        String templatedFlinkDeployment = flinkService.templateFlinkDeployment("foo");
        String expectedTemplatedFlinkDeployment = readExpectedTemplateFile("flink-deployment-templated.yaml");

        assertEquals(expectedTemplatedFlinkDeployment, templatedFlinkDeployment);
    }

    private String readExpectedTemplateFile(String file) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(file);

        return new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
    }
}
