package com.example.helloworld.controllers;

import com.example.helloworld.models.Message;
import com.example.helloworld.models.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.security.Principal;
//import java.sql.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;

@Controller
public class QueryController {
    private static final Logger log = LoggerFactory.getLogger(QueryController.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/secured/room")
    public void sendSpecific(
        @Payload Message msg,
        Principal user,
        @Header("simpSessionId") String sessionId) throws Exception {
        log.info("Initializing query {} from user {}", msg.getText(), user.getName());

        try (Connection connection = DriverManager.getConnection("jdbc:flink://localhost:64356")) {
            try (Statement statement = connection.createStatement()) {
                boolean hasResultSet = statement.execute(msg.getText());

                if (hasResultSet) {
                    try (ResultSet rs = statement.getResultSet()) {
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();

                        List<String> colNames = new ArrayList<>();
                        for (int i = 1; i <= columnCount; i++) {
                            colNames.add(metaData.getColumnName(i));
                        }

                        while (rs.next()) {
                            List<QueryResult.QueryResultRow> rows = new ArrayList<>();
                            List<String> values = new ArrayList<>();
                            for (int i = 1; i <= columnCount; i++) {
                                values.add(String.valueOf(rs.getObject(i)));
                            }
                            rows.add(QueryResult.QueryResultRow.from(values));
                            QueryResult result = QueryResult.from(colNames, rows);
                            simpMessagingTemplate.convertAndSendToUser(
                                user.getName(), "/secured/user/queue/specific-user", result);
                        }
                    }
//                    // Send a special message to indicate the end of results
//                    simpMessagingTemplate.convertAndSendToUser(
//                        user.getName(), "/secured/user/queue/specific-user", "END_OF_RESULTS");
                } else {
                    // Statement didn't return a ResultSet (e.g., it was an update or DDL statement)
                    QueryResult result = QueryResult.from(Collections.emptyList(), Collections.emptyList());
                    simpMessagingTemplate.convertAndSendToUser(
                        user.getName(), "/secured/user/queue/specific-user", result);
                }
            }
        } catch (SQLException e) {
            // Handle SQL exceptions and communicate the error back to the user
            e.printStackTrace(); // Log the exception for debugging purposes
            log.info("Query failed: {}", e.getMessage());
            throw e;
        }
    }

}