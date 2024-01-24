package entity;

import java.util.List;

public class ReportGenerator {
    public void generatePayrollReport(List<Employee> employees) {
        System.out.println("Payroll Report");
        for (Employee employee : employees) {
        	Payroll payroll = employee.getPayroll() ;
        	System.out.println("Employee: " + employee.getFirstName() +" "+ employee.getLastName() + ", Salary: " + payroll.getNetSalary());
        }
    }

    public void generateTaxReport(List<Employee> employees) {
        System.out.println("Tax Report");
        for(Employee employee: employees) {
        	Tax tax = employee.getTax();
        	System.out.println("Employee: " + employee.getFirstName() +" "+ employee.getLastName() + ", Tax: " + tax.calculateTax());
        }
    }

    public void generateFinancialReport(List<FinancialRecord> financialRecords) {
        System.out.println("Financial Report");
        for(FinancialRecord financialRecord: financialRecords) {
        	System.out.println("Record Date: " + financialRecord.getRecordDate() + ", Record Type: " + financialRecord.getRecordType() + ", Amount:" + financialRecord.getAmount());
        }
    }
}
