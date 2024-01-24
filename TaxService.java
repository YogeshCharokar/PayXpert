package entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dao.ITaxService;

public class TaxService implements ITaxService {
    public TaxService() {
    }
    
    @Override
    public double calculateTax(int employeeId, int taxYear) {
        double taxableIncome = 0.0;
        double taxAmount = 0.0;

        try (Connection connection = DatabaseContext.getConnection()) {
            try  {
                String sql = "SELECT BasicSalary FROM Payroll WHERE EmployeeID = ? AND YEAR(PayPeriodStartDate) = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, employeeId);
                    preparedStatement.setInt(2, taxYear);
    
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            taxableIncome = resultSet.getDouble("BasicSalary");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error calculating taxable income from the database.");
            }

            double taxRate = 0;
            if (taxableIncome <= 40000) {
                taxRate =  0.05;
            } else {
                taxRate = 0.18;
            }

            taxAmount = taxableIncome * taxRate;

            try  {
                String sql = "INSERT INTO Tax (EmployeeID, TaxYear, TaxableIncome, TaxAmount) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, employeeId);
                    preparedStatement.setInt(2, taxYear);
                    preparedStatement.setDouble(3, taxableIncome);
                    preparedStatement.setDouble(4, taxAmount);
    
                    int rowsAffected = preparedStatement.executeUpdate();
    
                    if (rowsAffected > 0) {
                        System.out.println("Tax details stored in the database for employee ID " + employeeId +
                                " for the tax year " + taxYear);
                    } else {
                        System.out.println("Failed to store tax details in the database for employee ID " + employeeId);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error storing tax details in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error calculating tax for employee from the database.");
        }
        return taxAmount;
    }

    @Override
    public Tax getTaxById(int taxId) {
        try (Connection connection = DatabaseContext.getConnection()) {
            String sql = "SELECT * FROM Tax WHERE TaxID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, taxId);
    
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int employeeId = resultSet.getInt("EmployeeID");
                        int taxYear = resultSet.getInt("TaxYear");
                        double taxableIncome = resultSet.getDouble("TaxableIncome");
                        double taxAmount = resultSet.getDouble("TaxAmount");
    
                        Tax tax = new Tax(taxId, employeeId, taxYear, taxableIncome, taxAmount);
                        return tax;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving tax details from the database.");
        }
        return null;
    }

    @Override
    public List<Tax> getTaxesForEmployee(int employeeId) {
        List<Tax> employeeTaxes = new ArrayList<>();
        
        try (Connection connection = DatabaseContext.getConnection()) {
            String sql = "SELECT * FROM Tax WHERE EmployeeID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employeeId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int taxId = resultSet.getInt("TaxID");
                        int taxYear = resultSet.getInt("TaxYear");
                        double taxableIncome = resultSet.getDouble("TaxableIncome");
                        double taxAmount = resultSet.getDouble("TaxAmount");

                        Tax tax = new Tax(taxId, employeeId, taxYear, taxableIncome, taxAmount);
                        employeeTaxes.add(tax);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving taxes for employee from the database.");
        }

        return employeeTaxes;
    }

    @Override
    public List<Tax> getTaxesForYear(int taxYear) {
        List<Tax> yearTaxes = new ArrayList<>();

        try (Connection connection = DatabaseContext.getConnection()) {
            String sql = "SELECT * FROM Tax WHERE TaxYear = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, taxYear);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int taxID = resultSet.getInt("TaxID");
                        int employeeID = resultSet.getInt("EmployeeID");
                        double taxableIncome = resultSet.getDouble("TaxableIncome");
                        double taxAmount = resultSet.getDouble("TaxAmount");

                        Tax tax = new Tax(taxID, employeeID, taxYear, taxableIncome, taxAmount);
                        yearTaxes.add(tax);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching taxes for the year " + taxYear + " from the database.");
        }

        return yearTaxes;
    }
}