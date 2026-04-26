package com.laborapp.dao;

import com.laborapp.model.Assembly;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssemblyDao extends BaseDao {
    
    public List<Assembly> findAll() {
        String sql = "SELECT * FROM assembly";
        try {
            return executeQuery(sql, rs -> {
                List<Assembly> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Assembly(
                        rs.getInt("assembly_code"),
                        rs.getInt("component_code"),
                        rs.getInt("operation_number"),
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
    
    public void insert(Assembly assembly) throws SQLException {
        String sql = """
            INSERT INTO assembly (assembly_code, component_code, operation_number, used_qty)
            VALUES (?, ?, ?, ?)
            """;
        executeUpdate(sql, ps -> {
            ps.setInt(1, assembly.assemblyCode);
            ps.setInt(2, assembly.componentCode);
            ps.setInt(3, assembly.operationNumber);
            ps.setBigDecimal(4, assembly.usedQty);
        });
    }
    
    public void delete(int assemblyCode, int componentCode, int operationNumber) throws SQLException {
        String sql = """
            DELETE FROM assembly 
            WHERE assembly_code = ? AND component_code = ? AND operation_number = ?
            """;
        executeUpdate(sql, ps -> {
            ps.setInt(1, assemblyCode);
            ps.setInt(2, componentCode);
            ps.setInt(3, operationNumber);
        });
    }
}
/// package com.laborapp.dao;

// import com.laborapp.model.Assembly;

// import java.sql.SQLException;
// import java.util.ArrayList;
// import java.util.List;

// public class AssemblyDao extends BaseDao {
    
//     public List<Assembly> findAll() {
//         String sql = "SELECT * FROM assembly";
//         try {
//             return executeQuery(sql, rs -> {
//                 List<Assembly> list = new ArrayList<>();
//                 while (rs.next()) {
//                     list.add(new Assembly(
//                         rs.getInt("assembly_code"),
//                         rs.getInt("component_code"),
//                         rs.getInt("operation_number"),
//                         rs.getBigDecimal("used_qty")
//                     ));
//                 }
//                 return list;
//             });
//         } catch (SQLException e) {
//             e.printStackTrace();
//             return new ArrayList<>();
//         }
//     }
    
//     public void insert(Assembly assembly) throws SQLException {
//         String sql = """
//             INSERT INTO assembly (assembly_code, component_code, operation_number, used_qty)
//             VALUES (?, ?, ?, ?)
//             """;
//         executeUpdate(sql, ps -> {
//             ps.setInt(1, assembly.assemblyCode);
//             ps.setInt(2, assembly.componentCode);
//             ps.setInt(3, assembly.operationNumber);
//             ps.setBigDecimal(4, assembly.usedQty);
//         });
//     }
    
//     public void delete(int assemblyCode, int componentCode, int operationNumber) throws SQLException {
//         String sql = """
//             DELETE FROM assembly 
//             WHERE assembly_code = ? AND component_code = ? AND operation_number = ?
//             """;
//         executeUpdate(sql, ps -> {
//             ps.setInt(1, assemblyCode);
//             ps.setInt(2, componentCode);
//             ps.setInt(3, operationNumber);
//         });
//     }
// }