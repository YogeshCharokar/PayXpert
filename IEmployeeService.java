package dao;

import java.util.List;
import entity.Employee;

public interface IEmployeeService {
    Employee getEmployeeById(int employeeId);
    List<Employee> getAllEmployees();
    void addEmployee(Employee employeeData);
    void updateEmployee(int employeeId, Employee employeeData);
    void removeEmployee(int employeeId);
}
