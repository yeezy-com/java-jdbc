package com.interface21.jdbc.core;

import com.interface21.dao.DataAccessException;
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
        try (final var conn = dataSource.getConnection()) {
            try {
                conn.setAutoCommit(false);

                T object = executor.apply(conn);

                conn.commit();
                return object;
            } catch (DataAccessException | SQLException e) {
                log.error(e.getMessage(), e);
                try {
                    conn.rollback();
                } catch (SQLException sqlException) {
                    log.error(e.getMessage(), sqlException);
                    throw new DataAccessException(sqlException);
                }
                throw new DataAccessException(e);
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    log.error(e.getMessage(), e);
                    throw new DataAccessException(e);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e);
        }
    }
}
