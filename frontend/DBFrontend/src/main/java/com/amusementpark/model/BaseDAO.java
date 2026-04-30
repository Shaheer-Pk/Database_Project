package com.amusementpark.model;

import com.amusementpark.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BaseDAO: provides generic helpers for all DAOs.
 * Every DAO extends this — no repeated boilerplate.
 *
 * @param <T> The model type this DAO handles.
 */
public abstract class BaseDAO<T> {

    protected Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Execute INSERT / UPDATE / DELETE.
     * Returns generated key for INSERT (or -1 for UPDATE/DELETE).
     */
    protected int executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindParams(ps, params);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    /**
     * Execute SELECT and map each row into a model via mapRow().
     */
    protected List<T> executeQuery(String sql, Object... params) throws SQLException {
        List<T> results = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    /**
     * Execute SELECT and return a single scalar (e.g., COUNT, SUM).
     */
    protected <R> R executeScalar(String sql, Class<R> type, Object... params) throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return type.cast(rs.getObject(1));
                }
            }
        }
        return null;
    }

    /** Bind parameters, correctly handling null values. */
    private void bindParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                ps.setNull(i + 1, Types.NULL);
            } else {
                ps.setObject(i + 1, params[i]);
            }
        }
    }

    /** Each DAO must implement this to map a ResultSet row to its model. */
    protected abstract T mapRow(ResultSet rs) throws SQLException;
}
