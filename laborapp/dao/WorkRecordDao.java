package com.laborapp.dao;

import com.laborapp.model.WorkRecord;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WorkRecordDao extends BaseDao {
    
    public List<WorkRecord> findAll() {
        String sql = "SELECT * FROM work_production";
        try {
            return executeQuery(sql, rs -> {
                List<WorkRecord> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new WorkRecord(
                        rs.getInt("worker_id"),
                        rs.getDate("work_date"),
                        rs.getInt("detail_code"),
                        rs.getInt("operation_number"),
                        rs.getInt("good_qty"),
                        rs.getInt("defect_qty"),
                        rs.getBigDecimal("defect_pay_percent")
                    ));
                }
                return list;
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public void insert(WorkRecord record) throws SQLException {
        String sql = """
            INSERT INTO work_production 
            (worker_id, work_date, detail_code, operation_number, 
             good_qty, defect_qty, defect_pay_percent)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        executeUpdate(sql, ps -> {
            ps.setInt(1, record.workerId);
            ps.setDate(2, record.workDate);
            ps.setInt(3, record.detailCode);
            ps.setInt(4, record.operationNumber);
            ps.setInt(5, record.goodQty);
            ps.setInt(6, record.defectQty);
            ps.setBigDecimal(7, record.defectPayPercent);
        });
    }
    
    public void update(WorkRecord record, int oldWorkerId, int oldDetailCode,
                      int oldOperationNumber, Date oldWorkDate) throws SQLException {
        String sql = """
            UPDATE work_production 
            SET worker_id = ?, work_date = ?, detail_code = ?, operation_number = ?,
                good_qty = ?, defect_qty = ?, defect_pay_percent = ?
            WHERE worker_id = ? AND detail_code = ? AND operation_number = ? AND work_date = ?
            """;
        int rows = executeUpdate(sql, ps -> {
            ps.setInt(1, record.workerId);
            ps.setDate(2, record.workDate);
            ps.setInt(3, record.detailCode);
            ps.setInt(4, record.operationNumber);
            ps.setInt(5, record.goodQty);
            ps.setInt(6, record.defectQty);
            ps.setBigDecimal(7, record.defectPayPercent);
            // WHERE
            ps.setInt(8, oldWorkerId);
            ps.setInt(9, oldDetailCode);
            ps.setInt(10, oldOperationNumber);
            ps.setDate(11, oldWorkDate);
        });
        if (rows == 0) {
            throw new SQLException("No record found for update");
        }
    }
    
    public void delete(int workerId, int detailCode, int operationNumber, Date workDate) 
            throws SQLException {
        String sql = """
            DELETE FROM work_production 
            WHERE worker_id = ? AND detail_code = ? AND operation_number = ? AND work_date = ?
            """;
        executeUpdate(sql, ps -> {
            ps.setInt(1, workerId);
            ps.setInt(2, detailCode);
            ps.setInt(3, operationNumber);
            ps.setDate(4, workDate);
        });
    }
    
    // Статистика по детали (ИСПРАВЛЕННЫЙ)
    public Object[] getDetailStatistics(int detailCode) throws SQLException {
        String sql = """
            SELECT 
                SUM(good_qty) as total_good,
                SUM(defect_qty) as total_defect,
                CASE 
                    WHEN SUM(good_qty + defect_qty) = 0 THEN 0
                    ELSE (SUM(defect_qty) * 100.0) / SUM(good_qty + defect_qty)
                END as defect_percentage
            FROM work_production 
            WHERE detail_code = ?
            """;
        return executeQuery(sql,
            ps -> ps.setInt(1, detailCode),
            rs -> {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("total_good"),
                        rs.getInt("total_defect"),
                        rs.getDouble("defect_percentage")
                    };
                }
                return new Object[]{0, 0, 0.0};
            }
        );
    }
}