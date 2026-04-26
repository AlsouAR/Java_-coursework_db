package com.laborapp.dao;

import com.laborapp.model.Detail;
import com.laborapp.model.DetailForAssembly;
import com.laborapp.service.DetailStats;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetailDao extends BaseDao {
    
    public List<Detail> findAll() {
        String sql = "SELECT * FROM details";
        try {
            return executeQuery(sql, rs -> {
                List<Detail> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Detail(
                        rs.getInt("detail_code"),
                        rs.getString("detail_type"),
                        rs.getString("name"),
                        rs.getString("unit"),
                        rs.getBigDecimal("planned_price"),
                        0, // operationNumber не из этой таблицы
                        0  // usedQty не из этой таблицы
                    ));
                }
                return list;
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<DetailForAssembly> findDetailsForAssembly(int assemblyCode) {
        String sql = """
            SELECT d.name, a.operation_number, d.detail_type, a.used_qty 
            FROM assembly a 
            JOIN details d ON a.component_code = d.detail_code 
            WHERE a.assembly_code = ? 
            ORDER BY a.operation_number
            """;
        try {
            return executeQuery(sql,
                ps -> ps.setInt(1, assemblyCode),
                rs -> {
                    List<DetailForAssembly> list = new ArrayList<>();
                    while (rs.next()) {
                        list.add(new DetailForAssembly(
                            rs.getString("name"),
                            rs.getInt("operation_number"),
                            rs.getString("detail_type"),
                            rs.getBigDecimal("used_qty")
                        ));
                    }
                    return list;
                });
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public void insert(Detail detail) throws SQLException {
        String sql = """
            INSERT INTO details (detail_type, name, unit, planned_price)
            VALUES (?, ?, ?, ?)
            """;
        executeUpdate(sql, ps -> {
            ps.setString(1, detail.detailType);
            ps.setString(2, detail.name);
            ps.setString(3, detail.unit);
            ps.setBigDecimal(4, detail.plannedPrice);
        });
    }
    
    public void update(Detail detail) throws SQLException {
        String sql = """
            UPDATE details 
            SET detail_type = ?, name = ?, unit = ?, planned_price = ?
            WHERE detail_code = ?
            """;
        int rows = executeUpdate(sql, ps -> {
            ps.setString(1, detail.detailType);
            ps.setString(2, detail.name);
            ps.setString(3, detail.unit);
            ps.setBigDecimal(4, detail.plannedPrice);
            ps.setInt(5, detail.detailCode);
        });
        if (rows == 0) {
            throw new SQLException("No detail found with code: " + detail.detailCode);
        }
    }
    
    public void delete(int detailCode) throws SQLException {
        String sql = "DELETE FROM details WHERE detail_code = ?";
        executeUpdate(sql, ps -> ps.setInt(1, detailCode));
    }
    
    // РЕАЛИЗОВАННЫЙ метод для задачи 3
    public List<Detail> findDetailsWithCondition(int minQuantity, int minOperationGrade) {
        String sql = """
            SELECT DISTINCT d.*
            FROM details d
            WHERE NOT EXISTS (
                SELECT 1 FROM labor_norms ln 
                WHERE ln.detail_code = d.detail_code 
                  AND ln.worker_grade < ?
                AND NOT EXISTS (
                    SELECT 1 FROM assembly a 
                    WHERE a.assembly_code = d.detail_code 
                      AND a.operation_number = ln.operation_number
                      AND a.used_qty > ?
                )
            )
            ORDER BY d.detail_code
            """;
        try {
            return executeQuery(sql,
                ps -> {
                    ps.setInt(1, minOperationGrade);
                    ps.setInt(2, minQuantity);
                },
                rs -> {
                    List<Detail> list = new ArrayList<>();
                    while (rs.next()) {
                        list.add(new Detail(
                            rs.getInt("detail_code"),
                            rs.getString("detail_type"),
                            rs.getString("name"),
                            rs.getString("unit"),
                            rs.getBigDecimal("planned_price"),
                            0, 0
                        ));
                    }
                    return list;
                });
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // РЕАЛИЗОВАННЫЙ метод для статистики
    public DetailStats getDetailStats(int detailCode) throws SQLException {
        String sql = """
            SELECT 
                COALESCE(SUM(good_qty), 0) as total_good,
                COALESCE(SUM(defect_qty), 0) as total_defect,
                CASE 
                    WHEN COALESCE(SUM(good_qty + defect_qty), 0) = 0 THEN 0
                    ELSE (COALESCE(SUM(defect_qty), 0) * 100.0) / COALESCE(SUM(good_qty + defect_qty), 1)
                END as defect_percentage
            FROM work_production 
            WHERE detail_code = ?
            """;
        return executeQuery(sql,
            ps -> ps.setInt(1, detailCode),
            rs -> {
                if (rs.next()) {
                    return new DetailStats(
                        rs.getInt("total_good"),
                        rs.getInt("total_defect"),
                        rs.getDouble("defect_percentage")
                    );
                }
                return new DetailStats(0, 0, 0.0);
            }
        );
    }
}