package com.laborapp.dao;

import com.laborapp.model.Worker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WorkerDao extends BaseDao {
    
    public List<Worker> findAll() {
        String sql = "SELECT * FROM workers";
        try {
            return executeQuery(sql, rs -> {
                List<Worker> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Worker(
                        rs.getInt("worker_id"),
                        rs.getInt("shop_number"),
                        rs.getInt("section_number"),
                        rs.getInt("profession_code"),
                        rs.getInt("grade"),
                        rs.getString("marital_status"),
                        rs.getString("full_name")
                    ));
                }
                return list;
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public int insert(Worker worker) throws SQLException {
        String sql = """
            INSERT INTO workers (shop_number, section_number, profession_code, 
                               grade, marital_status, full_name)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING worker_id
            """;
        return executeInsertReturning(sql,
            ps -> {
                ps.setInt(1, worker.shopNumber);
                ps.setInt(2, worker.sectionNumber);
                ps.setInt(3, worker.professionCode);
                ps.setInt(4, worker.grade);
                ps.setString(5, worker.maritalStatus);
                ps.setString(6, worker.fullName);
            },
            rs -> {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("No generated key returned");
            }
        );
    }
    
    public void update(Worker worker) throws SQLException {
        String sql = """
            UPDATE workers 
            SET shop_number = ?, section_number = ?, profession_code = ?, 
                grade = ?, marital_status = ?, full_name = ?
            WHERE worker_id = ?
            """;
        int rows = executeUpdate(sql,
            ps -> {
                ps.setInt(1, worker.shopNumber);
                ps.setInt(2, worker.sectionNumber);
                ps.setInt(3, worker.professionCode);
                ps.setInt(4, worker.grade);
                ps.setString(5, worker.maritalStatus);
                ps.setString(6, worker.fullName);
                ps.setInt(7, worker.workerId);
            });
        if (rows == 0) {
            throw new SQLException("No worker found with ID: " + worker.workerId);
        }
    }
    
    public void delete(int workerId) throws SQLException {
        String sql = "DELETE FROM workers WHERE worker_id = ?";
        executeUpdate(sql, ps -> ps.setInt(1, workerId));
    }
    
    // Проверка квалификации рабочего для операции (ИСПРАВЛЕННЫЙ)
    public boolean checkQualification(int workerId, int detailCode, int operationNumber) 
            throws SQLException {
        String sql = """
            SELECT EXISTS (
                SELECT 1 
                FROM workers w
                JOIN labor_norms ln ON w.profession_code = ln.profession_code
                WHERE w.worker_id = ? 
                  AND ln.detail_code = ? 
                  AND ln.operation_number = ?
                  AND w.grade >= ln.worker_grade
            )
            """;
        return executeQuery(sql,
            ps -> {
                ps.setInt(1, workerId);
                ps.setInt(2, detailCode);
                ps.setInt(3, operationNumber);
            },
            rs -> {
                if (rs.next()) return rs.getBoolean(1);
                return false;
            }
        );
    }
}