package com.laborapp.dao;

import com.laborapp.model.LaborNorm;
import com.laborapp.service.LaborNormStat;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LaborNormDao extends BaseDao {
    
    public List<LaborNorm> findAll() {
        String sql = "SELECT * FROM labor_norms";
        try {
            return executeQuery(sql, rs -> {
                List<LaborNorm> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new LaborNorm(
                        rs.getInt("detail_code"),
                        rs.getInt("operation_number"),
                        rs.getInt("profession_code"),
                        rs.getInt("worker_grade"),
                        rs.getInt("tariff_code"),
                        rs.getBigDecimal("prep_time"),
                        rs.getBigDecimal("piece_time")
                    ));
                }
                return list;
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public void insert(LaborNorm norm) throws SQLException {
        String sql = """
            INSERT INTO labor_norms (detail_code, operation_number, profession_code, 
                                    worker_grade, tariff_code, prep_time, piece_time)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        executeUpdate(sql, ps -> {
            ps.setInt(1, norm.detailCode);
            ps.setInt(2, norm.operationNumber);
            ps.setInt(3, norm.professionCode);
            ps.setInt(4, norm.workerGrade);
            ps.setInt(5, norm.tariffCode);
            ps.setBigDecimal(6, norm.prepTime);
            ps.setBigDecimal(7, norm.pieceTime);
        });
    }
    
    public void update(LaborNorm norm, int oldDetailCode, int oldOperationNumber) throws SQLException {
        String sql = """
            UPDATE labor_norms 
            SET detail_code = ?, operation_number = ?, profession_code = ?, 
                worker_grade = ?, tariff_code = ?, prep_time = ?, piece_time = ?
            WHERE detail_code = ? AND operation_number = ?
            """;
        int rows = executeUpdate(sql, ps -> {
            ps.setInt(1, norm.detailCode);
            ps.setInt(2, norm.operationNumber);
            ps.setInt(3, norm.professionCode);
            ps.setInt(4, norm.workerGrade);
            ps.setInt(5, norm.tariffCode);
            ps.setBigDecimal(6, norm.prepTime);
            ps.setBigDecimal(7, norm.pieceTime);
            ps.setInt(8, oldDetailCode);
            ps.setInt(9, oldOperationNumber);
        });
        if (rows == 0) {
            throw new SQLException("No norm found for update");
        }
    }
    
    public void delete(int detailCode, int operationNumber) throws SQLException {
        String sql = "DELETE FROM labor_norms WHERE detail_code = ? AND operation_number = ?";
        executeUpdate(sql, ps -> {
            ps.setInt(1, detailCode);
            ps.setInt(2, operationNumber);
        });
    }
    
    // РЕАЛИЗОВАННЫЙ метод для задачи 2
    public List<LaborNormStat> findNormStats() {
        String sql = """
            SELECT 
                ln.detail_code,
                ln.operation_number,
                ln.prep_time,
                ln.piece_time,
                ROUND(ln.prep_time * 100.0 / SUM(ln.prep_time + ln.piece_time) 
                      OVER (PARTITION BY ln.detail_code), 2) as prep_time_share,
                ROUND(ln.piece_time * 100.0 / SUM(ln.prep_time + ln.piece_time) 
                      OVER (PARTITION BY ln.detail_code), 2) as piece_time_share,
                MAX(ln.piece_time) OVER (PARTITION BY ln.detail_code) as max_piece_time
            FROM labor_norms ln
            ORDER BY ln.detail_code, ln.operation_number
            """;
        try {
            return executeQuery(sql, rs -> {
                List<LaborNormStat> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new LaborNormStat(
                        rs.getInt("detail_code"),
                        rs.getInt("operation_number"),
                        rs.getBigDecimal("prep_time"),
                        rs.getBigDecimal("piece_time"),
                        rs.getBigDecimal("prep_time_share"),
                        rs.getBigDecimal("piece_time_share"),
                        rs.getBigDecimal("max_piece_time")
                    ));
                }
                return list;
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}