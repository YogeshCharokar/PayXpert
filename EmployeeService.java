package entity;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dao.IEmployeeService;
import exception.EmployeeNotFoundException;

public class EmployeeService implements IEmployeeService {
    private List<Employee> employeeList;
    public EmployeeService() {
        this.employeeList = new ArrayList<>();
    }

	@Override
    public Employee getEmployeeById(int employeeId) {
        for (Employee employee : employeeList) {
            if (employee.getEmployeeID() == employeeId) {
                return employee;
            }
        }
        return null;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeList;
    }

    @Override
    public void addEmployee(Employee employee) {
        employeeList.add(employee);
    }

    public void updateEmployee(int employeeId, Employee updatedEmployeeData) throws EmployeeNotFoundException {
        try (Connection connection = DatabaseContext.getConnection()) {
            String sql = "SELECT * FROM Employee WHERE EmployeeID = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
                selectStatement.setInt(1, employeeId);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String updateSql = "UPDATE Employee SET FirstName = ?, LastName = ?, DateOfBirth = ?, Gender = ?, " +
                                           "Email = ?, PhoneNumber = ?, Address = ?, Position = ?, JoiningDate = ?, TerminationDate = ? " +
                                           "WHERE EmployeeID = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                            updateStatement.setString(1, updatedEmployeeData.getFirstName());
                            updateStatement.setString(2, updatedEmployeeData.getLastName());
                            updateStatement.setDate(3, Date.valueOf(updatedEmployeeData.getDateOfBirth()));
                            updateStatement.setString(4, String.valueOf(updatedEmployeeData.getGender()));
                            updateStatement.setString(5, updatedEmployeeData.getEmail());
                            updateStatement.setString(6, updatedEmployeeData.getPhoneNumber());
                            updateStatement.setString(7, updatedEmployeeData.getAddress());
                            updateStatement.setString(8, updatedEmployeeData.getPosition());
                            updateStatement.setDate(9, Date.valueOf(updatedEmployeeData.getJoiningDate()));
                            updateStatement.setDate(10, Date.valueOf(updatedEmployeeData.getTerminationDate()));
                            // if (updatedEmployeeData.getTerminationDate() != null) {
                            //     updateStatement.setDate(10, Date.valueOf(updatedEmployeeData.getTerminationDate()));
                            // } else {
                            //     updateStatement.setNull(10, Types.DATE);
                            // }
                            updateStatement.setInt(11, employeeId);

                            int rowsAffected = updateStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Employee details updated successfully for employee ID: " + employeeId);
                            } else {
                                System.out.println("Failed to update employee details for employee ID: " + employeeId);
                            }
                        }
                    } else {
                        throw new EmployeeNotFoundException("Employee with ID " + employeeId + " not found.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating employee details in the database.");
        }
    }

    @Override
    public void removeEmployee(int employeeId) {
        Employee employeeToRemove = null;
        for (Employee employee : employeeList) {
            if (employee.getEmployeeID() == employeeId) {
                employeeToRemove = employee;
                break;
            }
        }
        if (employeeToRemove != null) {
            employeeList.remove(employeeToRemove);
        }
    }
}
