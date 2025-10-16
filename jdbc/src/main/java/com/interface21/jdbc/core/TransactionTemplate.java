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
        Connection conn = null;
        T object;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            object = executor.apply(conn);

            conn.commit();
        } catch (DataAccessException | SQLException e) {
            log.error(e.getMessage(), e);
            try {
                conn.rollback();
            } catch (SQLException sqlException) {
                throw new DataAccessException(sqlException);
            }
            throw new DataAccessException(e);
        } finally {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                throw new DataAccessException(e);
            }
        }

        return object;
    }
}
