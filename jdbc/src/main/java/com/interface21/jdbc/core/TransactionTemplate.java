package com.interface21.jdbc.core;

import com.interface21.dao.DataAccessException;
import com.interface21.jdbc.datasource.DataSourceUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionTemplate {

    private static final Logger log = LoggerFactory.getLogger(TransactionTemplate.class);

    private final DataSource dataSource;

    public TransactionTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T execute(Function<Connection, T> executor) {
        final var conn = DataSourceUtils.getConnection(dataSource);
        try {
            conn.setAutoCommit(false);

            T object = executor.apply(conn);

            conn.commit();
            return object;
        } catch (DataAccessException | SQLException e) {
            log.error(e.getMessage(), e);
            rollback(conn);
            throw new DataAccessException(e);
        } finally {
            release(conn);
        }
    }

    private void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e);
        }
    }

    private void release(Connection conn) {
        try {
            conn.setAutoCommit(true);
            DataSourceUtils.releaseConnection(conn, dataSource);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e);
        }
    }
}
