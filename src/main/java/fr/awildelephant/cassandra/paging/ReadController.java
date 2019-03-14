package fr.awildelephant.cassandra.paging;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReadController {

    @RequestMapping("/read")
    public Response read(@RequestParam(value = "tableName") String tableName, @RequestParam(value = "paging_state", required = false) String startFrom) {
        try (final Cluster cluster = Cluster.builder()
                                            .addContactPoint("127.0.0.1")
                                            .withQueryOptions(new QueryOptions().setFetchSize(10))
                                            .build()) {
            final Session session = cluster.connect();

            final SimpleStatement statement = new SimpleStatement(String.format("SELECT * FROM %s", tableName));

            if (!StringUtils.isEmpty(startFrom)) {
                statement.setPagingState(PagingState.fromString(startFrom));
            }

            final ResultSet resultSet = session.execute(statement);

            final Response response = new Response();

            while (resultSet.getAvailableWithoutFetching() > 0) {
                response.addRow(resultSet.one().toString());
            }

            final PagingState pagingState = resultSet.getExecutionInfo().getPagingState();
            if (pagingState != null) {
                response.setPagingState(pagingState.toString());
            }

            return response;
        }
    }
}
