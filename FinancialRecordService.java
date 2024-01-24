package entity;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dao.IFinancialRecordService;

public class FinancialRecordService implements IFinancialRecordService {
    // private List<FinancialRecord> financialRecords;

    public FinancialRecordService() {
    }

    @Override
    public void addFinancialRecord(int employeeId, String description, double amount, String recordType) {
        if (ValidationService.isPositiveNumber(amount) && ValidationService.isNotEmpty(description)) {
            try (Connection connection = DatabaseContext.getConnection()) {
                String sql = "INSERT INTO FinancialRecord (EmployeeID, RecordDate, Description, Amount, RecordType) " +
                        "VALUES (?, CURRENT_DATE, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, employeeId);
                    preparedStatement.setString(2, description);
                    preparedStatement.setDouble(3, amount);
                    preparedStatement.setString(4, recordType);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Financial record added successfully for employee ID " + employeeId);
                    } else {
                        System.out.println("Failed to add financial record for employee ID " + employeeId);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error adding financial record to the database.");
            }
        } else {
            System.out.println("Invalid financial record data. Please check the input.");
        }
    }

    @Override
    public FinancialRecord getFinancialRecordById(int recordId) {
        FinancialRecord financialRecord = null;

            try (Connection connection = DatabaseContext.getConnection()) {
                String sql = "SELECT * FROM FinancialRecord WHERE RecordID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, recordId);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            int employeeId = resultSet.getInt("EmployeeID");
                            Date recordDate = resultSet.getDate("RecordDate");
                            String description = resultSet.getString("Description");
                            double amount = resultSet.getDouble("Amount");
                            String recordType = resultSet.getString("RecordType");
                            financialRecord = new FinancialRecord(recordId, employeeId, recordDate, description, amount, recordType);
                            return financialRecord;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error retrieving financial record from the database.");
            }

            return financialRecord;
        }


    @Override
    public List<FinancialRecord> getFinancialRecordsForEmployee(int employeeId) {
        List<FinancialRecord> employeeRecords = new ArrayList<>();
        try (Connection connection = DatabaseContext.getConnection()) {
            String sql = "SELECT * FROM FinancialRecord WHERE EmployeeID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employeeId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        FinancialRecord record = createFinancialRecordFromResultSet(resultSet);
                        employeeRecords.add(record);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching financial records for employee from the database.");
        }
        return employeeRecords;
    }

    @Override
    public List<FinancialRecord> getFinancialRecordsForDate(Date recordDate) {
        List<FinancialRecord> dateRecords = new ArrayList<>();
        try (Connection connection = DatabaseContext.getConnection()) {
            String sql = "SELECT * FROM FinancialRecord WHERE RecordDate = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setDate(1, recordDate);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        FinancialRecord record = createFinancialRecordFromResultSet(resultSet);
                        dateRecords.add(record);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching financial records for date from the database.");
        }
        return dateRecords;
    }

    private FinancialRecord createFinancialRecordFromResultSet(ResultSet resultSet) throws SQLException {
        int recordID = resultSet.getInt("RecordID");
        int employeeID = resultSet.getInt("EmployeeID");
        Date recordDate = resultSet.getDate("RecordDate");
        String description = resultSet.getString("Description");
        double amount = resultSet.getDouble("Amount");
        String recordType = resultSet.getString("RecordType");

        return new FinancialRecord(recordID, employeeID, recordDate, description, amount, recordType);
    }
}
