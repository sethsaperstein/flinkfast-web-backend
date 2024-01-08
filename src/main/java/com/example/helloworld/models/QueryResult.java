package com.example.helloworld.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryResult {
    private List<String> colNames;
    private List<QueryResultRow> rows;

    static public QueryResult from(List<String> colNames, List<QueryResultRow> rows) {
        return new QueryResult(colNames, rows);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QueryResultRow {
        private List<String> values;

        static public QueryResultRow from(List<String> values) {
            return new QueryResultRow(values);
        }
    }
}
