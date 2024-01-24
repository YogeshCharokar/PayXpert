package dao;

import java.sql.Date;
import java.util.List;

import entity.FinancialRecord;

public interface IFinancialRecordService {
    void addFinancialRecord(int employeeId, String description, double amount, String recordType);

    FinancialRecord getFinancialRecordById(int recordId);

    List<FinancialRecord> getFinancialRecordsForEmployee(int employeeId);

    List<FinancialRecord> getFinancialRecordsForDate(Date recordDate);
}
