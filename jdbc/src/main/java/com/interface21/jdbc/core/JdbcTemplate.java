package com.interface21.jdbc.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    private final DataSource dataSource;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void update(String sql, Object ... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);

            log.debug("query : {}", sql);

            setParameters(pstmt, params);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ignored) {}

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ignored) {}
        }
    }

    public List<Object> find(final String sql, final Class<?> resultClass) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            log.debug("query : {}", sql);

            List<Object> result = new ArrayList<>();
            while (rs.next()) {
                Constructor<?> constructor = findConstructor(resultClass, rs);
                assert constructor != null;

                Object[] objects = new Object[rs.getMetaData().getColumnCount()];
                for (int idx = 0; idx < rs.getMetaData().getColumnCount(); idx++) {
                    objects[idx] = rs.getObject(idx+1);
                }

                result.add(constructor.newInstance(objects));
            }

            return result;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignored) {}

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ignored) {}

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ignored) {}
        }
    }

    public Object findOne(final String sql, final Class<?> resultClass, final Object ... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParameters(pstmt, params);
            rs = pstmt.executeQuery();

            log.debug("query : {}", sql);

            if (rs.next()) {
                Constructor<?> constructor = findConstructor(resultClass, rs);
                if (constructor == null) {
                    throw new RuntimeException();
                }

                Object[] objects = new Object[rs.getMetaData().getColumnCount()];
                for (int idx = 0; idx < rs.getMetaData().getColumnCount(); idx++) {
                    objects[idx] = rs.getObject(idx+1);
                }

                return constructor.newInstance(objects);
            }
            return null;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignored) {}

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ignored) {}

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ignored) {}
        }
    }

    private void setParameters(PreparedStatement pstmt, Object ... params) throws SQLException {
        for (int idx = 0; idx < params.length; idx++) {
            Object param = params[idx];
            String typeName = param.getClass().getTypeName();

            if (typeName.equals(String.class.getTypeName())) {
                pstmt.setString(idx +1, (String) param);
                return;
            }
            if (typeName.equals(Integer.class.getTypeName()) || typeName.equals(int.class.getTypeName())) {
                pstmt.setInt(idx +1, (int) param);
                return;
            }
            if (typeName.equals(Double.class.getTypeName()) || typeName.equals(double.class.getTypeName())) {
                pstmt.setDouble(idx +1, (double) param);
                return;
            }
            if (typeName.equals(Long.class.getTypeName()) || typeName.equals(long.class.getTypeName())) {
                pstmt.setLong(idx +1, (long) param);
                return;
            }

            pstmt.setObject(idx + 1, param);
        }
    }

    private Constructor<?> findConstructor(Class<?> resultClass, ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        for (Constructor<?> constructor : resultClass.getConstructors()) {
            if (constructor.getParameterCount() == metaData.getColumnCount()) {
                return constructor;
            }
        }
        return null;
    }
}
