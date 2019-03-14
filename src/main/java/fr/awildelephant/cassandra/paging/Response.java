package fr.awildelephant.cassandra.paging;

import java.util.ArrayList;
import java.util.List;

public class Response {

    private final List<String> rows = new ArrayList<>();

    private String pagingState;

    public void addRow(String serializedRow) {
        this.rows.add(serializedRow);
    }

    public void setPagingState(String pagingState) {
        this.pagingState = pagingState;
    }

    public String getPagingState() {
        return pagingState;
    }

    public List<String> getRows() {
        return rows;
    }
}
