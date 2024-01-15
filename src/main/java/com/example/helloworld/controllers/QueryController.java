package com.example.helloworld.controllers;

import com.example.helloworld.models.Message;
import com.example.helloworld.models.QueryError;
import com.example.helloworld.models.QueryResult;
import com.example.helloworld.models.QuerySuccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

        try (Connection connection = DriverManager.getConnection("jdbc:flink://localhost:60824")) {
            try (Statement statement = connection.createStatement()) {
                String[] userStatements = msg.getText().strip().split(";");
                for (String userStatement: userStatements) {
                    executeQuery(userStatement, statement, user);
                }

                simpMessagingTemplate.convertAndSendToUser(
                    user.getName(), "/secured/user/queue/specific-user", new QuerySuccess());
            }
        } catch (Exception e) {
            // Handle SQL exceptions and communicate the error back to the user
            e.printStackTrace();
            log.info("Query failed: {}", e.getMessage());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            simpMessagingTemplate.convertAndSendToUser(
                user.getName(), "/secured/user/queue/specific-user", QueryError.from(sw.toString()));
        }
    }

    private void executeQuery(String query, Statement statement, Principal user) throws SQLException {
        boolean hasResultSet = statement.execute(query);

        if (hasResultSet) {
            try (ResultSet rs = statement.getResultSet()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                List<String> colNames = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    colNames.add(metaData.getColumnName(i));
                }

                // handle INSERT INTO statements which return job id
                if (colNames.size() == 1 && colNames.get(0).equals("job id")) {
                    return;
                }

                // send initial with no rows in case result has no rows
                if (colNames.size() > 0) {
                    simpMessagingTemplate.convertAndSendToUser(
                        user.getName(),
                        "/secured/user/queue/specific-user",
                        QueryResult.from(colNames, new ArrayList<>()));
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
        }
    }
}