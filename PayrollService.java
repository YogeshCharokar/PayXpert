package entity;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
// import java.util.Scanner;
import dao.IPayrollService;

public class PayrollService implements IPayrollService {
    private List<Payroll> payrollList;

    public PayrollService() {
        this.payrollList = new ArrayList<>();
    }

    @Override
    public Payroll generatePayroll(int employeeId, LocalDate startDate, LocalDate endDate) {
        Payroll payroll = new Payroll();
        payrollList.add(payroll);

        return payroll;
    }

    @Override
    public Payroll getPayrollById(int payrollId) {
        Payroll payroll = null;
        try (Connection connection = DatabaseContext.getConnection()) {
            String sql = "SELECT * FROM Payroll WHERE PayrollID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, payrollId);
    
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int employeeId = resultSet.getInt("EmployeeID");
                        Date startDate = resultSet.getDate("PayPeriodStartDate");
                        Date endDate = resultSet.getDate("PayPeriodEndDate");
                        double basicSalary = resultSet.getDouble("BasicSalary");
                        double overtimePay = resultSet.getDouble("OvertimePay");
                        double deductions = resultSet.getDouble("Deductions");
                        double netSalary = resultSet.getDouble("NetSalary");

                        payroll = new Payroll(payrollId, employeeId, startDate.toLocalDate(), endDate.toLocalDate(),
                                basicSalary, overtimePay, deductions, netSalary);
                        payroll.setPayrollID(payrollId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving payroll details from the database.");
        }
    
        return payroll;
    }

    @Override
    public List<Payroll> getPayrollsForEmployee(int employeeId) {
        List<Payroll> employeePayrolls = new ArrayList<>();
        try (Connection connection = DatabaseContext.getConnection()) {
            String sql = "SELECT * FROM Payroll WHERE EmployeeID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employeeId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int payrollId = resultSet.getInt("PayrollID");
                        Date startDate = resultSet.getDate("PayPeriodStartDate");
                        Date endDate = resultSet.getDate("PayPeriodEndDate");
                        double basicSalary = resultSet.getDouble("BasicSalary");
                        double overtimePay = resultSet.getDouble("OvertimePay");
                        double deductions = resultSet.getDouble("Deductions");
                        double netSalary = resultSet.getDouble("NetSalary");

                        Payroll payroll = new Payroll(payrollId, employeeId, startDate.toLocalDate(), endDate.toLocalDate(),
                                basicSalary, overtimePay, deductions, netSalary);
                        payroll.setPayrollID(payrollId);
                        employeePayrolls.add(payroll);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving payrolls for employee from the database.");
        }

        return employeePayrolls;
    }


    @Override
    public List<Payroll> getPayrollsForPeriod(LocalDate startDate, LocalDate endDate) {
        List<Payroll> payrolls = new ArrayList<>();
        try (Connection connection = DatabaseContext.getConnection()) {
            String sql = "SELECT * FROM Payroll WHERE PayPeriodStartDate >= ? AND PayPeriodEndDate <= ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setDate(1, Date.valueOf(startDate));
                preparedStatement.setDate(2, Date.valueOf(endDate));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int payrollId = resultSet.getInt("PayrollID");
                        int employeeId = resultSet.getInt("EmployeeID");
                        LocalDate payPeriodStartDate = resultSet.getDate("PayPeriodStartDate").toLocalDate();
                        LocalDate payPeriodEndDate = resultSet.getDate("PayPeriodEndDate").toLocalDate();
                        double basicSalary = resultSet.getDouble("BasicSalary");
                        double overtimePay = resultSet.getDouble("OvertimePay");
                        double deductions = resultSet.getDouble("Deductions");
                        double netSalary = resultSet.getDouble("NetSalary");

                        Payroll payroll = new Payroll(payrollId, employeeId, payPeriodStartDate, payPeriodEndDate,
                                basicSalary, overtimePay, deductions, netSalary);
                        payroll.setPayrollID(payrollId);
                        payrolls.add(payroll);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving payrolls for the period from the database.");
        }

        return payrolls;
    }
}