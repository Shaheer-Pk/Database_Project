package com.amusementpark.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DashboardDAO: aggregated stats for the Dashboard view.
 * No model needed — returns primitives and BigDecimals directly.
 */
public class DashboardDAO extends BaseDAO<Object> {

    // ── REVENUE ─────────────────────────────────────────────────────────────

    /**
     * Total revenue = Card_Payment + Ticketing + Bowling_Booking + Food_Payment
     */
    public BigDecimal getTotalRevenue() throws SQLException {
        String sql = """
            SELECT
              (SELECT COALESCE(SUM(Amount), 0) FROM Card_Payment)    +
              (SELECT COALESCE(SUM(Amount), 0) FROM Ticketing)        +
              (SELECT COALESCE(SUM(Amount), 0) FROM Bowling_Booking)  +
              (SELECT COALESCE(SUM(Amount), 0) FROM Food_Payment)
            AS TotalRevenue
            """;
        Object result = executeScalar(sql, Object.class);
        return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
    }

    /**
     * Revenue breakdown by source — used to populate Dashboard chart/cards.
     * Returns a RevenueBreakdown record.
     */
    public RevenueBreakdown getRevenueBreakdown() throws SQLException {
        String sql = """
            SELECT
              (SELECT COALESCE(SUM(Amount), 0) FROM Card_Payment)   AS ride_revenue,
              (SELECT COALESCE(SUM(Amount), 0) FROM Ticketing)       AS cinema_revenue,
              (SELECT COALESCE(SUM(Amount), 0) FROM Bowling_Booking) AS bowling_revenue,
              (SELECT COALESCE(SUM(Amount), 0) FROM Food_Payment)    AS food_revenue
            """;
        // Inline query — not using executeQuery since we need multiple columns, not a list
        var conn = getConnection();
        try (var ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            if (rs.next()) {
                return new RevenueBreakdown(
                    toBigDecimal(rs, "ride_revenue"),
                    toBigDecimal(rs, "cinema_revenue"),
                    toBigDecimal(rs, "bowling_revenue"),
                    toBigDecimal(rs, "food_revenue")
                );
            }
        }
        return new RevenueBreakdown(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    // ── OPERATIONAL COUNTS ───────────────────────────────────────────────────

    public int getActiveRideCount() throws SQLException {
        // Status is BOOL (TINYINT 1=active)
        Object count = executeScalar("SELECT COUNT(*) FROM Ride WHERE Status = TRUE", Object.class);
        return count != null ? ((Number) count).intValue() : 0;
    }

    public int getTotalStaffCount() throws SQLException {
        Object count = executeScalar("SELECT COUNT(*) FROM Staff", Object.class);
        return count != null ? ((Number) count).intValue() : 0;
    }

    public int getTotalCustomerCount() throws SQLException {
        Object count = executeScalar("SELECT COUNT(*) FROM Customer", Object.class);
        return count != null ? ((Number) count).intValue() : 0;
    }

    public int getActiveFoodStallCount() throws SQLException {
        Object count = executeScalar("SELECT COUNT(*) FROM Food_Stalls", Object.class);
        return count != null ? ((Number) count).intValue() : 0;
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────

    private BigDecimal toBigDecimal(ResultSet rs, String col) throws SQLException {
        Object v = rs.getObject(col);
        return v != null ? new BigDecimal(v.toString()) : BigDecimal.ZERO;
    }

    @Override
    protected Object mapRow(ResultSet rs) {
        return null; // Not used — DashboardDAO uses direct queries only
    }

    // ── NESTED RECORD ────────────────────────────────────────────────────────

    public record RevenueBreakdown(
        BigDecimal rideRevenue,
        BigDecimal cinemaRevenue,
        BigDecimal bowlingRevenue,
        BigDecimal foodRevenue
    ) {
        public BigDecimal total() {
            return rideRevenue.add(cinemaRevenue).add(bowlingRevenue).add(foodRevenue);
        }
    }
}
