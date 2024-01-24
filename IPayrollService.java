package dao;

import java.time.LocalDate;
// import java.util.Date;
import java.util.List;
import entity.Payroll;

public interface IPayrollService {
    Payroll generatePayroll(int employeeId, LocalDate startDate, LocalDate endDate);
    Payroll getPayrollById(int payrollId);
    List<Payroll> getPayrollsForEmployee(int employeeId);
    List<Payroll> getPayrollsForPeriod(LocalDate startDate, LocalDate endDate);
}
