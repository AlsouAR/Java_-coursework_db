package com.laborapp.service;

import com.laborapp.dao.*;
import com.laborapp.model.*;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class LaborService {

    // DAO для ваших таблиц
    private final DetailDao detailDao = new DetailDao();
    private final LaborNormDao laborNormDao = new LaborNormDao();
    private final WorkerDao workerDao = new WorkerDao();
    private final WorkRecordDao workRecordDao = new WorkRecordDao();
    private final AssemblyDao assemblyDao = new AssemblyDao();

    // ========== Методы для таблиц ==========
    
    // Детали
    public List<Detail> getAllDetails() {
        return detailDao.findAll();
    }
    
    // Нормы труда
    public List<LaborNorm> getAllLaborNorms() {
        return laborNormDao.findAll();
    }
    
    // Рабочие
    public List<Worker> getAllWorkers() {
        return workerDao.findAll();
    }
    
    // Учёт выработки
    public List<WorkRecord> getAllWorkRecords() {
        return workRecordDao.findAll();
    }
    
    // Сборка
    public List<Assembly> getAllAssemblies() {
        return assemblyDao.findAll();
    }
    
    // ========== CRUD операции ==========
    
    // Workers
    public int insertWorker(Worker worker) throws SQLException {
        return workerDao.insert(worker);
    }
    
    public void updateWorker(Worker worker) throws SQLException {
        workerDao.update(worker);
    }
    
    public void deleteWorker(int workerId) throws SQLException {
        workerDao.delete(workerId);
    }
    
    // Details
    public void insertDetail(Detail detail) throws SQLException {
        detailDao.insert(detail);
    }
    
    public void updateDetail(Detail detail) throws SQLException {
        detailDao.update(detail);
    }
    
    public void deleteDetail(int detailCode) throws SQLException {
        detailDao.delete(detailCode);
    }
    
    // Labor Norms
    public void insertLaborNorm(LaborNorm norm) throws SQLException {
        laborNormDao.insert(norm);
    }
    
    public void updateLaborNorm(LaborNorm norm, int oldDetailCode, int oldOperationNumber) 
            throws SQLException {
        laborNormDao.update(norm, oldDetailCode, oldOperationNumber);
    }
    
    public void deleteLaborNorm(int detailCode, int operationNumber) throws SQLException {
        laborNormDao.delete(detailCode, operationNumber);
    }
    
    // Work Records
    public void insertWorkRecord(WorkRecord record) throws SQLException {
        workRecordDao.insert(record);
    }
    
    public void updateWorkRecord(WorkRecord record, int oldWorkerId, int oldDetailCode, 
                                 int oldOperationNumber, Date oldWorkDate) throws SQLException {
        workRecordDao.update(record, oldWorkerId, oldDetailCode, oldOperationNumber, oldWorkDate);
    }
    
    public void deleteWorkRecord(int workerId, int detailCode, int operationNumber, Date workDate) 
            throws SQLException {
        workRecordDao.delete(workerId, detailCode, operationNumber, workDate);
    }
    
    // Assembly
    public void insertAssembly(Assembly assembly) throws SQLException {
        assemblyDao.insert(assembly);
    }
    
    public void deleteAssembly(int assemblyCode, int componentCode, int operationNumber) 
            throws SQLException {
        assemblyDao.delete(assemblyCode, componentCode, operationNumber);
    }
    
    // ========== Методы для задач курсовой ==========
    
    // Задача 1: Детали для сборки заданного изделия
    public List<DetailForAssembly> getDetailsForAssembly(int assemblyCode) {
        return detailDao.findDetailsForAssembly(assemblyCode);
    }
    
    // Задача 2: Статистика по нормам времени (РЕАЛИЗОВАННЫЙ)
    public List<LaborNormStat> getLaborNormStats() {
        return laborNormDao.findNormStats();
    }
    
    // Задача 3: Детали с проверкой условий (РЕАЛИЗОВАННЫЙ)
    public List<Detail> findDetailsWithCondition(int minQuantity, int minOperationGrade) {
        return detailDao.findDetailsWithCondition(minQuantity, minOperationGrade);
    }
    
    // ========== Дополнительные методы ==========
    
    // Проверка квалификации рабочего
    public boolean checkWorkerQualification(int workerId, int detailCode, int operationNumber) {
        try {
            return workerDao.checkQualification(workerId, detailCode, operationNumber);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Получение статистики по детали (РЕАЛИЗОВАННЫЙ)
    public DetailStats getDetailStats(int detailCode) {
        try {
            return detailDao.getDetailStats(detailCode);
        } catch (SQLException e) {
            e.printStackTrace();
            return new DetailStats(0, 0, 0.0);
        }
    }
}